package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.exceptions.MemberNotFoundException;
import com.oop.gymquest.screens.dashboard.DashboardController;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TrainerClientsListController {

    @FXML private VBox clientListContainer;
    @FXML private TextField searchField;

    @FXML
    public void initialize() {
        loadClients("");
        searchField.textProperty().addListener((obs, old, newVal) -> loadClients(newVal));
    }

    private void loadClients(String filter) {
        clientListContainer.getChildren().clear();
        int trainerId = MainApp.instance.currentUser.getUserId();

        String sql = "SELECT DISTINCT u.userid, u.firstname, u.lastname, u.email, u.avatar " +
                "FROM users u " +
                "JOIN trainer_slots s ON u.userid = s.member_id " +
                "WHERE s.trainer_id = ? AND (u.firstname LIKE ? OR u.lastname LIKE ?)";

        try (java.sql.Connection conn = DatabaseHandler.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainerId);
            ps.setString(2, "%" + filter + "%");
            ps.setString(3, "%" + filter + "%");
            ResultSet rs = ps.executeQuery();
            boolean foundAny = false;
            while (rs.next()) {
                foundAny = true;
                clientListContainer.getChildren().add(buildClientRow(rs));
            }
            if (!foundAny && !filter.isEmpty()) throw new MemberNotFoundException(filter);
        } catch (com.oop.gymquest.exceptions.MemberNotFoundException ex) {
            System.err.println(ex.getMessage());
            Label notFoundLabel = new Label("No clients found matching: '" + filter + "'");
            notFoundLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-padding: 20;");
            clientListContainer.getChildren().add(notFoundLabel);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private HBox buildClientRow(ResultSet rs) throws Exception {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f8fafc; -fx-padding: 15; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-width: 1;");

        String avatarFile = rs.getString("avatar");
        if (avatarFile == null || avatarFile.isEmpty()) avatarFile = "user.png";

        ImageView iv = new ImageView();
        try {
            iv.setImage(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/" + avatarFile)));
        } catch (Exception e) {
            iv.setImage(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/user.png")));
        }
        iv.setFitHeight(40);
        iv.setFitWidth(40);
        iv.setClip(new Circle(20, 20, 20));

        VBox info = new VBox(2);
        Label name = new Label(rs.getString("firstname") + " " + rs.getString("lastname"));
        name.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label email = new Label(rs.getString("email"));
        email.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12;");

        info.getChildren().setAll(name, email);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().setAll(iv, info, spacer);
        return row;
    }

    @FXML
    private void handleBack() {
        DashboardController.instance.handleNavDashboard();
    }
}