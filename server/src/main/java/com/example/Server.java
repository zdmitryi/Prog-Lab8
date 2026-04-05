package com.example;


import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import com.example.models.StudyGroup;
import com.example.utilities.*;
import com.example.commands.*;

import java.nio.file.Files;
import java.nio.file.Paths;



public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    public static void main(String[] args) throws IOException{
        logger.info("Инициализация");
        String host = null;
        String port = null;
        String name = null;
        String user = null;
        String password = null;
        String url = null;
        for (String line : Files.readAllLines(Paths.get(".env"))) {
            if (line.startsWith("DB_HOST=")) host = line.split("=", 2)[1];
            if (line.startsWith("DB_PORT=")) port = line.split("=", 2)[1];
            if (line.startsWith("DB_NAME=")) name = line.split("=", 2)[1];
            if (line.startsWith("DB_USER=")) user = line.split("=", 2)[1];
            if (line.startsWith("DB_PASSWORD=")) password = line.split("=", 2)[1];
            if (line.startsWith("DB_URL=")) url = line.split("=",2)[1];
        }
        String urlFull = url + host + ":" + port + "/" + name;
        DbMigration.migrate(urlFull, user, password);
        ConnectionPool connectionPool = new ConnectionPool(urlFull, user, password);
        ThreadPoolManager threadPoolManager = new ThreadPoolManager();
        ServerNetworkManager manager = new ServerNetworkManager(threadPoolManager, "localhost", 12345);
        CommandManager commandManager = new CommandManager();
        CommandExecuter executer = new CommandExecuter(manager, commandManager, threadPoolManager);
        manager.setExecuter(executer);
        manager.setCommandManager(commandManager);
        CollectionManager collectionManager = new CollectionManager();
        RepositoryManager repositoryManager = new RepositoryManager(connectionPool);
        WrapperForCollection wrapper = new WrapperForCollection();  
        collectionManager.setRepositoryManager(repositoryManager);
        collectionManager.setWrapper(wrapper);
        manager.setRepositoryManager(repositoryManager);
        collectionManager.loadCollection();
        CopyOnWriteArrayList<StudyGroup> list = new CopyOnWriteArrayList<>(collectionManager.getCollection());
        wrapper.setGroups(list);
        StudyGroup.setInitialNextId(collectionManager.getCollection());
        wrapper.sort();
        commandManager.register(new AddCommand("add", "Добавляет новый элемент в коллекцию", 0, true, false, false, collectionManager, repositoryManager));
        commandManager.register(new AddIfMinCommand("addIfMin", "Добавляет новый элеменет в коллекцию, если количество студентов в новой группе меньше, чем у любой другой группы из коллекции", 0, true, false, false, collectionManager, wrapper, repositoryManager));
        commandManager.register(new ClearCommand("clear", "Отчищает всю коллекцию", 0, false, false, false, collectionManager, repositoryManager));
        commandManager.register(new ExitCommand("exit","Завершает работу приложения", 0, false, false, false));
        commandManager.register(new CountLessThanGroupAdminCommand("countLessThanGroupAdmin", "Выводит количество групп, у которых вес админа меньше введенного", 0, false, false, true, wrapper, repositoryManager));
        commandManager.register(new ExecuteScriptCommand("executeScript", "Выполняет скрипт из переданного файла", 1, false, true, false));
        commandManager.register(new HelpCommand("help", "Выводит имена и описания всех команд", 0, false, false, false, commandManager));
        commandManager.register(new HistoryCommand("history", "Выводит последние 10 использованных команд", 0, false, false, false, commandManager));
        commandManager.register(new InfoCommand("info", "Выводит общую информацию о коллекции", 0, false, false, false, collectionManager));
        commandManager.register(new PrintFieldDescendingStudentsCountCommand("printField", "Выводит количество студентов в каждой группе из коллекции в порядке убывания", 0 , false, false, false, wrapper));
        commandManager.register(new RemoveGreaterCommand("removeGreater", "Удаляет из коллекции все элементы, превышающие заданный", 0, true, false, false, collectionManager, repositoryManager));
        commandManager.register(new RemoveIdCommand("removeId", "Удаляет элемент из коллекции по ID", 1, false, false, false, collectionManager, repositoryManager));
        commandManager.register(new ShowCommand("show", "Выводит всю коллекцию", 0 ,false, false, false, collectionManager));
        commandManager.register(new SumOfStudentsCommand("sumOfStudents", "Выводит суммарное количество студентов из всех групп коллекции", 0, false, false, false, collectionManager));
        commandManager.register(new UpdateIdCommand("updateId", "Меняет элемент заданного ID на введенный", 1, true, false, false, collectionManager, repositoryManager));
        manager.startConsoleListener();
        manager.start();
        manager.startMainLoop();
    }    
}
