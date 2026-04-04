package com.example.server;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import com.example.common.models.StudyGroup;
import com.example.server.commands.AddCommand;
import com.example.server.commands.AddIfMinCommand;
import com.example.server.commands.ClearCommand;
import com.example.server.commands.CountLessThanGroupAdminCommand;
import com.example.server.commands.ExecuteScriptCommand;
import com.example.server.commands.ExitCommand;
import com.example.server.commands.HelpCommand;
import com.example.server.commands.HistoryCommand;
import com.example.server.commands.InfoCommand;
import com.example.server.commands.PrintFieldDescendingStudentsCountCommand;
import com.example.server.commands.RemoveGreaterCommand;
import com.example.server.commands.RemoveIdCommand;
import com.example.server.commands.ShowCommand;
import com.example.server.commands.SumOfStudentsCommand;
import com.example.server.commands.UpdateIdCommand;
import com.example.server.utilities.*;


public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    public static void main(String[] args) throws IOException{
        logger.info("Инициализация");
        ConnectionPool connectionPool = new ConnectionPool();
        ThreadPoolManager threadPoolManager = new ThreadPoolManager();
        ServerNetworkManager manager = new ServerNetworkManager(threadPoolManager, "server", 12345);
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
