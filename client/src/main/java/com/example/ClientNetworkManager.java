package com.example;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;




public class ClientNetworkManager {
    private Socket socket;
    private final int port;
    private final String serverHost;
    private boolean isRunning = true;
    private final Object sendLock = new Object();
    private final Object receiveLock = new Object();

    public ClientNetworkManager(String serverHost, int port) {
        this.serverHost = serverHost;
        this.port = port;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void connect() {
        try {
            this.socket = new Socket(serverHost, port);
            socket.setSoTimeout(60000);
            System.out.println("Подключение к серверу " + serverHost + ":" + port);
        } catch (IOException e) {
            System.out.println("Ошибка при открытии соединения.");
            System.exit(0);
        }
    }

    public void close() {
        try {
            this.socket.close();
            isRunning = false;
        } catch (IOException e) {
            System.out.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }

    public void sendAuthorizationInfo(CommandRequest a) throws IOException {
        byte[] data = a.serialize();
        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
        buffer.putInt(data.length);
        buffer.put(data);
        buffer.flip();
        byte[] toSend = new byte[buffer.remaining()];
        buffer.get(toSend);
        socket.getOutputStream().write(toSend);
        socket.getOutputStream().flush();
    }


    public void send(CommandRequest r) throws IOException {
        synchronized (sendLock) {
            byte[] data = r.serialize();
            ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
            buffer.putInt(data.length);
            buffer.put(data);
            buffer.flip();
            byte[] toSend = new byte[buffer.remaining()];
            buffer.get(toSend);
            socket.getOutputStream().write(toSend);
            socket.getOutputStream().flush();
        }
    }

    public class AuthResponse {
        public boolean exists;
        public int ownerId;

        public boolean isExists() {
            return exists;
        }

        public int getOwnerId() {
            return ownerId;
        }
    }

    public AuthResponse receiveAuthResponse() throws IOException {
        byte[] data = new byte[5];
        readFully(data);

        AuthResponse response = new AuthResponse();
        response.exists = data[0] == 1;
        response.ownerId = ByteBuffer.wrap(data, 1, 4).getInt();

        return response;
    }
    public int receiveOwnerId() throws IOException, ClassNotFoundException {
        byte[] data = new byte[4];
        readFully(data);
        return ByteBuffer.wrap(data).getInt();
    }

    public CommandResponse receive() throws IOException, ClassNotFoundException {
        synchronized (receiveLock) {
            byte[] lenBytes = new byte[4];
            readFully(lenBytes);
            int dataLength = ByteBuffer.wrap(lenBytes).getInt();
            byte[] data = new byte[dataLength];
            readFully(data);
            return (CommandResponse) deserializeObject(data);
        }
    }

    private void readFully(byte[] buffer) throws IOException {
        int total = 0;
        while (total < buffer.length) {
            int read = socket.getInputStream().read(buffer, total, buffer.length - total);
            if (read == -1) throw new IOException("Соединение закрыто");
            total += read;
        }
    }

    private Object deserializeObject(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<CommandInfo> receiveInfo() throws IOException, ClassNotFoundException {
        try {
            byte[] lenBytes = new byte[4];
            readFully(lenBytes);
            int dataLength = ByteBuffer.wrap(lenBytes).getInt();
            byte[] data = new byte[dataLength];
            readFully(data);
            ArrayList<CommandInfo> list = (ArrayList<CommandInfo>) deserializeObject(data);
            return list;
        } catch (java.net.SocketTimeoutException e) {
            System.out.println("Таймаут ожидания ответа");
            return null;
        }
    }
}
