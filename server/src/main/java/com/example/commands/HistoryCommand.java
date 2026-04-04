package com.example.commands;

import com.example.CommandResponse;
import com.example.models.StudyGroup;
import com.example.utilities.CommandManager;

import java.util.stream.Stream;




public class HistoryCommand extends Command{
    private final CommandManager commandManager;
    /**
     * @param args
     * @param ownerId
     */
    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){
        String answer;
        Stream<Command> groupStream = commandManager.getHistory().stream();
        answer = groupStream.map(a -> a.getName())
                            .reduce("", (a,b) -> a + b + "\n");
        return new CommandResponse(0,answer, true);

    };
    public HistoryCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin, CommandManager commandManager){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
        this.commandManager = commandManager;
    }
}
