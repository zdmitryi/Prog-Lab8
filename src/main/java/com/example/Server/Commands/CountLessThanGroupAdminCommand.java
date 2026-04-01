package com.example.Server.Commands;

import java.util.List;
import java.util.stream.Stream;

import com.example.Common.CommandResponse;
import com.example.Common.Models.Person;
import com.example.Common.Models.StudyGroup;
import com.example.Server.Utilities.RepositoryManager;
import com.example.Server.Utilities.WrapperForCollection;

public class CountLessThanGroupAdminCommand extends Command {
    private final WrapperForCollection wrapper;
    private final RepositoryManager repositoryManager;
    @Override
    public CommandResponse execute(String[] args, StudyGroup group, int ownerId){
        Person admin = group.getGroupAdmin();
        List<StudyGroup> groups = wrapper.getGroups();
        long counterSQL;
        try {
            counterSQL = repositoryManager.countLessThan(admin);
        } catch (RuntimeException e) {
            return new CommandResponse(0, "Ошибка при выполнении countLessThanGroupAdmin", false);
        }


        long counter;
        Stream<StudyGroup> groupStream = groups.stream();
        counter = groupStream.filter(g -> g.getGroupAdmin()
                             .getWeight() < admin.getWeight())
                             .count();
        if (counter != counterSQL) return new CommandResponse(0,"Ошибка синхронизации сервера и базы данных", false);

        return new CommandResponse(0,"Количество таких админов:" + counter, true);
    };
    public CountLessThanGroupAdminCommand(String name, String description, int amount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin, WrapperForCollection wrapper, RepositoryManager repositoryManager){
        super(name, description, amount, needsObject, needsFile, needsOnlyAdmin);
        this.wrapper = wrapper;
        this.repositoryManager = repositoryManager;
    }
}
