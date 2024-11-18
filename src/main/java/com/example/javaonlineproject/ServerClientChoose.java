package com.example.javaonlineproject;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ServerClientChoose {

    private boolean isServer; // To store whether the user chose server or client
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(600, 400);

        Text instructionText = new Text("Select if you want to run as a server or client");
        instructionText.setLayoutX(65);
        instructionText.setLayoutY(67);
        instructionText.setFont(new Font(24));

        // Radio buttons for server/client
        ToggleGroup group = new ToggleGroup();

        RadioButton serverRadio = new RadioButton("Server");
        serverRadio.setLayoutX(143);
        serverRadio.setLayoutY(136);
        serverRadio.setFont(new Font(18));
        serverRadio.setToggleGroup(group);

        RadioButton clientRadio = new RadioButton("Client");
        clientRadio.setLayoutX(348);
        clientRadio.setLayoutY(136);
        clientRadio.setFont(new Font(18));
        clientRadio.setToggleGroup(group);

        // Start button
        Button startButton = new Button("Start");
        startButton.setLayoutX(249);
        startButton.setLayoutY(187);
        startButton.setPrefSize(99, 59);
        startButton.setFont(new Font(24));

        // Handle start button click event
        startButton.setOnAction(event -> {
            if (serverRadio.isSelected()) {
                isServer = true;
            } else if (clientRadio.isSelected()) {
                isServer = false;
            }

            // Proceed to the next part of the application, passing the choice
            proceedToGame(primaryStage, isServer);
        });

        root.getChildren().addAll(instructionText, serverRadio, clientRadio, startButton);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Server or Client Choice");
        primaryStage.show();
    }

    // Method to proceed to the game based on the user's choice
    private void proceedToGame(Stage primaryStage, boolean isServer) {
        System.out.println("User chose to run as " + (isServer ? "Server" : "Client"));
        // Here, you would launch the next stage of the application (e.g., initialize the game)
        // You can pass this choice to the next screen or class (e.g., HelloApplication)
        HelloApplication helloApp = new HelloApplication();
        helloApp.initializeGame(primaryStage, isServer);
    }
}
