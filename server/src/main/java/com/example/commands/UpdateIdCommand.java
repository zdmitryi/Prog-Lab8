package com.example.commands;

import com.example.CommandResponse;
import com.example.models.StudyGroup;
import com.example.utilities.CollectionManager;
import com.example.utilities.RepositoryManager;

import java.util.Optional;
import java.util.Set;




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
        System.out.println("Updating id: " + id + " by owner: " + ownerId);
        try {
            repositoryManager.updateGroup(id, group, ownerId);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new CommandResponse(0,"Ошибка при обновлении группы", false);
        }

        Set<StudyGroup> collection = collectionManager.getCollection();
        
        Optional<StudyGroup> toUpdate = collection.stream()
                                              .filter(g -> g.getId() == id && g.getOwnerId() == ownerId)
                                              .findAny();
        if (toUpdate.isPresent()) {
            StudyGroup old = toUpdate.get();
            collectionManager.getWrapper().getGroups().remove(old);
            collection.remove(old);
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
