package com.oop.gymquest.screens.community;

import com.oop.gymquest.data.PostDAO;
import com.oop.gymquest.model.Post;
import com.oop.gymquest.model.UserSession;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class CommunityController {
    @FXML private VBox postContainer;
    private final PostDAO postDAO = new PostDAO();

    @FXML
    public void initialize() {
        refreshFeed();
    }

    public void refreshFeed() {
        postContainer.getChildren().clear();
        // Get posts directly from SQL
        for (Post post : postDAO.getAllPosts()) {
            postContainer.getChildren().add(createPostCard(post));
        }
    }

    @FXML
    public void handleShareMilestone() {
        // Instead of AppState, we get the name from our Session
        String currentUsername = UserSession.getCurrentUser().getName();

        // You can pull the milestone data from a UserDAO or WorkoutDAO here
        String milestoneData = "New Achievement";

        Post newPost = new Post(
                0,
                currentUsername,
                "👤",
                Post.PostType.GOAL,
                "just reached a new goal!",
                milestoneData,
                "Just now",
                0,
                false
        );

        postDAO.savePost(newPost);
        refreshFeed();
    }

    private VBox createPostCard(Post post) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Label name = new Label(post.getUserName());
        name.setStyle("-fx-font-weight: bold;");

        Label content = new Label(post.getContent());
        Label milestone = new Label(post.getMilestone());
        milestone.setStyle("-fx-background-color: #fef3c7; -fx-padding: 5;");

        card.getChildren().addAll(name, content, milestone);
        return card;
    }
}