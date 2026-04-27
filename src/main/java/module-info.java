module com.oop.gymquest {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens com.oop.gymquest to javafx.fxml;
    opens com.oop.gymquest.app to javafx.fxml;
    opens com.oop.gymquest.screens.dashboard to javafx.fxml;
    opens com.oop.gymquest.screens.profile to javafx.fxml;
    opens com.oop.gymquest.screens.register to javafx.fxml;
    opens com.oop.gymquest.screens.sessions to javafx.fxml;
    opens com.oop.gymquest.screens.workouts to javafx.fxml;

    exports com.oop.gymquest;
    exports com.oop.gymquest.app;
    exports com.oop.gymquest.screens.dashboard;
    exports com.oop.gymquest.screens.profile;
    exports com.oop.gymquest.screens.register;
    exports com.oop.gymquest.screens.sessions;
    exports com.oop.gymquest.screens.workouts;
}