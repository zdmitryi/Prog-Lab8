package com.example.utilities;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


import com.example.models.StudyGroup;

public class WrapperForCollection {
    private CopyOnWriteArrayList<StudyGroup> groups = new CopyOnWriteArrayList<>();
    public List<StudyGroup> getGroups() { return groups; }
    public void setGroups(CopyOnWriteArrayList<StudyGroup> groups) { this.groups = groups; }
    public void sort() {Collections.sort(this.groups);}
}
