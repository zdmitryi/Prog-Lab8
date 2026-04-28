package com.example;

import javafx.application.Application;
import javafx.stage.Stage;
public class JavaFXClient extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        ClientNetworkManager networkManager = new ClientNetworkManager("localhost", 12345);
        LocalizationManager localizationManager = new LocalizationManager();
        try {
            networkManager.connect();
        } catch (Exception e) {
            System.out.println("Не удалось подключиться к серверу");
            return;
        }
        AuthWindow authWindow = new AuthWindow(networkManager, localizationManager);
        boolean authorized = authWindow.showAndWait();
        if (authorized) {
            String login = authWindow.getLogin();
            String password = authWindow.getPassword();
            int userId = 1;
        } else {
            System.out.println("Авторизация не удалась");
            System.exit(0);
        }
    }
}