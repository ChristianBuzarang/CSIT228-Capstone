package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.data.userdata.User;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class AdminDashboardController {
    @FXML private Label totalMembersLabel, activeMembersLabel, totalTrainersLabel, activeTrainersLabel;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Void> actionsCol;
    @FXML private Button btnToggleMembers, btnToggleTrainers, btnToggleAdmins, btnToggleArchive;
    @FXML private TextField searchField;

    private String currentRoleFilter = "member";
    private FilteredList<User> filteredList;

    @FXML
    public void initialize() {
        setupActionsColumn();
        setupSearchLogic();
        refreshStats();
        showMembers();
    }

    private void setupActionsColumn() {
        actionsCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button editBtn = new Button("Update");
                    private final Button actionBtn = new Button();
                    private final HBox container = new HBox(10, editBtn, actionBtn);
                    {
                        container.setAlignment(Pos.CENTER);
                        editBtn.getStyleClass().add("action-btn-update");
                        actionBtn.setFocusTraversable(false);
                        editBtn.setFocusTraversable(false);
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            User user = getTableView().getItems().get(getIndex());
                            if (user.isActive()) {
                                actionBtn.setText("Archive");
                                actionBtn.getStyleClass().setAll("action-btn-archive");
                                actionBtn.setOnAction(e -> handleToggleStatus(user, false));
                            } else {
                                actionBtn.setText("Restore");
                                actionBtn.getStyleClass().setAll("action-btn-restore");
                                actionBtn.setOnAction(e -> handleToggleStatus(user, true));
                            }
                            editBtn.setOnAction(event -> handleEditUser(user));
                            setGraphic(container);
                        }
                    }
                };
            }
        });
    }

    private void handleToggleStatus(User selected, boolean activate) {
        if (selected == null) return;
        if (!activate && selected.getUserId() == MainApp.instance.currentUser.getUserId()) {
            new Alert(Alert.AlertType.ERROR, "Security Guard: You cannot archive your own account.", ButtonType.OK).showAndWait();
            return;
        }
        String verb = activate ? "Restore" : "Archive";
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, verb + " " + selected.getFullName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean success;
                if (activate) {
                    success = DatabaseHandler.restoreUser(selected.getUserId());
                } else {
                    success = DatabaseHandler.archiveUser(selected.getUserId());
                }

                if (success) {
                    refreshUserTable();
                    refreshStats();
                }
            }
        });
    }

    private void setupSearchLogic() {
        searchField.textProperty().addListener((obs, old, newVal) -> applyFilter(newVal));
    }

    private void applyFilter(String text) {
        if (filteredList != null) {
            final String lower = text == null ? "" : text.toLowerCase().trim();
            filteredList.setPredicate(user -> {
                if (lower.isEmpty()) return true;
                return user.getFullName().toLowerCase().contains(lower) ||
                        user.getEmail().toLowerCase().contains(lower);
            });
        }
    }

    private void updateToggleStyles(Button active) {
        btnToggleMembers.getStyleClass().remove("toggle-nav-btn-active");
        btnToggleTrainers.getStyleClass().remove("toggle-nav-btn-active");
        btnToggleAdmins.getStyleClass().remove("toggle-nav-btn-active");
        btnToggleArchive.getStyleClass().remove("toggle-nav-btn-active");
        active.getStyleClass().add("toggle-nav-btn-active");
    }

    private void refreshStats() {
        totalMembersLabel.setText(String.valueOf(DatabaseHandler.getCountByType("member")));
        activeMembersLabel.setText(String.valueOf(DatabaseHandler.getActiveMemberCount()));
        totalTrainersLabel.setText(String.valueOf(DatabaseHandler.getCountByType("trainer")));
        activeTrainersLabel.setText(String.valueOf(DatabaseHandler.getCountByType("trainer")));
    }

    @FXML public void handleCreateAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/register_admin.fxml"));
            Parent root = loader.load();
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root); scene.setFill(Color.TRANSPARENT);
            modalStage.setScene(scene); modalStage.showAndWait();
            refreshUserTable(); refreshStats();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void handleDeleteUser(User selected) {
        if (selected == null) return;
        if (selected.getUserId() == MainApp.instance.currentUser.getUserId()) {
            new Alert(Alert.AlertType.ERROR, "You cannot archive your own account.", ButtonType.OK).showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Archive " + selected.getFullName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (DatabaseHandler.archiveUser(selected.getUserId())) {
                    refreshUserTable(); refreshStats();
                }
            }
        });
    }

    private void handleEditUser(User selected) {
        if (selected == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/edit_user.fxml"));
            Parent root = loader.load();
            EditUserController controller = loader.getController();
            controller.setUserData(selected);
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root); scene.setFill(Color.TRANSPARENT);
            modalStage.setScene(scene); modalStage.showAndWait();
            refreshUserTable();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void showMembers() { updateToggleStyles(btnToggleMembers); updateTable("member", true); }
    @FXML private void showTrainers() { updateToggleStyles(btnToggleTrainers); updateTable("trainer", true); }
    @FXML private void showAdmins() { updateToggleStyles(btnToggleAdmins); updateTable("admin", true); }
    @FXML private void showArchive() {
        currentRoleFilter = "archive";
        updateToggleStyles(btnToggleArchive);
        updateTable("archive", false);
    }

    private void updateTable(String type, boolean active) {
        currentRoleFilter = type;
        List<User> data = DatabaseHandler.fetchUsersByStatus(type, active);
        filteredList = new FilteredList<>(FXCollections.observableArrayList(data), p -> true);
        userTable.setItems(filteredList);
        applyFilter(searchField.getText());
    }

    @FXML private void refreshUserTable() {
        switch (currentRoleFilter) {
            case "admin" -> showAdmins();
            case "trainer" -> showTrainers();
            case "archive" -> showArchive();
            default -> showMembers();
        }
    }
}