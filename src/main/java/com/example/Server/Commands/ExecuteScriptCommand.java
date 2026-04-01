package com.example.Server.Commands;


import com.example.Common.CommandResponse;
import com.example.Common.Models.StudyGroup;
public class ExecuteScriptCommand extends Command {
    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){
        return new CommandResponse(0,"Ошибка при инициализации команды", false);
    };
    public ExecuteScriptCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
    }
}
