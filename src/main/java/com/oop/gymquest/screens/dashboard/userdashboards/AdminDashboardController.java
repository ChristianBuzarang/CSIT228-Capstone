package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.data.UserDAO;
import com.oop.gymquest.data.userdata.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class AdminDashboardController {

    @FXML private TableView<User> userTable;
    @FXML private Label totalMembersLabel, activeTrainersLabel;

    @FXML
    public void initialize() {
        refreshUserTable();
        updateStats();
    }

    @FXML
    private void refreshUserTable() {
        // Use the CRUD method from UserDAO
        List<User> users = UserDAO.getAll();
        userTable.setItems(FXCollections.observableArrayList(users));
    }

    private void updateStats() {
        List<User> allUsers = UserDAO.getAll();
        long members = allUsers.stream().filter(u -> u.getType().equalsIgnoreCase("member")).count();
        long trainers = allUsers.stream().filter(u -> u.getType().equalsIgnoreCase("trainer")).count();

        totalMembersLabel.setText(String.valueOf(members));
        activeTrainersLabel.setText(String.valueOf(trainers));
    }

    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a user to delete.");
            return;
        }

        // Confirmation Dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + selected.getFirstName()
                + " " + selected.getLastName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                UserDAO.delete(selected.getUserId());
                refreshUserTable();
                updateStats();
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
