package com.example.server.commands;

import java.util.Set;
import java.util.stream.Stream;

import com.example.common.CommandResponse;
import com.example.common.models.StudyGroup;
import com.example.server.utilities.CollectionManager;
import com.example.server.utilities.RepositoryManager;


public class RemoveGreaterCommand extends Command {
    private final CollectionManager collectionManager;
    private final RepositoryManager repositoryManager;

    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){
        long counter;
        long counterSQL;

        try {
            counterSQL = repositoryManager.removeGreaterSQL(group.getName(), ownerId);
        } catch (RuntimeException e){
            return new CommandResponse(0, "Ошибка при выполнении removeGreater", false);
        }


        Set<StudyGroup> groups = collectionManager.getCollection();
        Stream<StudyGroup> groupStream = groups.stream();
        counter = groupStream.filter(a -> a.getName().compareToIgnoreCase(group.getName()) > 0)
                             .peek(groups::remove)
                             .peek(g -> collectionManager.getWrapper().getGroups().remove(g))
                             .count();
        if (counter != counterSQL) return new CommandResponse(0, "Ошибка синхронизации сервера и базы данных", false);
        return new CommandResponse(0, "Удалено групп: " + counter, true);
    };
    
    public RemoveGreaterCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin, CollectionManager collectionManager, RepositoryManager repositoryManager){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
        this.collectionManager = collectionManager;
        this.repositoryManager = repositoryManager;
    }
}
