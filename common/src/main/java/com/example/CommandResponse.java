package com.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public record CommandResponse(
    long responseId,
    String answer,
    boolean isSuccessful
) implements Serializable {
    public CommandResponse withNewId(long id){
        return new CommandResponse(id, this.answer, this.isSuccessful);
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream arr = new ByteArrayOutputStream();
        try (ObjectOutputStream stream = new ObjectOutputStream(arr)) {
        stream.writeObject(this);
        }
        return arr.toByteArray();
    }

    public static CommandResponse deserialize(byte[] arr) throws IOException, ClassNotFoundException{
        ByteArrayInputStream arrIn = new ByteArrayInputStream(arr);
        try (ObjectInputStream stream = new ObjectInputStream(arrIn)){
            return (CommandResponse) stream.readObject();
        }
    }
}