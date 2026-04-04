package com.example.utilities;

import java.util.HashMap;

import com.example.CommandRequest;
import com.example.CommandResponse;
import com.example.models.StudyGroup;
import com.example.commands.Command;


public class CommandExecuter {
    private final ServerNetworkManager manager;
    private final CommandManager commandManager;
    private final ThreadPoolManager threadPoolManager;
    public CommandExecuter(ServerNetworkManager manager, CommandManager commandManager, ThreadPoolManager threadPoolManager){
        this.manager = manager;
        this.commandManager = commandManager;
        this.threadPoolManager = threadPoolManager;
    }
    public CommandResponse executeCommand(CommandRequest request, int ownerId) {
        String commandName = request.commandName();
        String[] args = request.stringArgs();
        StudyGroup group = request.group();

        if (ownerId == -1){
            return new CommandResponse(request.requestId(), "Требуется авторизация", false);
        }

        HashMap<String, Command> map = commandManager.getListOfCommand();

        if (map.containsKey(commandName)) {
            try {
                Command c = map.get(commandName);
                CommandResponse response = c.execute(args, group, ownerId);
                commandManager.addToHistory(c);
                return new CommandResponse(request.requestId(), response.answer(), response.isSuccessful());
            } catch (IllegalArgumentException e) {
                System.out.println(e);
                return new CommandResponse(request.requestId(), "Ошибка аргументов: " + e.getMessage(), false);
            }
        } else {
            return new CommandResponse(request.requestId(), "Неизвестная команда: " + commandName, false);
        }
    }
    
    
}
