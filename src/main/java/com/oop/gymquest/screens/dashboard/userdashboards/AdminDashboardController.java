package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.UserDAO;
import com.oop.gymquest.data.userdata.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
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
        User currentUser = MainApp.instance.currentUser;
        if (selected == null) return;
        // Guard - Can't delete self
        if (selected.getUserId() == currentUser.getUserId()) {
            showAlert("Access Denied", "For security reasons, you cannot archive your own admin account.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Move " + selected.getFullName() + " to cold storage archive?",
                ButtonType.YES, ButtonType.NO);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean success = UserDAO.archiveUser(selected.getUserId());
                if (success) {
                    refreshUserTable();
                }
            }
        });
    }

    @FXML
    private void handleCreateAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/register_admin.fxml"));
            Parent root = loader.load();
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.setFill(null);
            modalStage.setScene(scene);
            modalStage.showAndWait();
            refreshUserTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
