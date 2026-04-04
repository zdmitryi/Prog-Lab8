package com.example.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.CommandInfo;
import com.example.CommandRequest;
import com.example.CommandResponse;
import com.example.commands.Command;

public class ServerNetworkManager {
    private static final Logger logger = Logger.getLogger(ServerNetworkManager.class.getName());
    private final ThreadPoolManager threadPoolManager;
    private CommandManager commandManager;
    private ServerSocketChannel serverChannel;
    private final int port;
    private final String serverHost;
    CommandExecuter executer;
    private Selector selector;
    private boolean isRunning = true;
    private RepositoryManager repositoryManager;

    private static class ClientState {
        int ownerId;
        Queue<CommandResponse> responseQueue = new ConcurrentLinkedQueue<>();
        public ClientState(int ownerId) {
            this.ownerId = ownerId;

        }
        @Override
        public String toString(){
            return "" + this.ownerId;
        }
    }

    public ServerNetworkManager(ThreadPoolManager threadPoolManager, String serverHost, int port) {
        this.threadPoolManager = threadPoolManager;
        this.serverHost = serverHost;
        this.port = port;
    }

    public void setRepositoryManager(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public void setExecuter(CommandExecuter executer) {
        this.executer = executer;
    }

    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    private long currentRequestId = 0;

    public void start() throws IOException {
        try {
            this.serverChannel = ServerSocketChannel.open();
            serverChannel.socket().bind(new InetSocketAddress(serverHost, port));
            serverChannel.configureBlocking(false);
            this.selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.log(Level.INFO, "Запуск сервера на порту: {0}", port);
            System.out.println("Запуск сервера на порту: " + port);
            isRunning = true;
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getClass().getSimpleName());
        }
    }

