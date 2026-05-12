package com.oop.gymquest.screens.community;

import com.oop.gymquest.data.DatabaseHandler;
import com.oop.gymquest.data.PostDAO;
import com.oop.gymquest.data.workoutdata.Post;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommunityController {
    @FXML private VBox postContainer;

    private final PostDAO postDAO = new PostDAO();

    @FXML public void initialize() {
        refreshFeed();
    }

    public void refreshFeed() {
        postContainer.getChildren().clear();
        ResultSet rs = DatabaseHandler.fetchPosts();
        try {
            while (rs != null && rs.next()) {
                Post p = new Post(
                        rs.getInt("postid"),
                        rs.getString("firstname") + " " + rs.getString("lastname"),
                        rs.getString("content"),
                        rs.getString("milestone_text"),
                        Post.PostType.GOAL,
                        "Just now",
                        rs.getInt("reactions"),
                        false
                );
                postContainer.getChildren().add(createPostCard(p, rs.getString("avatar")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleShareMilestone() {
        try {
            String fxmlPath = "/com/oop/gymquest/fxml/post.fxml";
            var resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("❌ Error: Could not find FXML file at " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            modalStage.setScene(scene);
            modalStage.showAndWait();
            refreshFeed();
        } catch (IOException e) {
            System.err.println("❌ Failed to load the share modal.");
            e.printStackTrace();
        }
    }

    private VBox createPostCard(Post post, String avatarFileName) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-padding: 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");
        HBox topRow = new HBox(15);
        topRow.setAlignment(Pos.CENTER_LEFT);
        StackPane avatarBox = new StackPane();
        avatarBox.setPrefSize(45, 45);
        avatarBox.setStyle("-fx-background-color: #3b82f6; -fx-background-radius: 25;");
        ImageView userImg = new ImageView();
        try {
            String imgPath = "/com/oop/gymquest/images/" + (avatarFileName != null ? avatarFileName : "user.png");
            userImg.setImage(new Image(getClass().getResourceAsStream(imgPath)));
        } catch (Exception e) {
            userImg.setImage(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/user.png")));
        }
        userImg.setFitHeight(45);
        userImg.setFitWidth(45);
        Circle clip = new Circle(22.5, 22.5, 22.5);
        userImg.setClip(clip);
        avatarBox.getChildren().add(userImg);
        VBox nameBox = new VBox(2);
        Label nameLabel = new Label(post.getUserName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-font-size: 14;");
        Label timeLabel = new Label(post.getTimeAgo());
        timeLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11;");
        nameBox.getChildren().addAll(nameLabel, timeLabel);
        topRow.getChildren().addAll(avatarBox, nameBox);
        Label contentLabel = new Label(post.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-text-fill: #334155; -fx-line-spacing: 3;");
        if (post.getMilestone() != null && !post.getMilestone().isEmpty()) {
            HBox milestone = new HBox(10);
            milestone.setAlignment(Pos.CENTER_LEFT);
            milestone.setPadding(new Insets(10, 15, 10, 15));
            milestone.setStyle("-fx-background-color: #fefce8; -fx-background-radius: 12; -fx-border-color: #fef08a; -fx-border-width: 1; -fx-border-radius: 12;");
            ImageView muscleIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/muscle.png")));
            muscleIcon.setFitHeight(18);
            muscleIcon.setFitWidth(18);
            Label mLabel = new Label(post.getMilestone());
            mLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #854d0e;");
            milestone.getChildren().addAll(muscleIcon, mLabel);
            card.getChildren().addAll(topRow, contentLabel, milestone);
        } else {
            card.getChildren().addAll(topRow, contentLabel);
        }
        Button likeBtn = new Button(String.valueOf(post.getReactions()));
        likeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #64748b; -fx-font-weight: bold;");
        String heartIcon = post.isHasReacted() ? "heart-filled.png" : "heart-empty.png";
        ImageView heart = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/" + heartIcon)));
        heart.setFitHeight(18);
        heart.setFitWidth(18);
        likeBtn.setGraphic(heart);
        likeBtn.setOnAction(e -> {
            postDAO.toggleReaction(post.getId());
            refreshFeed();
        });
        card.getChildren().add(likeBtn);
        return card;
    }
}