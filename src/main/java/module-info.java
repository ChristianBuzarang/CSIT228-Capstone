module com.oop.gymquest {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens com.oop.gymquest to javafx.fxml;
    opens com.oop.gymquest.app to javafx.fxml;

    // New
//    opens com.oop.gymquest.controller to javafx.fxml;
//    opens com.oop.gymquest.view to javafx.fxml;
//    opens com.oop.gymquest.model to javafx.base;
    opens com.oop.gymquest.screens.login to javafx.fxml;

    // Previous
    opens com.oop.gymquest.screens.dashboard to javafx.fxml;
    opens com.oop.gymquest.screens.profile to javafx.fxml;
    opens com.oop.gymquest.screens.register to javafx.fxml;
    opens com.oop.gymquest.screens.sessions to javafx.fxml;
    opens com.oop.gymquest.screens.workouts to javafx.fxml;
//    opens com.oop.gymquest.screens.community to javafx.fxml;

    exports com.oop.gymquest;
    exports com.oop.gymquest.app;
    exports com.oop.gymquest.screens.dashboard;
    exports com.oop.gymquest.screens.profile;
    exports com.oop.gymquest.screens.register;
    exports com.oop.gymquest.screens.sessions;
    exports com.oop.gymquest.screens.workouts;
//    exports com.oop.gymquest.screens.community;

    // New
//    exports com.oop.gymquest.controller;
//    exports com.oop.gymquest.model;
//    exports com.oop.gymquest.view;
//    exports com.oop.gymquest.util;

}