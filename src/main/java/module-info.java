module com.oop.gymquest {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.oop.gymquest to javafx.fxml;
    exports com.oop.gymquest;
}