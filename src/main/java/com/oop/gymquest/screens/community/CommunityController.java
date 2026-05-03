package com.oop.gymquest.screens.community;

import com.oop.gymquest.app.MainApp;
import com.oop.gymquest.data.PostDAO;
import com.oop.gymquest.data.workoutdata.Post;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.util.List;

public class CommunityController {
    @FXML
    private VBox postContainer;

    private final PostDAO postDAO = new PostDAO();

    @FXML
    public void initialize() {
        refreshFeed();
    }

    public void refreshFeed() {
        postContainer.getChildren().clear();
        List<Post> posts = postDAO.getAllPosts();
        for (Post post : posts) {
            VBox card = createPostCard(post);
            postContainer.getChildren().add(card);
        }
    }

    @FXML
    public void handleShareMilestone() {
        if (MainApp.instance.currentUser == null) {
            return;
        }
        String currentName = MainApp.instance.currentUser.getFullName();
        Post newPost = new Post(
                0,
                currentName,
                "just reached a new goal!",
                "10km Run Completed",
                Post.PostType.GOAL,
                "Just now",
                0,
                false
        );
        postDAO.savePost(newPost);
        refreshFeed();
    }

    private VBox createPostCard(Post post) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");

        HBox topRow = new HBox(15);
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane avatarBox = new StackPane();
        avatarBox.getStyleClass().add("profile-image-container");
        avatarBox.setPrefSize(40, 40);
        ImageView userImg = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/user.png")));
        userImg.setFitHeight(25);
        userImg.setFitWidth(25);
        avatarBox.getChildren().add(userImg);

        VBox nameBox = new VBox(2);
        Label nameLabel = new Label(post.getUserName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
        Label timeLabel = new Label(post.getTimeAgo());
        timeLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11;");
        nameBox.getChildren().addAll(nameLabel, timeLabel);
        topRow.getChildren().addAll(avatarBox, nameBox);

        Label contentLabel = new Label(post.getContent());
        contentLabel.setStyle("-fx-text-fill: black;");

        HBox milestone = new HBox(10);
        milestone.setAlignment(Pos.CENTER_LEFT);
        milestone.setPadding(new Insets(10));
        milestone.setStyle("-fx-background-color: #fef9c3; -fx-background-radius: 10;");
        ImageView muscleIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/muscle.png")));
        muscleIcon.setFitHeight(18);
        muscleIcon.setFitWidth(18);
        Label mLabel = new Label(post.getMilestone());
        mLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
        milestone.getChildren().addAll(muscleIcon, mLabel);

        Button likeBtn = new Button(String.valueOf(post.getReactions()));
        likeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        ImageView heart = new ImageView(new Image(getClass().getResourceAsStream("/com/oop/gymquest/images/heart-empty.png")));
        heart.setFitHeight(18);
        heart.setFitWidth(18);
        likeBtn.setGraphic(heart);
        likeBtn.setOnAction(e -> {
            postDAO.toggleReaction(post.getId());
            refreshFeed();
        });

        card.getChildren().addAll(topRow, contentLabel, milestone, likeBtn);
        return card;
    }
}