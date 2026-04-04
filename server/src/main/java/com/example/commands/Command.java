package com.example.commands;

import com.example.CommandResponse;
import com.example.models.StudyGroup;

public abstract class Command {
    private final String name;
    private final String description;
    private final int argCount;
    private final boolean needsOject;
    private final boolean needsFile;
    private final boolean needsOnlyAdmin;
    /** 
     * @return String
     */
    public String getDescription() {
        return this.description;
    }
    public boolean isNeedFile(){
        return this.needsFile;
    }
    public boolean isNeedsOnlyAdmin(){
        return this.needsOnlyAdmin;
    }

    public boolean isNeedObject(){
        return this.needsOject;
    }
    /** 
     * @return String
     */
    public String getName(){
        return this.name;
    }
    public int getArgCount(){
        return this.argCount;
    }
    public abstract CommandResponse execute(String[] args, StudyGroup group, int ownerId);
    public Command(String name, String description,int argCount, boolean needsObject, boolean needsFile, boolean needsOnlyAdmin){
        this.name = name;
        this.description = description;
        this.argCount = argCount;
        this.needsOject = needsObject;
        this.needsOnlyAdmin = needsOnlyAdmin;
        this.needsFile = needsFile;
    }
}
