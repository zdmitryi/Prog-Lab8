package com.example.Server.Commands;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.Common.CommandResponse;
import com.example.Common.Models.StudyGroup;
import com.example.Server.Utilities.CollectionManager;
import com.example.Server.Utilities.RepositoryManager;


public class UpdateIdCommand extends Command{
    private final CollectionManager collectionManager;
    private final RepositoryManager repositoryManager;
    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){
        group.setOwnerId(ownerId);
        int id;
        try {
        id = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException  | NumberFormatException e) {
            return new CommandResponse(0,"Некорректный ввод аргументов", false);
        }

        try {
            repositoryManager.deleteGroup(group.getId(), ownerId);
            repositoryManager.insertGroup(group, ownerId);
        } catch (RuntimeException e) {
            return new CommandResponse(0,"Ошибка при обновлении группы", false);
        }

        Set<StudyGroup> collection = collectionManager.getCollection();
        
        Optional<StudyGroup> toUpdate = collection.stream()
                                              .filter(g -> g.getId() == id && g.getOwnerId() == ownerId)
                                              .findAny();
        if (toUpdate.isPresent()) {
            collection.remove(toUpdate);
            collectionManager.getWrapper().getGroups().remove(toUpdate);
            group.setId(id);
            collectionManager.getWrapper().getGroups().add(group);
            collection.add(group);
            return new CommandResponse(0,"Группа успешно переопределена", true);
        }
        return new CommandResponse(0,"Группы с заданным ID не существует", false);
                
    };
    public UpdateIdCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin, CollectionManager collectionManager, RepositoryManager repositoryManager){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
        this.collectionManager = collectionManager;
        this.repositoryManager = repositoryManager;
    }
}
