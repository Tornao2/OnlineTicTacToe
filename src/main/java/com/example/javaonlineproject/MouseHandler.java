package com.example.javaonlineproject;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class MouseHandler {
    public void setEvents(StackPane root){
        root.setOnMouseClicked((MouseEvent event) -> {
            System.out.println(event.getX()+ " " + event.getY());
        });
    }
}
