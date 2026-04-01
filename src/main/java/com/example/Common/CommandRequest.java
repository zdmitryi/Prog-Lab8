package com.example.Common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.example.Common.Models.StudyGroup;

public record CommandRequest(
    String login,
    String password,
    long requestId,
    String commandName,
    String[] stringArgs,
    StudyGroup group
) implements Serializable {
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream arr = new ByteArrayOutputStream();
        try (ObjectOutputStream stream = new ObjectOutputStream(arr)) {
        stream.writeObject(this);
        }
        return arr.toByteArray();
    }
    public static CommandRequest deserialize(byte[] arr) throws IOException, ClassNotFoundException{
        ByteArrayInputStream arrIn = new ByteArrayInputStream(arr);
        try (ObjectInputStream stream = new ObjectInputStream(arrIn)){
            return (CommandRequest) stream.readObject();
        }
    }
}
