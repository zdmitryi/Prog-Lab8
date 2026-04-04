package com.example.server.commands;

import com.example.common.CommandResponse;
import com.example.common.models.StudyGroup;
import com.example.server.utilities.CollectionManager;
import com.example.server.utilities.RepositoryManager;
import com.example.server.utilities.WrapperForCollection;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ClearCommand extends Command{
    private final CollectionManager collectionManager;
    private final RepositoryManager repositoryManager;

    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){

        try {
            repositoryManager.clearGroups(ownerId);
        } catch (RuntimeException e) {
            return new CommandResponse(0, "Ошибка при очистке коллекции: " + e, false);
        }

        Set<StudyGroup> collection = collectionManager.getCollection();
        WrapperForCollection wrapper = collectionManager.getWrapper();
        List<StudyGroup> studyGroupList = wrapper.getGroups();

        Stream<StudyGroup> groupStream = collection.stream();
        groupStream.filter(g -> g.getOwnerId() == ownerId).forEach(g -> {
            collection.remove(g);
            studyGroupList.remove(g);
        });

        return new CommandResponse(0,"Коллекция успешно очищена", true);
    }
    public ClearCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin, CollectionManager collectionManager, RepositoryManager repositoryManager){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
        this.collectionManager = collectionManager;
        this.repositoryManager = repositoryManager;
    }
}