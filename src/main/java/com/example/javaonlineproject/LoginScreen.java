package com.example.javaonlineproject;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Objects;

import static javafx.scene.paint.Color.WHITE;

public class LoginScreen {
    private Runnable playerLogin;
    private Runnable serverLogin;
    private final String serverName = "Server";
    private final String serverPassword = "Server";

    private ImageView createLogo() {
        ImageView logoImageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/TictacToe.png"))));
        logoImageView.setFitHeight(200);
        logoImageView.setPreserveRatio(true);
        return logoImageView;
    }
    private TextField createLoginField() {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setFont(new Font(16));
        usernameField.setStyle("-fx-background-color: #222222; -fx-text-fill: white;");
        return usernameField;
    }
    private PasswordField createPassField() {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setFont(new Font(16));
        passwordField.setStyle("-fx-background-color: #222222; -fx-text-fill: white;");
        return passwordField;
    }
    private Text createErrorText() {
        Text text = new Text("");
        text.setFill(WHITE);
        text.setTabSize(16);
        text.setFont(new Font(16));
        text.setVisible(false);
        return text;
    }
    private Button createLoginButton(TextField usernameField, TextField passwordField, Text text) {
        Button loginButton = new Button("Login");
        loginButton.setFont(new Font(16.0));
        loginButton.setOnAction(_ -> changeScene(usernameField, passwordField, text));
        return loginButton;
    }
    private VBox createVBox() {
        VBox organizer = new VBox(12);
        organizer.setMinSize(300, 210);
        organizer.setPadding(new Insets(10, 8, 10, 8));
        organizer.setAlignment(Pos.BASELINE_CENTER);
        return organizer;
    }
    private BorderPane createManager(VBox organizer) {
        BorderPane root = new BorderPane(organizer);
        root.setStyle("-fx-background-color: #1A1A1A;");
        return root;
    }
    private void manageScene(BorderPane root, Stage primaryStage) {
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Screen");
        primaryStage.show();
        root.requestFocus();
    }
    private void centerElements(ImageView image, Text text, BorderPane root) {
        image.setX((root.getWidth() - image.getLayoutBounds().getWidth()) / 2);
        text.setX((root.getWidth() - text.getLayoutBounds().getWidth()) / 2);
    }

    public void start(Stage primaryStage) {
        ImageView logoImageView = createLogo();
        TextField usernameField = createLoginField();
        PasswordField passwordField = createPassField();
        Text errorText = createErrorText();
        Button loginButton = createLoginButton(usernameField, passwordField, errorText);
        VBox organizer = createVBox();
        organizer.getChildren().addAll(logoImageView, usernameField, passwordField, loginButton, errorText);
        BorderPane Manager = createManager(organizer);
        manageScene(Manager, primaryStage);
        centerElements(logoImageView, errorText, Manager);
    }


    private void changeScene(TextField usernameField, TextField passwordField, Text text) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Socket socket = null;
        try {
            socket = new Socket("localhost", 12345);
        } catch (IOException _) {

        }
        if (username.equals(serverName) && password.equals(serverPassword)) {
            if (socket != null) {
                text.setText("Server already exists!");
            } else {
                serverLogin.run();
                return;
            }
        } else if (username.isEmpty() || password.isEmpty())
            text.setText("Username or password cannot be empty.");
        else if (socket == null)
            text.setText("Server isn't currently running");
        else {
            BufferedReader input;
            String answer;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            PlayerSender playerSender = new PlayerSender(socket);
            playerSender.sendMessage("LOGIN" + ',' + username + ',' + password);
            try {
                answer = input.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (answer.equals("ALLOWED")) {
                playerLogin.run();
            }
            return;
        }
        text.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(_ -> text.setVisible(false));
        visiblePause.play();
    }
    public void setOnLoginPlayer(Runnable onLogin) {
        this.playerLogin = onLogin;
    }
    public void setOnLoginServer(Runnable onLogin) {
        this.serverLogin = onLogin;
    }
}
