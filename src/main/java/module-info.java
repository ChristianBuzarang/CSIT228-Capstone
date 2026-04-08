module com.oop.gymquest {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.oop.gymquest to javafx.fxml;
    exports com.oop.gymquest;
}