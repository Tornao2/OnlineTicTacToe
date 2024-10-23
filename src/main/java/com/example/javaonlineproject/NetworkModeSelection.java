package com.example.javaonlineproject;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NetworkModeSelection {

    private boolean isServer;

    public void show(Stage primaryStage, NetworkModeCallback callback) {
        // Create a VBox layout
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        // Create a label
        Label label = new Label("Choose Network Mode:");

        // Create radio buttons
        RadioButton serverRadioButton = new RadioButton("Server");
        RadioButton clientRadioButton = new RadioButton("Client");

        ToggleGroup group = new ToggleGroup();
        serverRadioButton.setToggleGroup(group);
        clientRadioButton.setToggleGroup(group);
        serverRadioButton.setSelected(true);

        Button startButton = new Button("Start");
        startButton.setOnAction(e -> {
            // Sprawdza który 'radio' przycisk jest wybrany
            isServer = serverRadioButton.isSelected();
            callback.onModeSelected(isServer);
        });

        // Add all elements to the layout
        layout.getChildren().addAll(label, serverRadioButton, clientRadioButton, startButton);

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Select Mode");
        primaryStage.show();
    }

    public interface NetworkModeCallback {
        void onModeSelected(boolean isServer);
    }
}
