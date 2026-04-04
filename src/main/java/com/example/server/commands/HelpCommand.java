package com.example.server.commands;

import java.util.stream.Stream;

import com.example.common.CommandResponse;
import com.example.common.models.StudyGroup;
import com.example.server.utilities.CommandManager;


public class HelpCommand extends Command{
    private final CommandManager commandManager;
    /**
     * @param args
     * @param ownerId
     */
    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){
        String answer;
        Stream<Command> commandStream = commandManager.getListOfCommand().values().stream();
        answer = commandStream.map(a -> a.getName() + " : " + a.getDescription())
                              .reduce("", (a,b) -> a + b + "\n");
        return new CommandResponse(0,answer, true);
    }
    public HelpCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin, CommandManager commandManager){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
        this.commandManager = commandManager;

    }
}