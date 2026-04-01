package com.example.Server.Commands;

import com.example.Common.CommandResponse;
import com.example.Common.Models.StudyGroup;
import com.example.Server.Utilities.CollectionManager;

import java.util.Set;


public class InfoCommand extends Command{
    private final CollectionManager collectionManager;
    /**
     * @param args
     * @param ownerId
     */
    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){
        String answer = "";
        Set<StudyGroup> collection = collectionManager.getCollection();
        answer = answer + "Дата создания коллекции: " + collectionManager.getInitializationDate() + "\n";
        answer = answer + "Размер коллекции: " + collection.size() + "\n";
        if (collection != null && !collection.isEmpty()) {
            answer = answer + "Класс объектов в коллекции: " +
                    collection.iterator().next().getClass().getName() + "\n";
        } else {
            answer = answer + "Класс объектов в коллекции: коллекция пуста\n";
        }
        return new CommandResponse(0, answer,false);

    };
    public InfoCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin, CollectionManager collectionManager){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
        this.collectionManager = collectionManager;
    }
}