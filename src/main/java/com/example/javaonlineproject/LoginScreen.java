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

/**
 * Klasa LoginScreen zapewnia interfejs użytkownika oraz logikę ekranu logowania i rejestracji.
 * Zarządza procesem połączenia z serwerem oraz weryfikuje dane logowania użytkownika.
 * Klasa umożliwia użytkownikowi zarówno logowanie, jak i rejestrację.
 */
public class LoginScreen {
    /**
     * Funkcja wykonywana jeśli nastąpi poprawne zalogowanie
     */
    private Runnable playerLogin;
    /**
     * Wątek odpowiedzialny za połączenie się z serwerem
     */
    private Thread preConnectionThread;
    /**
     * Obiekt użytkownika
     */
    private UserInfo user = new UserInfo();
    /**
     * Debugowy text
     */
    Text text;
    /**
     * Tworzy widok obrazu logo aplikacji.
     * @return Obiekt ImageView wyświetlający logo.
     */
    private ImageView createLogo() {
        ImageView logoImageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/TictacToe.png"))));
        logoImageView.setFitHeight(200);
        logoImageView.setPreserveRatio(true);
        return logoImageView;
    }
    /**
     * Tworzy pole tekstowe do wprowadzania nazwy użytkownika.
     * @return Obiekt TextField do wprowadzania nazwy użytkownika.
     */
    private TextField createUsernameField() {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setFont(new Font(16));
        usernameField.setStyle("-fx-background-color: #222222; -fx-text-fill: white;");
        return usernameField;
    }
    /**
     * Tworzy pole tekstowe do wprowadzania hasła.
     * @return Obiekt PasswordField do wprowadzania hasła.
     */
    private PasswordField createPassField() {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setFont(new Font(16));
        passwordField.setStyle("-fx-background-color: #222222; -fx-text-fill: white;");
        return passwordField;
    }
    /**
     * Tworzy tekst dla komunikatów o błędach.
     */
    private void createErrorText() {
        text = new Text("");
        text.setFill(WHITE);
        text.setFont(new Font(20));
        text.setVisible(false);
    }
    /**
     * Tworzy przycisk "Zaloguj się" i definiuje jego funkcjonalność.
     * @param usernameField TextField z nazwą użytkownika.
     * @param passwordField PasswordField z hasłem.
     * @return Obiekt Button dla akcji logowania.
     */
    private Button createSignInButton(TextField usernameField, PasswordField passwordField) {
        Button signInButton = new Button("Sign in");
        signInButton.setFont(new Font(16.0));
        signInButton.getStyleClass().add("button");
        signInButton.setOnAction(_ -> buttonsFunc(usernameField, passwordField, true));
        return signInButton;
    }
    /**
     * Tworzy przycisk "Zarejestruj się" i definiuje jego funkcjonalność.
     * @param usernameField TextField z nazwą użytkownika.
     * @param passwordField PasswordField z hasłem.
     * @return Obiekt Button dla akcji rejestracji.
     */
    private Button createSignUpButton(TextField usernameField, PasswordField passwordField){
        Button signUpButton = new Button("Sign up");
        signUpButton.setFont(new Font(16.0));
        signUpButton.getStyleClass().add("button");
        signUpButton.setOnAction(_ -> buttonsFunc(usernameField, passwordField, false));
        return signUpButton;
    }
    /**
     * Tworzy układ VBox dla organizacji elementów.
     * @return Obiekt VBox zawierający elementy interfejsu.
     */
    private VBox createVBox() {
        VBox organizer = new VBox(12);
        organizer.setMinSize(300, 210);
        organizer.setPadding(new Insets(10, 8, 10, 8));
        organizer.setAlignment(Pos.BASELINE_CENTER);
        return organizer;
    }
    /**
     * Tworzy główny układ BorderPane dla ekranu logowania.
     * @param organizer VBox zawierający elementy interfejsu.
     * @return Obiekt BorderPane do organizacji ekranu.
     */
    private BorderPane createManager(VBox organizer) {
        BorderPane root = new BorderPane(organizer);
        root.setStyle("-fx-background-color: #1A1A1A;");
        return root;
    }
    /**
     * Zarządza tworzeniem i wyświetlaniem sceny dla ekranu logowania.
     * @param root Główny układ BorderPane zawierający elementy interfejsu.
     * @param primaryStage Główne okno aplikacji.
     */
    private void manageScene(BorderPane root, Stage primaryStage) {
        Scene scene = new Scene(root, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Screen");
        primaryStage.show();
        root.requestFocus();
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }
    /**
     * Centruje elementy, takie jak logo i tekst błędu, w głównym układzie.
     * @param image Obiekt ImageView wyświetlający logo.
     * @param root Główny układ BorderPane.
     */
    private void centerElements(ImageView image, BorderPane root) {
        image.setX((root.getWidth() - image.getLayoutBounds().getWidth()) / 2);
        text.setX((root.getWidth() - text.getLayoutBounds().getWidth()) / 2);
    }
    /**
     * Uruchamia ekran logowania, inicjalizując wszystkie komponenty i wyświetlając interfejs.
     * @param primaryStage Główne okno aplikacji.
     */
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
    /**
     * Inicjalizuje proces połączenia z serwerem w tle.
     * Próbuje połączyć się z serwerem sprawdzając tabelę w poszukiwaniu dostępnych adresów IP.
     */
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
                    } catch (IOException _) {}
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException _) {}
            }
        };
        preConnectionThread = new Thread(preConnection);
        preConnectionThread.setDaemon(true);
        preConnectionThread.start();
    }
    /**
     * Obsługuje funkcjonalność przycisków (Zaloguj się/Zarejestruj się).
     * Weryfikuje dane użytkownika i komunikuje się z serwerem.
     * @param usernameField TextField zawierający nazwę użytkownika.
     * @param passwordField PasswordField zawierający hasło.
     * @param isSignIn Wartość logiczna, która wskazuje, czy akcja dotyczy logowania czy rejestracji.
     */
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
                if(response == null) {
                    user.closeConnection();
                    System.exit(-2);
                }
                switch (response) {
                    case "SOCKETERROR":
                    case "CLOSING":
                        user.closeConnection();
                        System.exit(-2);
                        return;
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
            }
        }
        text.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(_ -> text.setVisible(false));
        visiblePause.play();
    }
    /**
     * Ustawia callback, który zostanie wywołany po pomyślnym zalogowaniu użytkownika.
     * @param onLogin Runnable, który jest wywoływany po zalogowaniu użytkownika.
     */
    public void setOnLoginPlayer(Runnable onLogin) {
        this.playerLogin = onLogin;
    }
    /**
     * Pobiera listę adresów IP z tabeli ARP.
     * @return Lista adresów IP.
     */
    public static List<String> getArpIps() {
        List<String> ipAddresses = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("arp -a");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                for (String part : parts)
                    if (part.matches("\\d+\\.\\d+\\.\\d+\\.\\d+"))
                        ipAddresses.add(part);
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Couldnt get arp" + e);
        }
        return ipAddresses;
    }
    /**
     * Pobiera obiekt UserInfo zawierający informacje o użytkowniku.
     * @return Obiekt UserInfo.
     */
    public UserInfo getUser() {
        return user;
    }
    /**
     * Ustawia obiekt UserInfo dla tego ekranu.
     * @param userREAD Obiekt UserInfo.
     */
    public void setUser(UserInfo userREAD) {
        user = userREAD;
    }
}


