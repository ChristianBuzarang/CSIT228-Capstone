package com.oop.gymquest.screens.profile;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.data.userdata.User;
import com.oop.gymquest.screens.dashboard.DashboardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ProfileController {
    @FXML private Label nameLabel, roleSubLabel, avatarEmoji, workoutsDoneLabel;
    @FXML private Button editProfileBtn, changeNameBtn;
    @FXML private HBox statsRow;
    @FXML private VBox achievementsSection;
    @FXML private FlowPane badgeContainer;
    @FXML private StackPane avatarContainer;
    @FXML private ImageView profileImageView;

    public static ProfileController instance;

    @FXML
    public void initialize() {
        instance = this;
        User user = MainApp.instance.currentUser;
        if (user == null) return;
        nameLabel.setText(user.getFullName());
        if (user.getAvatar() != null && !user.getAvatar().equals("user.png")) {
            try {
                Image img = new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/" + user.getAvatar()));
                profileImageView.setImage(img);
                avatarEmoji.setVisible(false);
            } catch (Exception e) {
                System.err.println("Could not load user avatar, defaulting to icon.");
            }
        }
        String role = user.getType().toLowerCase();
        if (role.equals("admin")) {
            statsRow.setVisible(false);
            statsRow.setManaged(false);
            achievementsSection.setVisible(false);
            achievementsSection.setManaged(false);
            avatarEmoji.setText("👨‍💼");
            roleSubLabel.setText("System Administrator • Full Access");
        } else if (role.equals("trainer")) {
            workoutsDoneLabel.setText("Sessions Done");
            avatarEmoji.setText("🏋️");
            roleSubLabel.setText("Professional Trainer • Member since Jan 2026");
            loadBadges();
        } else {
            avatarEmoji.setText("🎯");
            roleSubLabel.setText("Fitness Enthusiast • Member since Jan 2026");
            loadBadges();
        }
    }

    @FXML private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/avatar_picker.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.setFill(null);
            modal.setScene(scene);
            modal.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateProfilePicture(String imageName) {
        User currentUser = MainApp.instance.currentUser;
        boolean saved = DatabaseHandler.updateUserAvatar(currentUser.getUserId(), imageName);
        if (saved) {
            currentUser.setAvatar(imageName);
            Image img = new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/" + imageName));
            profileImageView.setImage(img);
            avatarEmoji.setVisible(false);
            if (DashboardController.instance != null) {
                DashboardController.instance.refreshHeader();
            }
            System.out.println("✅ Avatar saved to database: " + imageName);
        } else {
            System.err.println("❌ Failed to save avatar to database.");
        }
    }

    @FXML private void handleChangeName() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/gymquest/fxml/change_name.fxml"));
            javafx.scene.Parent root = loader.load();
            Stage modal = new Stage();
            modal.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            modal.initStyle(javafx.stage.StageStyle.TRANSPARENT);
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(null);
            modal.setScene(scene);
            modal.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshNameLabel() {
        nameLabel.setText(MainApp.instance.currentUser.getFullName());
    }

    private void loadBadges() {
        Object[][] badges = {
                {"👟", "First Steps", "Unlocked"},
                {"🔥", "Week Warrior", "Unlocked"},
                {"🌅", "Early Bird", "Unlocked"},
                {"💪", "Iron Will", "Locked"},
                {"💯", "Century Club", "Locked"}
        };
        for (Object[] b : badges) {
            badgeContainer.getChildren().add(createBadgeCard((String)b[0], (String)b[1], (String)b[2]));
        }
    }

    private VBox createBadgeCard(String emoji, String title, String status) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(140, 140);
        boolean isLocked = status.equals("Locked");
        String style = "-fx-background-color: " + (isLocked ? "#f8fafc" : "#fef3c7") + ";" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: " + (isLocked ? "#e2e8f0" : "#f59e0b") + ";" +
                "-fx-border-width: 2; -fx-border-radius: 15;";
        card.setStyle(style);
        if (isLocked) card.setOpacity(0.6);
        Label icon = new Label(emoji); icon.setStyle("-fx-font-size: 35;");
        Label lblTitle = new Label(title); lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        Label lblStatus = new Label(status); lblStatus.setStyle("-fx-font-size: 11; -fx-text-fill: #64748b;");
        card.getChildren().addAll(icon, lblTitle, lblStatus);
        return card;
    }
}