package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.data.userdata.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditUserController {
    @FXML private TextField firstnameField, lastnameField, emailField;
    @FXML private ComboBox<String> typeBox;
    @FXML private Label statusLabel;

    private int selectedUserId;

    @FXML public void initialize() {
        if (typeBox != null) typeBox.getItems().addAll("member", "trainer", "admin");
    }

    public void setUserData(User user) {
        selectedUserId = user.getUserId();
        firstnameField.setText(user.getFirstName());
        lastnameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        typeBox.setValue(user.getType().toLowerCase());
    }

    @FXML private void handleUpdate() {
        boolean success = DatabaseHandler.updateUser(
                selectedUserId,
                emailField.getText(),
                firstnameField.getText(),
                lastnameField.getText(),
                typeBox.getValue()
        );
        if (success) ((Stage) emailField.getScene().getWindow()).close();
        else statusLabel.setText("Update failed. Email might be in use.");
    }

    @FXML private void handleCancel() {
        ((Stage) emailField.getScene().getWindow()).close();
    }
}