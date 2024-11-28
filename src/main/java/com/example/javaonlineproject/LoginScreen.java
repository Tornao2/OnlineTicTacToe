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

import java.io.*;
import java.net.Socket;
import java.util.Objects;

import static javafx.scene.paint.Color.WHITE;

public class LoginScreen {
    private Runnable playerLogin;
    private final UserInfo user = new UserInfo();


    private ImageView createLogo() {
        ImageView logoImageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/TictacToe.png"))));
        logoImageView.setFitHeight(200);
        logoImageView.setPreserveRatio(true);
        return logoImageView;
    }
    private TextField createUsernameField() {
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
        text.setFont(new Font(16));
        text.setVisible(false);
        return text;
    }
    private Button createSignInButton(TextField usernameField, PasswordField passwordField, Text text) {
        Button signInButton = new Button("Sign in");
        signInButton.setFont(new Font(16.0));
        signInButton.setOnAction(_ -> changeScene(usernameField, passwordField, text));
        return signInButton;
    }
    private Button createSignUpButton(TextField usernameField, PasswordField passwordField, Text text){
        Button signUpButton = new Button("Sign up");
        signUpButton.setFont(new Font(16.0));
        signUpButton.setOnAction(_ -> handleSignUp(usernameField, passwordField, text));
        return signUpButton;
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
        TextField usernameField = createUsernameField();
        PasswordField passwordField = createPassField();
        Text errorText = createErrorText();
        Button signInButton = createSignInButton(usernameField, passwordField, errorText);
        Button signUpButton = createSignUpButton(usernameField, passwordField, errorText);
        VBox organizer = createVBox();
        organizer.getChildren().addAll(logoImageView, usernameField, passwordField, signInButton, signUpButton, errorText);
        BorderPane Manager = createManager(organizer);
        manageScene(Manager, primaryStage);
        centerElements(logoImageView, errorText, Manager);
    }
    private void handleSignUp(TextField usernameField, PasswordField passwordField, Text text){
        user.setUsername(usernameField.getText());
        String password = passwordField.getText();
        if (user.getUsername().isEmpty() || password.isEmpty())
            text.setText("Username or password cannot be empty.");
        else if(user.getUsername().matches(".*[^a-zA-Z0-9].*") || password.matches(".*[^a-zA-Z0-9].*"))
            text.setText("You can't use space");
        else{
            try {
                user.setUserSocket(new Socket("localhost", 12345));
            } catch (IOException _) {}
            if (user.getUserSocket() == null) text.setText("Server isn't currently running");
            else {
                //wysylanie danych do serwera
                user.setUserInput(user.getUserSocket());
                user.setUserOutput(user.getUserSocket());
                user.getUserOutput().sendMessage("SIGNUP" + "," + user.getUsername() + "," + password);
                String response = user.getUserInput().receiveMessage();
                if (response.equals("ALLOWED")) {
                    text.setText("Account create succesfully!");
                    playerLogin.run(); //Nie wiem czy chcesz zeby rejestracja tworzyla konto czy tez przezucala do menu wiec zostawie narazie
                }
                else if (response.equals("USERNAME_TAKEN")) {
                    text.setText("Username already taken!");
                }
            }
        }
        text.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(_ -> text.setVisible(false));
        visiblePause.play();
    }
    private void changeScene(TextField usernameField, PasswordField passwordField, Text text) {
        user.setUsername(usernameField.getText());
        String password = passwordField.getText();
        if (user.getUsername().isEmpty() || password.isEmpty())
            text.setText("Username or password cannot be empty.");
        else if(user.getUsername().contains(" ") || password.contains(" "))
            text.setText("You can't use space");
        else {
            try {
                user.setUserSocket(new Socket("localhost", 12345));
            } catch (IOException _) {

            }
            if (user.getUserSocket() == null) text.setText("Server isn't currently running");
            else {
                //wysylanie danych do serwera
                user.setUserInput(user.getUserSocket());
                user.setUserOutput(user.getUserSocket());
                user.getUserOutput().sendMessage("LOGIN" + "," + user.getUsername() + "," + password);
                String response = user.getUserInput().receiveMessage();
                if (response.equals("ALLOWED")) {
                    playerLogin.run();
                }
                else if(response.equals("ALREADY_LOGGED_IN")){
                    text.setText("You are already logged in");
                }
                else {
                    text.setText("Incorrect username or password");
                }
            }
        }
        text.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(_ -> text.setVisible(false));
        visiblePause.play();
    }
    public void setOnLoginPlayer(Runnable onLogin) {
        this.playerLogin = onLogin;
    }
    public UserInfo getUser() {
        return user;
    }
}
