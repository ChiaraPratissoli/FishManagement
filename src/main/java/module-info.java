module com.example.pescevia {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.sql;


    opens com.example.pescevia.controller to javafx.fxml;
    opens com.example.pescevia.model to javafx.base, javafx.fxml;
    exports com.example.pescevia;
}