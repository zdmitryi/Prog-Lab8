package com.example.server.commands;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.common.CommandResponse;
import com.example.common.models.StudyGroup;
import com.example.server.utilities.CollectionManager;


public class ShowCommand extends Command{
    private final CollectionManager collectionManager;
    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){
        String answer;
        Stream<StudyGroup> groupStream = collectionManager.getCollection().stream();
        answer = groupStream
                            .sorted()
                            .map(g -> g.toString())
                            .reduce("", (a,b) -> a + b + "\n");
        if (answer.length() > 10000){
            answer = answer.lines().map(line -> {
                        if (line.startsWith("ID группы")){
                            line = "SEPARATOR" + line;
                        }
                        return line;
                    }
            ).collect(Collectors.joining("\n"));
        }
        return new CommandResponse(0,answer, true);
    }
    public ShowCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin, CollectionManager collectionManager){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
        this.collectionManager = collectionManager;
    }
}