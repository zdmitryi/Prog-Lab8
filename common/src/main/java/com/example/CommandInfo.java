package com.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public record CommandInfo(
    String name,
    int argCount,
    boolean needsObject,
    boolean needsFile,
    boolean needsOnlyAdmin
) implements Serializable{
   public byte[] serialize() throws IOException {
        ByteArrayOutputStream arr = new ByteArrayOutputStream();
        try (ObjectOutputStream stream = new ObjectOutputStream(arr)) {
        stream.writeObject(this);
        }
        return arr.toByteArray();
    }
    public static CommandInfo deserialize(byte[] arr) throws IOException, ClassNotFoundException{
        ByteArrayInputStream arrIn = new ByteArrayInputStream(arr);
        try (ObjectInputStream stream = new ObjectInputStream(arrIn)){
            return (CommandInfo) stream.readObject();
        }
    }
}