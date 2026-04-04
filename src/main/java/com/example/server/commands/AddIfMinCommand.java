package com.example.server.commands;


import com.example.common.CommandResponse;
import com.example.common.models.StudyGroup;
import com.example.server.utilities.CollectionManager;
import com.example.server.utilities.RepositoryManager;
import com.example.server.utilities.WrapperForCollection;

public class AddIfMinCommand extends Command {
    private final CollectionManager collectionManager;
    private final WrapperForCollection wrapper;
    private final RepositoryManager repositoryManager;
    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){
        group.setOwnerId(ownerId);
        wrapper.sort();
        if (group.compareTo(wrapper.getGroups().get(0)) < 0) {
            try{
                repositoryManager.insertGroup(group, ownerId);
            } catch (RuntimeException e) {
                return new CommandResponse(0, "Ошибка при вставки группы", false);
            }
            collectionManager.getCollection().add(group);
            collectionManager.updateWrapper(group);
            return new CommandResponse(0,"Группа успешно добавлена", true);
        } else return new CommandResponse(0,"Группа не была добавлена", true);
    }
    public AddIfMinCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin, CollectionManager collectionManager, WrapperForCollection wrapper, RepositoryManager repositoryManager){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
        this.collectionManager = collectionManager;
        this.wrapper = wrapper;
        this.repositoryManager = repositoryManager;
        }

}
