package com.example.server.utilities;
import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.example.common.models.StudyGroup;

public class CollectionManager {
    private Set<StudyGroup> collection = ConcurrentHashMap.newKeySet();
    private WrapperForCollection wrapper;
    private final LocalDate initializationDate = LocalDate.now();
    private RepositoryManager repositoryManager;

    public void setRepositoryManager(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }
    public void setWrapper(WrapperForCollection wrapper){
        this.wrapper = wrapper;
    }
    public WrapperForCollection getWrapper(){
        return this.wrapper;
    }
    public void updateWrapper(StudyGroup group){
        this.wrapper.getGroups().add(group);
    }
    public Set<StudyGroup> getCollection(){
        return this.collection;
    }
    /** 
     * @param collection
     */
    /** 
     * @return LocalDate
     */

    public void loadCollection(){
        this.collection = (Set<StudyGroup>) repositoryManager.selectAllGroups();
    }

    public LocalDate getInitializationDate(){
        return this.initializationDate;
    }
}
