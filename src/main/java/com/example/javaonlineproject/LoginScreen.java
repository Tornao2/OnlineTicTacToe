package com.example.javaonlineproject;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
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
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static javafx.scene.paint.Color.WHITE;

public class LoginScreen {
    private Runnable playerLogin;
    private Thread preConnectionThread;
    private final UserInfo user = new UserInfo();
    Text text;

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
    private void createErrorText() {
        text = new Text("");
        text.setFill(WHITE);
        text.setFont(new Font(20));
        text.setVisible(false);
    }
    private Button createSignInButton(TextField usernameField, PasswordField passwordField) {
        Button signInButton = new Button("Sign in");
        signInButton.setFont(new Font(16.0));
        signInButton.getStyleClass().add("button");
        signInButton.setOnAction(_ -> buttonsFunc(usernameField, passwordField, true));
        return signInButton;
    }
    private Button createSignUpButton(TextField usernameField, PasswordField passwordField){
        Button signUpButton = new Button("Sign up");
        signUpButton.setFont(new Font(16.0));
        signUpButton.getStyleClass().add("button");
        signUpButton.setOnAction(_ -> buttonsFunc(usernameField, passwordField, false));
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
        Scene scene = new Scene(root, 400, 500); // szerokość 400 i wysokość 500
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Screen");
        primaryStage.show();
        root.requestFocus();
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    private void centerElements(ImageView image, BorderPane root) {
        image.setX((root.getWidth() - image.getLayoutBounds().getWidth()) / 2);
        text.setX((root.getWidth() - text.getLayoutBounds().getWidth()) / 2);
    }
    public void start(Stage primaryStage) {
        ImageView logoImageView = createLogo();
        TextField usernameField = createUsernameField();
        PasswordField passwordField = createPassField();
        createErrorText();
        Button signInButton = createSignInButton(usernameField, passwordField);
        Button signUpButton = createSignUpButton(usernameField, passwordField);
        VBox organizer = createVBox();
        organizer.getChildren().addAll(logoImageView, usernameField, passwordField, signInButton, signUpButton, text);
        BorderPane Manager = createManager(organizer);
        manageScene(Manager, primaryStage);
        centerElements(logoImageView, Manager);
        logic();
    }
    private void logic() {
        Runnable preConnection = () -> {
            Platform.runLater(() -> {
                text.setText("Connecting to the server");
                text.setVisible(true);
            });
            while (!Thread.currentThread().isInterrupted()) {
                ArrayList<String> temp = (ArrayList<String>) getArpIps();
                for (String s: temp){
                    try {
                        user.setUserSocket(new Socket(s, 12345));
                        Platform.runLater(() -> text.setVisible(false));
                        return;
                    } catch (IOException _) {
                        user.setUserSocket(null);
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException _) {

                }
            }
        };
        preConnectionThread = new Thread(preConnection);
        preConnectionThread.setDaemon(true);
        preConnectionThread.start();
    }
    private void buttonsFunc(TextField usernameField, PasswordField passwordField, boolean isSignIn) {
        if (user.getUserSocket() != null) {
            user.setUsername(usernameField.getText());
            String password = passwordField.getText();
            if (user.getUsername().isEmpty() || password.isEmpty())
                text.setText("Username or password cannot be empty.");
            else if (user.getUsername().matches(".*[^a-zA-Z0-9].*") || password.matches(".*[^a-zA-Z0-9].*"))
                text.setText("Use letters or digits");
            else {
                user.setUserInput(user.getUserSocket());
                user.setUserOutput(user.getUserSocket());
                if (isSignIn) user.getUserOutput().sendMessage("LOGIN," + user.getUsername() + "," + password);
                else user.getUserOutput().sendMessage("SIGNUP," + user.getUsername() + "," + password);
                String response = user.getUserInput().receiveMessage();
                switch (response) {
                    case "ALLOWED":
                        preConnectionThread.interrupt();
                        try {
                            preConnectionThread.join();
                        } catch (InterruptedException e) {
                            System.err.println("Couldn't join " + e.getMessage());
                        }
                        playerLogin.run();
                        return;
                    case "ALREADYLOGGEDIN":
                        text.setText("You are already logged in");
                        break;
                    case "WRONGLOGIN":
                        text.setText("Account doesn't exist");
                        break;
                    case "WRONGPASSWORD":
                        text.setText("Wrong password!");
                        break;
                    default:
                        text.setText("Username already taken!");
                }
                user.closeConnection();
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
    public static List<String> getArpIps() {
        List<String> ipAddresses = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("arp -a");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                for (String part : parts) {
                    if (part.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                        ipAddresses.add(part);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ipAddresses;
    }
    public UserInfo getUser() {
        return user;
    }
}