package com.example.commands;

import com.example.CommandResponse;
import com.example.models.StudyGroup;
import com.example.utilities.CollectionManager;
import com.example.utilities.RepositoryManager;

import java.util.Set;
import java.util.stream.Stream;




public class RemoveIdCommand extends Command{
    private final CollectionManager collectionManager;
    private final RepositoryManager repositoryManager;
    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){
        int id;
        try {
        id = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException  | NumberFormatException e) {
            return new CommandResponse(0,"Некорректный ввод аргументов", false);
        }
        System.out.println("Removing id: " + id + " by owner: " + ownerId);
        System.out.println(group);
        try {
            repositoryManager.deleteGroup(id, ownerId);
        } catch (RuntimeException e){
            e.printStackTrace();
            return new CommandResponse(0, "Ошибка при удалении группы", false);
        }
        Set<StudyGroup> collection = collectionManager.getCollection();
        Stream<StudyGroup> groupStream = collection.stream();
        final Boolean[] res = new Boolean[1];
        groupStream.filter(g -> g.getId() == id && g.getOwnerId() == ownerId)
                   .forEach(g -> {
                       collectionManager.getWrapper().getGroups().remove(g);
                       collection.remove(g);
                       res[0] = true;
                   });
        if (res[0]){
            return new CommandResponse(0,"Группа успешно удалена", true);
        } else {
            return new CommandResponse(0,"Группы с заданным ID не существует", true);
        }
    };
    public RemoveIdCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin, CollectionManager collectionManager, RepositoryManager repositoryManager){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
        this.collectionManager = collectionManager;
        this.repositoryManager = repositoryManager;
    }
}
