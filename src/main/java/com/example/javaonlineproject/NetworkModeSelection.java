package com.example.javaonlineproject;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static javafx.scene.paint.Color.WHITE;

public class NetworkModeSelection {
    private Runnable onSelectSuccess;
    private boolean isServer;

    private Label createLabel(){
        Label label = new Label("Choose Network Mode:");
        label.setTextFill(WHITE);
        return label;
    }
    private RadioButton createRadio(String text) {
        RadioButton radioButton = new RadioButton(text);
        radioButton.setTextFill(WHITE);
        return radioButton;
    }
    private void createToggleGroup(RadioButton serverRadioButton, RadioButton clientRadioButton) {
        ToggleGroup group = new ToggleGroup();
        serverRadioButton.setToggleGroup(group);
        clientRadioButton.setToggleGroup(group);
        serverRadioButton.setSelected(true);
    }
    private Button createStartButton (RadioButton serverRadioButton) {
        Button startButton = new Button("Start");
        startButton.setFont(new Font(16));
        startButton.setOnAction(_ -> startButtonFunc(serverRadioButton));
        return startButton;
    }
    private VBox createVBox() {
        VBox organizer = new VBox(12);
        organizer.setPrefSize(300, 150);
        organizer.setPadding(new Insets(8, 8, 10, 8));
        organizer.setAlignment(Pos.BASELINE_CENTER);
        return organizer;
    }
    private BorderPane createManager(VBox organizer) {
        BorderPane root = new BorderPane(organizer);
        root.setStyle("-fx-background-color: #1A1A1A;");
        return root;
    }
    private void manageScene(Stage primaryStage, BorderPane manager) {
        Scene scene = new Scene(manager);
        primaryStage.setTitle("Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void start(Stage primaryStage) {
        Label label = createLabel();
        RadioButton serverRadioButton = createRadio("Server");
        RadioButton clientRadioButton = createRadio("Client");
        createToggleGroup(serverRadioButton, clientRadioButton);
        Button startButton = createStartButton(serverRadioButton);
        VBox organizer = createVBox();
        organizer.getChildren().addAll(label, serverRadioButton, clientRadioButton, startButton);
        BorderPane manager = createManager(organizer);
        manageScene(primaryStage, manager);
    }

    public void startButtonFunc(RadioButton serverRadioButton) {
        isServer = serverRadioButton.isSelected();
        onSelectSuccess.run();
    }
    public void setOnStartSuccess(Runnable onSelectSuccess) {
        this.onSelectSuccess = onSelectSuccess; // Set the callback for login success
    }
    public boolean getIsServer() {
        return this.isServer;
    }
}
