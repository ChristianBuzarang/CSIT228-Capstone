package com.oop.gymquest.screens.manageSchedule;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.sql.ResultSet;

public class ManageScheduleController {
    @FXML
    private Label countLabel;
    @FXML
    private VBox emptyPlaceholder;
    @FXML
    private VBox scheduleList;

    @FXML
    public void initialize() {
        refreshView();
    }

    public void refreshView() {
        scheduleList.getChildren().clear();
        int tid = MainApp.instance.currentUser.getUserId();
        int count = 0;
        try (ResultSet rs = DatabaseHandler.getTrainerSchedule(tid)) {
            while (rs != null && rs.next()) {
                count++;
                addCard(
                        rs.getInt("slot_id"),
                        rs.getString("activity"),
                        rs.getString("slot_date"),
                        rs.getString("slot_time"),
                        rs.getString("duration"),
                        rs.getString("status")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        countLabel.setText("Your Schedules (" + count + ")");
        boolean isEmpty = (count == 0);
        emptyPlaceholder.setVisible(isEmpty);
        emptyPlaceholder.setManaged(isEmpty);
    }

    private void addCard(int id, String act, String date, String time, String dur, String status) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("card");
        row.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(50, 50); // Equal width and height
        iconBox.setMinSize(50, 50);
        iconBox.setMaxSize(50, 50);
        iconBox.setStyle("-fx-background-color: #3b82f6; -fx-background-radius: 25;");

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/calendar.png")));
        icon.setFitHeight(22);
        icon.setFitWidth(22);
        iconBox.getChildren().add(icon);

        VBox content = new VBox(8);
        Label title = new Label(act);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        Label details = new Label(date + "  •  " + time + " (" + dur + ")");
        details.setStyle("-fx-text-fill: #64748b;");

        HBox badgeRow = new HBox(10);
        badgeRow.setAlignment(Pos.CENTER_LEFT);

        Label memberCount = new Label("👥 0/5 members");
        memberCount.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 13;");

        Label statusBadge = new Label(status.toUpperCase());
        statusBadge.getStyleClass().add(status.equalsIgnoreCase("Available") ? "badge-available" : "badge-booked");

        badgeRow.getChildren().addAll(memberCount, statusBadge);
        content.getChildren().addAll(title, details, badgeRow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button delBtn = new Button();
        ImageView trash = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/check.png")));
        trash.setFitHeight(18);
        trash.setFitWidth(18);
        delBtn.setGraphic(trash);
        delBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        delBtn.setOnAction(e -> {
            if (DatabaseHandler.deleteSlot(id)) {
                refreshView();
            }
        });

        row.getChildren().addAll(iconBox, content, spacer, delBtn);
        scheduleList.getChildren().add(row);
    }

    @FXML
    private void handleAddSchedule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/add_schedule_dialog.fxml"));
            Parent root = loader.load();
            AddScheduleDialogController controller = loader.getController();
            controller.setParent(this);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            scene.setFill(null);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}