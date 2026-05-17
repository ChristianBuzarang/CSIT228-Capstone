module com.oop.gymquest {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires mysql.connector.j;
    requires java.sql;

    opens com.oop.gymquest to javafx.fxml;
    opens com.oop.gymquest.app to javafx.fxml;
    opens com.oop.gymquest.screens.login to javafx.fxml;
    opens com.oop.gymquest.screens.dashboard to javafx.fxml;
    opens com.oop.gymquest.screens.profile to javafx.fxml;
    opens com.oop.gymquest.screens.register to javafx.fxml;
    opens com.oop.gymquest.screens.sessions to javafx.fxml;
    opens com.oop.gymquest.screens.workouts to javafx.fxml;
    opens com.oop.gymquest.screens.booking to javafx.fxml;
    opens com.oop.gymquest.screens.community to javafx.fxml;
    opens com.oop.gymquest.screens.customWorkoutCreator to javafx.fxml;
    opens com.oop.gymquest.screens.exercisePicker to javafx.fxml;
    opens com.oop.gymquest.screens.dashboard.userdashboards to javafx.fxml;
    opens com.oop.gymquest.screens.manageSchedule to javafx.fxml;
    opens com.oop.gymquest.screens.notifications to javafx.fxml;
    opens com.oop.gymquest.data to javafx.base;

    exports com.oop.gymquest;
    exports com.oop.gymquest.app;
    exports com.oop.gymquest.data;
    exports com.oop.gymquest.screens.dashboard;
    exports com.oop.gymquest.screens.profile;
    exports com.oop.gymquest.screens.register;
    exports com.oop.gymquest.screens.sessions;
    exports com.oop.gymquest.screens.workouts;
    exports com.oop.gymquest.screens.booking;
    exports com.oop.gymquest.screens.customWorkoutCreator;
    exports com.oop.gymquest.screens.exercisePicker;
    exports com.oop.gymquest.data.userdata;
    opens com.oop.gymquest.data.userdata to javafx.base;
    exports com.oop.gymquest.data.workoutdata;
    opens com.oop.gymquest.data.workoutdata to javafx.base;
    exports com.oop.gymquest.screens.dashboard.userdashboards;


}