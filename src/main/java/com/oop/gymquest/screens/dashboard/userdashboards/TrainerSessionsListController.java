package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.screens.dashboard.DashboardController;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import java.sql.ResultSet;

public class TrainerSessionsListController {
    @FXML private VBox fullListContainer;

    @FXML
    public void initialize() {
        loadFullSchedule();
    }

    private void loadFullSchedule() {
        fullListContainer.getChildren().clear();
        int tid = MainApp.instance.currentUser.getUserId();

        try (ResultSet rs = DatabaseHandler.getTrainerFullSchedule(tid)) {
            while (rs != null && rs.next()) {
                fullListContainer.getChildren().add(buildClientRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox buildClientRow(ResultSet rs) throws Exception {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 15; -fx-border-color: #f1f5f9; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        VBox sessionInfo = new VBox(5);
        Label activityLabel = new Label(rs.getString("activity"));
        activityLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #1e293b;");

        Label dateTimeLabel = new Label("📅 " + rs.getString("slot_date") + "  at  " + rs.getString("slot_time"));
        dateTimeLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12;");
        sessionInfo.getChildren().addAll(activityLabel, dateTimeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox memberBox = new HBox(12);
        memberBox.setAlignment(Pos.CENTER_RIGHT);

        String status = rs.getString("status");
        if (status.equalsIgnoreCase("Booked")) {
            String firstName = rs.getString("firstname");
            String lastName = rs.getString("lastname");
            String avatarFile = rs.getString("avatar");
            if (avatarFile == null || avatarFile.isEmpty()) avatarFile = "user.png";

            ImageView iv = new ImageView();
            try {
                iv.setImage(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/" + avatarFile)));
            } catch (Exception e) {
                iv.setImage(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/user.png")));
            }
            iv.setFitHeight(35);
            iv.setFitWidth(35);
            iv.setClip(new Circle(17.5, 17.5, 17.5));

            VBox nameBox = new VBox(2);
            nameBox.setAlignment(Pos.CENTER_LEFT);
            Label nameLbl = new Label(firstName + " " + lastName);
            nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #3b82f6; -fx-font-size: 13;");

            String emailStr = "";
            try { emailStr = rs.getString("email"); } catch (Exception e) { emailStr = "No email found"; }
            Label emailLbl = new Label(emailStr);
            emailLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11;");

            nameBox.getChildren().addAll(nameLbl, emailLbl);
            memberBox.getChildren().addAll(iv, nameBox);
        } else {
            Label openLabel = new Label("OPEN SLOT");
            openLabel.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-padding: 5 12; -fx-background-radius: 10; -fx-font-size: 11; -fx-font-weight: bold;");
            memberBox.getChildren().add(openLabel);
        }

        row.getChildren().setAll(sessionInfo, spacer, memberBox);
        return row;
    }

    @FXML private void handleBack() {
        DashboardController.instance.handleNavDashboard();
    }
}