    public void startMainLoop() {
        while (isRunning && selector.isOpen()) {
            try {
                selector.select(500);
                for (SelectionKey key : selector.selectedKeys()) {
                    if (key.isAcceptable()){
                        try {
                            threadPoolManager.getReadPool().submit(() -> {
                                handleAccept(key);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (key.isReadable()){
                        key.interestOps(0);
                        threadPoolManager.getReadPool().submit(() -> {
                            try {
                                handleRead(key);
                            } catch (IOException e) {}
                            finally {
                                if (key.isValid()) {
                                    key.interestOps(SelectionKey.OP_READ);
                                    selector.wakeup();
                                }
                            }
                        });
                    }
                }
                selector.selectedKeys().clear();
            } catch (IOException e) {
                System.out.println("Ошибка селектора: " + e.getMessage());
            }
        }
    }


    public void handleAccept(SelectionKey key) {
        try {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            logger.log(Level.INFO, "Подключился новый клиент: {0}", client);
            ClientState state = new ClientState(-1);
            key.attach(state);
        } catch (IOException e) {
            System.out.println("Ошибка клиента: " + e.getMessage());
            key.cancel();
            try {
                key.channel().close();
            } catch (IOException ex) {
            }
        }
    }


    public void handleRead(SelectionKey key) throws IOException {
        ClientState state1 = (ClientState) key.attachment();
        if (state1 == null){
            state1 = new ClientState(-1);
        }
        final ClientState state = new ClientState(state1.ownerId);
        key.attach(state);

        try {
            CommandRequest request = this.receive(key);
            if (request == null) {
                key.cancel();
                return;
            }
            String cmd = request.commandName();

            if ("GET_COMMANDS".equals(cmd)) {
                this.sendInfo(commandManager, key);
            }
            else if ("AUTHORIZATION_INFO".equals(cmd)) {
                try {
                    boolean flag = true;
                    int newOwnerId = repositoryManager.selectOwnerId(request.login(), request.password());
                    if (newOwnerId == -1) {
                        flag = false;
                        repositoryManager.insertUser(request.login(), request.password());
                        newOwnerId = repositoryManager.selectOwnerId(request.login(), request.password());
                    }
                    state.ownerId = newOwnerId;
                    this.sendBoolean(flag, key);
                } catch (RuntimeException e) {
                    this.send(new CommandResponse(0, "Ошибка при работе с БД", false), key, 1);
                }
            }
            else {
                if (state.ownerId == -1) {
                    send(new CommandResponse(request.requestId(), "Требуется авторизация", false), key, 1);
                    return;
                }
                CompletableFuture
                        .supplyAsync(() -> {System.out.println("Sending in thread: " + Thread.currentThread().getName());
                            return executer.executeCommand(request, state.ownerId);
                                },
                                threadPoolManager.getProcessPool())

                        .thenAcceptAsync(response -> {
                                send(response, key, 1);

                        }, threadPoolManager.getResponsePool())
                        .exceptionally(ex -> {
                            logger.log(Level.SEVERE, "Error", ex);
                            return null;
                        });
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка в handleRead", e);
            key.cancel();
            try { key.channel().close(); } catch (IOException ignored) {}
        } finally {
            if (key.isValid()) {
                key.interestOps(SelectionKey.OP_READ);
                selector.wakeup();
            }
        }
    }


    public void startConsoleListener() {
    new Thread(() -> {
        Scanner consoleScanner = new Scanner(System.in);
        while (isRunning) {
            try {
                String c = consoleScanner.nextLine();
                if ("stop".equals(c)) {
                    logger.info("Завершение работы сервера");
                    close();
                    if (selector != null && selector.isOpen()) {
                        selector.wakeup();
                    }
                    break;
                }
            } catch (Exception e) {
            }
        }
        consoleScanner.close();
    }).start();
    }
    public void close(){
        try {
            isRunning = false;
            this.serverChannel.close();
        } catch (IOException e){
            System.out.println("Ошибка при закрытии соединения: " +  e.getMessage());
        }
    }
    public CommandRequest receive(SelectionKey key) throws IOException, ClassNotFoundException {
        try {
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer lenBuffer = ByteBuffer.allocate(4);

            while (lenBuffer.hasRemaining()) {
                int read = client.read(lenBuffer);
                if (read == -1) {
                    logger.info("Клиент отключился");
                    return null;
                }
            }

            int dataLength = lenBuffer.flip().getInt();
            if (dataLength <= 0 || dataLength > 10000000) {
                throw new IOException("Некорректная длина: " + dataLength);
            }
            ByteBuffer dataBuffer = ByteBuffer.allocate(dataLength);
            while (dataBuffer.hasRemaining()) {
                int read = client.read(dataBuffer);
                if (read == -1) return null;
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(dataBuffer.array());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (CommandRequest) ois.readObject();
        } catch (SocketException e) {
            logger.warning("Клиент отключился: " + e.getMessage());
            return null;
        }
        }
    public void send(CommandResponse response, SelectionKey key, int attempt) {
        Object state = key.attachment();
        System.out.println(state);
        if (!key.isValid()) {
            return;
        }
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel == null || !channel.isOpen()) {
            key.cancel();
            return;
        }
        try {

            byte[] data = response.serialize();
            ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
            buffer.putInt(data.length);
            buffer.put(data);
            buffer.flip();
            int totalWritten = 0;
            while (buffer.hasRemaining()) {
                int written = channel.write(buffer);

                if (written == -1) {
                    throw new IOException("Channel closed");
                }

                if (written == 0) {
                    break;
                }

                totalWritten += written;
            }

            logger.info("Отправлен: " + totalWritten + " байт");

        } catch (Exception e) {
            logger.log(Level.WARNING, "Ошибка отправки: " + e.getMessage());
            key.cancel();
            try { channel.close(); } catch (IOException ignored) {}
        }
    }


    public void sendBoolean(boolean b, SelectionKey key) {
        if (!key.isValid()) return;

        SocketChannel channel = (SocketChannel) key.channel();
        if (channel == null || !channel.isOpen()) {
            key.cancel();
            return;
        }

        try {
            byte[] data = new byte[]{(byte) (b ? 1 : 0)};
            ByteBuffer buffer = ByteBuffer.wrap(data);

            while (buffer.hasRemaining()) {
                int written = channel.write(buffer);
                if (written == -1) throw new IOException("Closed");
                if (written == 0) break;  // ← Добавить!
            }

            logger.info("Отправлен boolean: " + b);

        } catch (Exception e) {
            logger.log(Level.WARNING, "Ошибка sendBoolean", e);
            key.cancel();
        }
    }

    public void sendInfo(CommandManager m, SelectionKey key) {
        if (!key.isValid()) return;

        SocketChannel channel = (SocketChannel) key.channel();
        if (channel == null || !channel.isOpen()) {
            key.cancel();
            return;
        }

        try {
            List<CommandInfo> commands = new ArrayList<>();
            for (Command c : m.getListOfCommand().values()) {
                commands.add(new CommandInfo(
                        c.getName(), c.getArgCount(),
                        c.isNeedObject(), c.isNeedFile(), c.isNeedsOnlyAdmin()
                ));
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(commands);
            oos.flush();
            byte[] data = baos.toByteArray();

            ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
            buffer.putInt(data.length);
            buffer.put(data);
            buffer.flip();

            while (buffer.hasRemaining()) {
                int written = channel.write(buffer);
                if (written == -1) throw new IOException("Channel closed");
                if (written == 0) break;
            }

            logger.info("sendInfo: отправлено " + (4 + data.length) + " байт");

        } catch (Exception e) {
            logger.log(Level.WARNING, "Ошибка sendInfo", e);
            key.cancel();
            try { channel.close(); } catch (IOException ignored) {}
        }
    }
}



