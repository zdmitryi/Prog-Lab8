package com.example.Client;
import com.example.Common.CommandRequest;

import java.io.IOException;
public class Client {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        int counterOfConnection = 0;
        ClientNetworkManager manager = new ClientNetworkManager("server", 12345);
        CommandValidator validator = new CommandValidator(manager);
        Reader reader = new Reader(validator, manager);
        boolean isAuthorized = false;
        while (counterOfConnection < 2) {
            manager.connect();
        try {
            reader.readAuthorizationInfo();
            manager.sendAuthorizationInfo(new CommandRequest(reader.getLogin(), reader.getPassword(), 0, "AUTHORIZATION_INFO", new String[0], null));
            isAuthorized = manager.receiveAuthorizationResponse();
        } catch (Exception e){
            System.out.println(e);
            return;
        }
        if (isAuthorized) System.out.println("Вы успешно авторизовались. С возвращением!");
        else System.out.println("Вы успешно зарегистрировались!");
        try {
                validator.initialize();
        } catch (Exception e) {
                System.out.println(e);
                return;
        }
        System.out.println("Приложение успешно запущено. Введите help для списка всех команд.");
        while (manager.isRunning()) {
            try {
                reader.readCommand();
            } catch (Exception e) {
                if (counterOfConnection == 0) System.out.println("Соединение с сервером прервано, попытка повторного переподключения");
                System.out.println(e);
                counterOfConnection++;
                Thread.sleep(1000);
                break;
            }
        }
        }
        System.out.println("Соединение с сервером разорвано, перезапустите приложение");
    }
}
