module com.example.javaonlineproject {
        requires java.base;
        requires org.junit.jupiter.api;
        requires org.mockito;
        requires org.slf4j;
        requires com.fasterxml.jackson.core;
        requires com.fasterxml.jackson.databind;
        requires javafx.controls;
        requires javafx.fxml;

        exports com.example.javaonlineproject; // eksportowanie pakietu
        }
