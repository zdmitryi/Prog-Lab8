package com.example.commands;

import com.example.CommandResponse;
import com.example.models.StudyGroup;
import com.example.utilities.*;

public class AddCommand extends Command{
    private final CollectionManager collectionManager;
    private final RepositoryManager repositoryManager;
    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){
        group.setOwnerId(ownerId);
        try{
            repositoryManager.insertGroup(group, ownerId);
        } catch (RuntimeException e) {
            return new CommandResponse(0, "Ошибка при вставки группы", false);
        }
        collectionManager.getCollection().add(group);
        collectionManager.updateWrapper(group);
        return new CommandResponse(0,"Группа успешно добавлена", true);
    }
    
    public AddCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin, CollectionManager collectionManager, RepositoryManager repositoryManager){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
        this.collectionManager = collectionManager;
        this.repositoryManager = repositoryManager;
        }

}
