package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.data.userdata.User;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminDashboardController {
    @FXML private Label totalMembersLabel, activeMembersLabel, totalTrainersLabel, activeTrainersLabel;
    @FXML private TableView<User> userTable;
    @FXML private Button btnToggleMembers, btnToggleTrainers;
    @FXML private TextField searchField;

    @FXML
    public void initialize() {
        totalMembersLabel.setText(String.valueOf(DatabaseHandler.getCountByType("member")));
        activeMembersLabel.setText(String.valueOf(DatabaseHandler.getActiveMemberCount()));
        totalTrainersLabel.setText(String.valueOf(DatabaseHandler.getCountByType("trainer")));
        activeTrainersLabel.setText(totalTrainersLabel.getText());
        showMembers();
    }

    // FIX: Added the method missing from Namespace
    @FXML
    public void handleCreateAdmin() {
        // Logic to open Admin Registration FXML
        MainApp.instance.changeScene("register_admin.fxml", "GymQuest - Create Admin");
    }

    @FXML private void showMembers() { updateTable("member", btnToggleMembers, btnToggleTrainers); }
    @FXML private void showTrainers() { updateTable("trainer", btnToggleTrainers, btnToggleMembers); }

    private void updateTable(String type, Button active, Button inactive) {
        active.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 20; -fx-font-weight: bold;");
        inactive.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-padding: 8 20;");

        FilteredList<User> list = new FilteredList<>(FXCollections.observableArrayList(DatabaseHandler.fetchUsersByRole(type)), p -> true);
        searchField.textProperty().addListener((obs, old, newVal) -> {
            list.setPredicate(u -> newVal == null || newVal.isEmpty() || u.getFullName().toLowerCase().contains(newVal.toLowerCase()));
        });
        userTable.setItems(list);
    }
}