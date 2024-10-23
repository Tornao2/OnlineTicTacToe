package com.example.javaonlineproject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginScreen extends Application {

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(401, 400);
        root.setStyle("-fx-background-color: #1A1A1A;");

        ImageView logoImageView = new ImageView();
        Image logoImage = new Image(getClass().getResourceAsStream("/images/TictacToe.png"));
        logoImageView.setImage(logoImage);
        logoImageView.setFitHeight(182);
        logoImageView.setFitWidth(265);
        logoImageView.setLayoutX(69);
        logoImageView.setLayoutY(14);
        logoImageView.setPreserveRatio(true);


        // Create Username TextField
        TextField usernameField = new TextField();
        usernameField.setLayoutX(101);
        usernameField.setLayoutY(165);
        usernameField.setPrefHeight(26);
        usernameField.setPrefWidth(200);
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-background-color: #222222;");
        usernameField.setFont(new Font(16));

        // Create Password
        PasswordField passwordField = new PasswordField();
        passwordField.setLayoutX(100);
        passwordField.setLayoutY(215);
        passwordField.setPrefHeight(26);
        passwordField.setPrefWidth(200);
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-background-color: #222222;");
        passwordField.setFont(new Font(16));

        // Show Password TextField
        TextField showPasswordField = new TextField();
        showPasswordField.setLayoutX(100);
        showPasswordField.setLayoutY(215);
        showPasswordField.setPrefHeight(26);
        showPasswordField.setPrefWidth(200);
        showPasswordField.setStyle("-fx-background-color: #222222;");
        showPasswordField.setFont(new Font(16));
        showPasswordField.setManaged(false); // Initially hidden
        showPasswordField.setVisible(false); // Initially hidden

        // Show password
        CheckBox showPasswordCheckBox = new CheckBox("Show Password");
        showPasswordCheckBox.setLayoutX(100);
        showPasswordCheckBox.setLayoutY(258);
        showPasswordCheckBox.setTextFill(javafx.scene.paint.Color.WHITE);
        showPasswordCheckBox.setFont(new Font(8));

        // Widoczność hasłą
        showPasswordCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                showPasswordField.setText(passwordField.getText());
                showPasswordField.setVisible(true);
                showPasswordField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
            } else {
                passwordField.setText(showPasswordField.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                showPasswordField.setVisible(false);
                showPasswordField.setManaged(false);
            }
        });

        // Create Login Button
        Button loginButton = new Button("Login");
        loginButton.setLayoutX(168);
        loginButton.setLayoutY(280);
        loginButton.setStyle("-fx-background-color: #FFFFFF;");
        loginButton.setFont(new Font("System Bold", 13));

        // Handle Login Button Click
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.isVisible() ? passwordField.getText() : showPasswordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Login Error", "Username or password cannot be empty.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Login Success", "Login successful!");
            }
        });

        // Create Create Account Button
        Button createAccountButton = new Button("Create Account");
        createAccountButton.setLayoutX(144);
        createAccountButton.setLayoutY(348);
        createAccountButton.setPrefHeight(27);
        createAccountButton.setPrefWidth(114);
        createAccountButton.setStyle("-fx-background-color: #FFFFFF;");
        createAccountButton.setFont(new Font("System Bold", 13));

        createAccountButton.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "Create Account", "Create account clicked!"));

        root.getChildren().addAll(logoImageView, usernameField, passwordField, showPasswordField, showPasswordCheckBox, loginButton, createAccountButton);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Screen");
        primaryStage.show();
    }

    // Alerty
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
