package com.example;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Locale;
public class AuthWindow {
    private final Stage stage;
    private final ClientNetworkManager networkManager;
    private final LocalizationManager locManager;
    private Label titleLabel;
    private Label loginLabel;
    private Label passLabel;
    private Label messageLabel;
    private Button loginIn;
    private Button registerIn;
    private ComboBox<String> languageBox;
    private TextField loginField;
    private PasswordField passwordField;
    private String login;
    private String password;
    private boolean authorized = false;
    public AuthWindow(ClientNetworkManager networkManager, LocalizationManager localizationManager) {
        this.networkManager = networkManager;
        this.locManager = localizationManager;
        this.stage = new Stage();
        createWindow();
    }
    public boolean isAuthorized() {
        return authorized;
    }
    public String getLogin() {
        return login;
    }
    public String getPassword() {
        return password;
    }
    private void createWindow() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        languageBox = new ComboBox<>();
        languageBox.getItems().addAll("Русский", "Čeština", "Dansk", "Español (CO)");
        languageBox.setValue(locManager.get("auth.choose.lang"));
        languageBox.setMaxWidth(100);
        languageBox.setOnAction(e -> changeLanguage());
        titleLabel = new Label();
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        loginLabel = new Label();
        loginField = new TextField();
        loginField.setMaxWidth(250);
        passLabel = new Label();
        passwordField = new PasswordField();
        passwordField.setMaxWidth(250);
        passwordField.setOnAction(e -> authenticate(true));
        messageLabel = new Label();
        messageLabel.setVisible(false);
        messageLabel.setMaxWidth(250);
        messageLabel.setWrapText(true);
        loginIn = new Button();
        loginIn.setMaxWidth(250);
        loginIn.setOnAction(e -> authenticate(true));
        registerIn = new Button();
        registerIn.setMaxWidth(250);
        registerIn.setOnAction(e -> authenticate(false));
        root.getChildren().addAll(
                languageBox,
                titleLabel,
                loginLabel,
                loginField,
                passLabel,
                passwordField,
                messageLabel,
                loginIn,
                registerIn
        );
        Scene scene = new Scene(root, 350, 420);
        stage.setScene(scene);
        updateLanguage();
        stage.setOnShown(e -> loginField.requestFocus());
    }
    private void updateLanguage() {
        stage.setTitle(locManager.get("auth.title"));
        titleLabel.setText("Study Group Manager");
        loginLabel.setText(locManager.get("auth.login") + ":");
        loginField.setPromptText(locManager.get("auth.login"));
        passLabel.setText(locManager.get("auth.password") + ":");
        passwordField.setPromptText(locManager.get("auth.password"));
        loginIn.setText(locManager.get("auth.login.in"));
        registerIn.setText(locManager.get("auth.register.in"));
        if (messageLabel.isVisible() && messageLabel.getText() != null) {
            String currentText = messageLabel.getText();
        }
    }
    private void changeLanguage() {
        String selected = languageBox.getValue();
        switch (selected) {
            case "Русский":
                locManager.setLocale(new Locale("ru"));
                break;
            case "Čeština":
                locManager.setLocale(new Locale("cs"));
                break;
            case "Dansk":
                locManager.setLocale(new Locale("da"));
                break;
            case "Español (CO)":
                locManager.setLocale(new Locale("es", "CO"));
                break;
        }
        updateLanguage();
    }
    private void authenticate(boolean isLogin) {
        login = loginField.getText().trim();
        password = passwordField.getText().trim();
        if (login.isEmpty() || password.isEmpty()) {
            showMessage(locManager.get("auth.empty_fields"), "#e74c3c");
            return;
        }
        new Thread(() -> {
            try {
                CommandRequest request = new CommandRequest(
                        login, password, 1, "AUTHORIZATION_INFO",
                        new String[0], null
                );
                networkManager.sendAuthorizationInfo(request);
                boolean userExists = networkManager.receiveAuthorizationResponse();

                javafx.application.Platform.runLater(() -> {
                    if (isLogin) {
                        if (userExists) {
                            showMessage(locManager.get("auth.success"), "#27ae60");
                            authorized = true;
                            closeAfterDelay();
                        } else {
                            showMessage(locManager.get("auth.error"), "#e74c3c");
                        }
                    } else {
                        showMessage(locManager.get("auth.registered"), "#27ae60");
                        authorized = true;
                        closeAfterDelay();
                    }
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    showMessage(locManager.get("error.network"), "#e74c3c");
                });
            }
        }).start();
    }
    private void showMessage(String text, String color) {
        messageLabel.setText(text);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setVisible(true);
    }
    private void closeAfterDelay() {
        javafx.animation.PauseTransition delay =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));

        delay.setOnFinished(e -> stage.close());
        delay.play();
    }
    public boolean showAndWait() {
        stage.showAndWait();
        return authorized;
    }

}