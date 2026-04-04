package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;



public class CommandValidator {
    private ArrayList<CommandInfo> commands;
    private final ClientNetworkManager manager;
    public void initialize() throws IOException, ClassNotFoundException{
        CommandRequest request = new CommandRequest("","", -1,"GET_COMMANDS", null, null);
        manager.send(request);
        commands = manager.receiveInfo(); 
    }
    public ArrayList<CommandInfo> getCommands(){

        return commands;
    }
    public CommandInfo findInfo(String line){
        line = line.trim();
        String[] parts = line.split("\\s+");
        String s = parts[0];
        for (CommandInfo c : commands){
            if (c.name().equals(s)) {
                return c;
            }
        }
        System.out.println("Неверный ввод, попробуйте еще раз");
        return null;
    }
    public boolean validate(String line){
        String[] parts = line.split("\\s+");
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        CommandInfo c = findInfo(line);
        if (c == null){
            return false;
        }
        if (c.argCount() == args.length) return true;
        else{
            System.out.println("Неверное количество аргументов");
            return false;
        }
    }
    public CommandValidator(ClientNetworkManager manager){
        this.manager = manager;
    }
}
