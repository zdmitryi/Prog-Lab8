package com.example.server.commands;
import com.example.common.CommandResponse;
import com.example.common.models.StudyGroup;


public class ExitCommand extends Command {
    /**
     * @param args
     * @param ownerId
     */
    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){
        return new CommandResponse(0,"Завершение работы приложения", true);
    };
    public ExitCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
    }
}
