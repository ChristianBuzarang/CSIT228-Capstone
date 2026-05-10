package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.data.userdata.User;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class AdminDashboardController {
    @FXML private Label totalMembersLabel, activeMembersLabel, totalTrainersLabel, activeTrainersLabel;
    @FXML private TableView<User> userTable;
    @FXML private Button btnToggleMembers, btnToggleTrainers, btnToggleAdmins;
    @FXML private TextField searchField;

    private String currentRoleFilter = "member";
    private final String STYLE_ACTIVE = "-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-background-radius: 10; -fx-padding: 8 20; -fx-font-weight: bold;";
    private final String STYLE_INACTIVE = "-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-padding: 8 20; -fx-font-weight: normal;";

    @FXML
    public void initialize() {
        refreshStats();
        showMembers();
    }

    private void refreshStats() {
        if (totalMembersLabel != null) totalMembersLabel.setText(String.valueOf(DatabaseHandler.getCountByType("member")));
        if (activeMembersLabel != null) activeMembersLabel.setText(String.valueOf(DatabaseHandler.getActiveMemberCount()));
        if (totalTrainersLabel != null) totalTrainersLabel.setText(String.valueOf(DatabaseHandler.getCountByType("trainer")));
        if (activeTrainersLabel != null) activeTrainersLabel.setText(String.valueOf(DatabaseHandler.getCountByType("trainer")));
    }

    @FXML public void handleCreateAdmin() {
        try {
            String fxmlPath = "/com/oop/gymquest/fxml/register_admin.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                System.err.println("❌ Critical Error: Could not find FXML at " + fxmlPath);
                return;
            }
            Parent root = loader.load();
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            modalStage.setScene(scene);
            modalStage.showAndWait();
            refreshUserTable();
            refreshStats();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        User currentUser = MainApp.instance.currentUser;
        if (selected == null) return;
        if (selected.getUserId() == currentUser.getUserId()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Security Guard: You cannot archive your own account.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Move " + selected.getFullName() + " to cold storage archive?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (DatabaseHandler.archiveUser(selected.getUserId())) {
                    refreshUserTable();
                    refreshStats();
                }
            }
        });
    }

    @FXML private void showMembers() { updateTable("member", btnToggleMembers); }
    @FXML private void showTrainers() { updateTable("trainer", btnToggleTrainers); }
    @FXML private void showAdmins() { updateTable("admin", btnToggleAdmins); }

    private void updateTable(String type, Button activeBtn) {
        currentRoleFilter = type;
        btnToggleMembers.setStyle(STYLE_INACTIVE);
        btnToggleTrainers.setStyle(STYLE_INACTIVE);
        if (btnToggleAdmins != null) btnToggleAdmins.setStyle(STYLE_INACTIVE);
        activeBtn.setStyle(STYLE_ACTIVE);
        FilteredList<User> list = new FilteredList<>(
                FXCollections.observableArrayList(DatabaseHandler.fetchUsersByRole(type)), p -> true
        );
        searchField.textProperty().addListener((obs, old, newVal) -> {
            list.setPredicate(u -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return u.getFullName().toLowerCase().contains(lower) ||
                        u.getEmail().toLowerCase().contains(lower);
            });
        });
        userTable.setItems(list);
    }

    @FXML private void refreshUserTable() {
        switch (currentRoleFilter) {
            case "admin" -> showAdmins();
            case "trainer" -> showTrainers();
            default -> showMembers();
        }
    }

    @FXML private void handleEditUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a user to edit.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/edit_user.fxml"));
            Parent root = loader.load();
            EditUserController controller = loader.getController();
            controller.setUserData(selected);
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            modalStage.setScene(scene);
            modalStage.showAndWait();
            refreshUserTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}