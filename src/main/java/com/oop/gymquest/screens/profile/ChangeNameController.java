package com.oop.gymquest.screens.profile;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.screens.dashboard.DashboardController;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChangeNameController {
    @FXML private TextField firstNameField, lastNameField;

    @FXML public void initialize() {
        firstNameField.setText(MainApp.instance.currentUser.getFirstName());
        lastNameField.setText(MainApp.instance.currentUser.getLastName());
    }

    @FXML private void handleSave() {
        String fn = firstNameField.getText().trim();
        String ln = lastNameField.getText().trim();
        if (DatabaseHandler.updateUserName(MainApp.instance.currentUser.getUserId(), fn, ln)) {
            MainApp.instance.currentUser.setFirstName(fn);
            MainApp.instance.currentUser.setLastName(ln);
            ProfileController.instance.refreshNameLabel();
            if (DashboardController.instance != null) {
                DashboardController.instance.refreshHeader();
            }
            handleCancel();
        }
    }

    @FXML private void handleCancel() {
        ((Stage) firstNameField.getScene().getWindow()).close();
    }
}