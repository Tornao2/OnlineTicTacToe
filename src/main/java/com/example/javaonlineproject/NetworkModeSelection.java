package com.example.javaonlineproject;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import static javafx.scene.paint.Color.WHITE;

public class NetworkModeSelection {
    private Runnable onSelectSuccess;
    private boolean isServer;

    private Text createText(String text){
        Text label = new Text(text);
        label.setFont(new Font(16));
        label.setFill(WHITE);
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
    private Button createStartButton (RadioButton serverRadioButton, Text waitingLabel) {
        Button startButton = new Button("Start");
        startButton.setFont(new Font(16));
        startButton.setOnAction(_ -> startButtonFunc(serverRadioButton, waitingLabel));
        return startButton;
    }
    private VBox createVBox() {
        VBox organizer = new VBox(12);
        organizer.setPrefSize(340, 150);
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
        primaryStage.setTitle("ModeSelect");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void start(Stage primaryStage) {
        Text choiceText = createText("Choose Network Mode:");
        Text waitingText = createText("");
        RadioButton serverRadioButton = createRadio("Server");
        RadioButton clientRadioButton = createRadio("Client");
        createToggleGroup(serverRadioButton, clientRadioButton);
        Button startButton = createStartButton(serverRadioButton, waitingText);
        VBox organizer = createVBox();
        organizer.getChildren().addAll(choiceText, serverRadioButton, clientRadioButton, startButton, waitingText);
        BorderPane manager = createManager(organizer);
        manageScene(primaryStage, manager);
    }

    public void startButtonFunc(RadioButton serverRadioButton, Text waitingLabel) {
        isServer = serverRadioButton.isSelected();
        if (isServer) {
            waitingLabel.setVisible(true);
            waitingLabel.setText("No client was available or server already exists");
            PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
            visiblePause.setOnFinished(_ -> waitingLabel.setVisible(false));
            visiblePause.play();
        } else {
            waitingLabel.setVisible(true);
            waitingLabel.setText("No server was available");
            PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
            visiblePause.setOnFinished(_ -> waitingLabel.setVisible(false));
            visiblePause.play();
        }
        onSelectSuccess.run();
    }
    public void setOnStartSuccess(Runnable onSelectSuccess) {
        this.onSelectSuccess = onSelectSuccess;
    }
    public boolean getIsServer() {
        return this.isServer;
    }
}
