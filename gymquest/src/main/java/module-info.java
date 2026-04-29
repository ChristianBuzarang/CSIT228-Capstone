module com.gymquest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.gymquest to javafx.fxml;
    opens com.gymquest.controller to javafx.fxml;
    opens com.gymquest.view to javafx.fxml;
    opens com.gymquest.model to javafx.base;

    exports com.gymquest;
    exports com.gymquest.model;
    exports com.gymquest.view;
    exports com.gymquest.controller;
    exports com.gymquest.util;
}
