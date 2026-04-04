package com.example.server.commands;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.example.common.CommandResponse;
import com.example.common.models.StudyGroup;
import com.example.server.utilities.WrapperForCollection;


public class PrintFieldDescendingStudentsCountCommand extends Command{
    private final WrapperForCollection wrapper;
    /**
     * @param args
     * @param ownerId
     */
    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){

        String answer;
        List<StudyGroup> groups = wrapper.getGroups();
        Stream<StudyGroup> groupStream = groups.stream();
        answer = groupStream.sorted(Comparator.comparing(StudyGroup::getStudentsCount).reversed())
                            .map(a -> String.valueOf(a.getStudentsCount()))
                            .reduce("", (a,b) -> a + b + "\n");    
        return new CommandResponse(0, answer, true);
    }
    public PrintFieldDescendingStudentsCountCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin, WrapperForCollection wrapper){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
        this.wrapper = wrapper;
    }
}
