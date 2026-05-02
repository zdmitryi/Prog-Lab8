package com.example.commands;

import com.example.CommandResponse;
import com.example.models.StudyGroup;
import com.example.utilities.CollectionManager;

import java.util.stream.Collectors;
import java.util.stream.Stream;




public class ShowCommand extends Command{
    private final CollectionManager collectionManager;
    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){
        String answer = collectionManager.getCollection().stream()
                .sorted()
                .map(g -> "SEPARATOR" + g.toString())
                .collect(Collectors.joining(""));
        return new CommandResponse(0, answer, true);
    }
    public ShowCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin, CollectionManager collectionManager){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
        this.collectionManager = collectionManager;
    }
}