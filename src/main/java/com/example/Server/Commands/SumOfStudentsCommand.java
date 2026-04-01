package com.example.Server.Commands;

import java.util.stream.Stream;

import com.example.Common.CommandResponse;
import com.example.Common.Models.StudyGroup;
import com.example.Server.Utilities.CollectionManager;


public class SumOfStudentsCommand extends Command{
    private final CollectionManager collectionManager;
    /**
     * @param args
     * @param ownerId
     */
    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){
        Stream<StudyGroup> groupStream = collectionManager.getCollection().stream();
        long count = groupStream.mapToLong(g -> g.getStudentsCount()).reduce(0, (a,b) -> a + b);
        return new CommandResponse(0,"Суммарное количество студентов: " + count, true);
    };
    public SumOfStudentsCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin, CollectionManager collectionManager){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
        this.collectionManager = collectionManager;
    }
}
