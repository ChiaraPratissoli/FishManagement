module com.example.fishmanagement {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.fishmanagement to javafx.fxml;
    exports com.example.fishmanagement;
}