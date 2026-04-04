package com.example.utilities;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.commands.Command;

public class CommandManager {
    private final CopyOnWriteArrayList<Command> history = new CopyOnWriteArrayList<>();
    private final HashMap<String,Command> listOfCommand = new HashMap<>();
    /** 
     * @return ArrayList<Command>
     */
    public  CopyOnWriteArrayList<Command> getHistory(){
        return this.history;
    } 
    /** 
     * @return HashMap<String, Command>
     */
    public HashMap<String,Command> getListOfCommand(){
        return this.listOfCommand;
    }
    /** 
     * @param c
     */
    public void addToHistory(Command c){
        if (this.getHistory().size() > 10) {
            this.getHistory().remove(0);
        }
        this.getHistory().add(c);
    }
    /** 
     * @param c
     */
    public void register(Command c){
        if (!this.getListOfCommand().containsKey(c.getName())){
            this.getListOfCommand().put(c.getName(),c);
        }
    }
}
