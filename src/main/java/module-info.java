module com.example.javaonlineproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.javaonlineproject to javafx.fxml;
    exports com.example.javaonlineproject;
}