package com.example.javaonlineproject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ServerClientChoose extends Application {

    @Override
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

        root.getChildren().addAll(instructionText, serverRadio, clientRadio, startButton);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Server or Client Choice");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
/*
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.Button?>
<?import javafx.scene.control.RadioButton?>
<?import javafx.scene.control.TitledPane?>
<?import javafx.scene.layout.AnchorPane?>
<?import javafx.scene.text.Font?>
<?import javafx.scene.text.Text?>

<TitledPane animated="false" maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="400.0" prefWidth="600.0" text="untitled" xmlns:fx="http://javafx.com/fxml/1" xmlns="http://javafx.com/javafx/23.0.1">
  <content>
    <AnchorPane minHeight="0.0" minWidth="0.0" prefHeight="180.0" prefWidth="200.0">
         <children>
            <Text layoutX="65.0" layoutY="67.0" strokeType="OUTSIDE" strokeWidth="0.0" text="Select if you want to run as a server or client">
               <font>
                  <Font size="24.0" />
               </font>
            </Text>
            <RadioButton layoutX="143.0" layoutY="136.0" mnemonicParsing="false" prefHeight="50.0" prefWidth="105.0" text="Server">
               <font>
                  <Font size="18.0" />
               </font>
            </RadioButton>
            <RadioButton layoutX="348.0" layoutY="136.0" mnemonicParsing="false" prefHeight="50.0" prefWidth="105.0" text="Client">
               <font>
                  <Font size="18.0" />
               </font>
            </RadioButton>
            <Button layoutX="249.0" layoutY="187.0" mnemonicParsing="false" prefHeight="59.0" prefWidth="99.0" text="Start">
               <font>
                  <Font size="24.0" />
               </font>
            </Button>
         </children></AnchorPane>
  </content>
</TitledPane>

 */