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

    private Runnable onLoginSuccess; // Callback for login success

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(401, 400);
        root.setStyle("-fx-background-color: #1A1A1A;");

        // Logo
        ImageView logoImageView = new ImageView();
        Image logoImage = new Image(getClass().getResourceAsStream("/images/TictacToe.png"));
        logoImageView.setImage(logoImage);
        logoImageView.setFitHeight(182);
        logoImageView.setFitWidth(265);
        logoImageView.setLayoutX(69);
        logoImageView.setLayoutY(14);
        logoImageView.setPreserveRatio(true);

        // Username and Password Fields
        TextField usernameField = new TextField();
        usernameField.setLayoutX(101);
        usernameField.setLayoutY(165);
        usernameField.setPrefHeight(26);
        usernameField.setPrefWidth(200);
        usernameField.setPromptText("Username");
        usernameField.setFont(new Font(16));
        usernameField.setStyle("-fx-background-color: #222222;");

        PasswordField passwordField = new PasswordField();
        passwordField.setLayoutX(100);
        passwordField.setLayoutY(215);
        passwordField.setPrefHeight(26);
        passwordField.setPrefWidth(200);
        passwordField.setPromptText("Password");
        passwordField.setFont(new Font(16));
        passwordField.setStyle("-fx-background-color: #222222;");

        // Login Button
        Button loginButton = new Button("Login");
        loginButton.setLayoutX(168);
        loginButton.setLayoutY(280);
        loginButton.setStyle("-fx-background-color: #FFFFFF;");
        loginButton.setFont(new Font("System Bold", 13));

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Login Error", "Username or password cannot be empty.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Login Success", "Login successful!");
                System.out.println("Login successful, transitioning to menu.");
                if (onLoginSuccess != null) {
                    onLoginSuccess.run(); // Trigger the login success callback
                } else {
                    System.out.println("Login success callback is null"); // Debug line
                }
            }
        });


        root.getChildren().addAll(logoImageView, usernameField, passwordField, loginButton);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Screen");
        primaryStage.show();
    }

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess; // Set the callback for login success
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


/*<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.Button?>
<?import javafx.scene.control.CheckBox?>
<?import javafx.scene.control.PasswordField?>
<?import javafx.scene.control.TextField?>
<?import javafx.scene.image.Image?>
<?import javafx.scene.image.ImageView?>
<?import javafx.scene.layout.AnchorPane?>
<?import javafx.scene.text.Font?>

<AnchorPane prefHeight="400.0" prefWidth="401.0" style="-fx-background-color: #1A1A1A;" xmlns="http://javafx.com/javafx/23.0.1" xmlns:fx="http://javafx.com/fxml/1" fx:controller="com.example.javaonlineproject.LoginScreen">
    <children>
        <ImageView fitHeight="182.0" fitWidth="265.0" layoutX="69.0" layoutY="14.0" pickOnBounds="true" preserveRatio="true">
            <image>
                <Image url="@../../../../../../../../OneDrive/Pulpit/ubuntu%20i%20javafxml/TictacToe.png" />
            </image>
        </ImageView>
        <TextField fx:id="usernameField" layoutX="101.0" layoutY="165.0" prefHeight="26.0" prefWidth="200.0" promptText="Username" style="-fx-background-color: #222222;">
            <font>
                <Font size="16.0" />
            </font>
        </TextField>
        <PasswordField fx:id="passwordField" layoutX="100.0" layoutY="215.0" prefHeight="26.0" prefWidth="200.0" promptText="Password" style="-fx-background-color: #222222;">
            <font>
                <Font size="16.0" />
            </font>
        </PasswordField>
        <TextField fx:id="showPasswordField" layoutX="100.0" layoutY="215.0" prefHeight="26.0" prefWidth="200.0" style="-fx-background-color: #222222;">
            <font>
                <Font size="16.0" />
            </font>
        </TextField>
        <CheckBox fx:id="showPasswordCheckBox" layoutX="100.0" layoutY="258.0" mnemonicParsing="false" text="Show Password" textFill="WHITE">
            <font>
                <Font size="8.0" />
            </font>
        </CheckBox>
        <Button fx:id="loginButton" layoutX="168.0" layoutY="280.0" mnemonicParsing="false" style="-fx-background-color: #FFFFFF;" text="Login">
            <font>
                <Font name="System Bold" size="13.0" />
            </font>
        </Button>
        <Button fx:id="createAccountButton" layoutX="144.0" layoutY="348.0" mnemonicParsing="false" prefHeight="27.0" prefWidth="114.0" style="-fx-background-color: #FFFFFF;" text="Create Account">
            <font>
                <Font name="System Bold" size="13.0" />
            </font>
        </Button>
    </children>
</AnchorPane>
*/