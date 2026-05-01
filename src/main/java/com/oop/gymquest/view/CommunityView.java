//package com.oop.gymquest.view;
//
//import com.oop.gymquest.model.AppState;
//import com.oop.gymquest.model.DataStore;
//import com.oop.gymquest.model.Post;
//import com.oop.gymquest.screens.community.CommunityController;
//import com.oop.gymquest.util.UIHelper;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.ScrollPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Region;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.VBox;
//
//public class CommunityView {
//
//    private final ScrollPane root;
//    private final CommunityController controller;
//    private final VBox postContainer; // Added this to allow refreshing posts
//
//    public CommunityView(AppState state, CommunityController controller) {
//        this.controller = controller; // Fixed: Now correctly assigned
//
//        VBox content = new VBox(16);
//        content.setPadding(new Insets(24));
//        content.setMaxWidth(720);
//
//        // Header
//        VBox header = UIHelper.card();
//        HBox hrow = new HBox(20);
//        hrow.setAlignment(Pos.CENTER_LEFT);
//        VBox titleBox = new VBox(4);
//        titleBox.getChildren().addAll(
//                UIHelper.title("Community"),
//                UIHelper.subtitle("Celebrate achievements together!")
//        );
//        Region sp = UIHelper.spacer();
//        Button shareBtn = UIHelper.primaryBtn("+ Share Milestone");
//        shareBtn.setOnAction(e -> showShareDialog());
//        Label mascot = new Label("👥");
//        mascot.setStyle("-fx-font-size: 40px;");
//        hrow.getChildren().addAll(titleBox, sp, shareBtn, mascot);
//        header.getChildren().add(hrow);
//        content.getChildren().add(header);
//
//        // Posts Container
//        this.postContainer = new VBox(16);
//        content.getChildren().add(postContainer);
//
//        // Initial load of posts
//        refreshPosts();
//
//        HBox centered = new HBox(content);
//        centered.setPadding(new Insets(0, 40, 0, 40));
//        centered.setAlignment(Pos.TOP_CENTER);
//
//        root = new ScrollPane(centered);
//        root.setFitToWidth(true);
//        root.setStyle("-fx-background-color: #f0f8ff; -fx-background: #f0f8ff;");
//    }
//
//    // New method: Clears and redraws the posts
//    public void refreshPosts() {
//        postContainer.getChildren().clear();
//        for (Post post : DataStore.getInstance().getPosts()) {
//            postContainer.getChildren().add(buildPostCard(post));
//        }
//    }
//
//    private VBox buildPostCard(Post post) {
//        VBox card = new VBox(10);
//        card.setPadding(new Insets(20));
//        card.setStyle(
//                "-fx-background-color: white; -fx-background-radius: 16;" +
//                        "-fx-border-color: #bae6fd; -fx-border-width: 2;" +
//                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 3);"
//        );
//
//        HBox topRow = new HBox(14);
//        topRow.setAlignment(Pos.TOP_LEFT);
//
//        StackPane avatar = UIHelper.avatarCircle(post.getUserAvatar(), 44, "#3b82f6", "#60a5fa");
//
//        VBox postContent = new VBox(8);
//        HBox nameRow = new HBox(6);
//        Label name = UIHelper.label(post.getUserName());
//        name.setStyle(name.getStyle() + " -fx-font-weight: bold;");
//        Label contentLabel = UIHelper.muted(post.getContent());
//        contentLabel.setStyle(contentLabel.getStyle() + " -fx-font-size: 13px;");
//        nameRow.getChildren().addAll(name, contentLabel);
//
//        if (post.getMilestone() != null) {
//            HBox milestone = new HBox(8);
//            milestone.setPadding(new Insets(8, 14, 8, 14));
//            milestone.setStyle("-fx-background-color: linear-gradient(to right, #fef3c7, #fde68a); -fx-background-radius: 10;");
//            milestone.setAlignment(Pos.CENTER_LEFT);
//
//            String icon = switch (post.getType()) {
//                case STREAK -> "🔥";
//                case WORKOUT -> "🎯";
//                case BADGE -> "🏆";
//                case GOAL -> "✅";
//            };
//            Label iconLabel = new Label(icon);
//            iconLabel.setStyle("-fx-font-size: 16px;");
//            Label milestoneLabel = new Label(post.getMilestone());
//            milestoneLabel.setStyle("-fx-font-size: 18px;");
//            milestone.getChildren().addAll(iconLabel, milestoneLabel);
//            postContent.getChildren().addAll(nameRow, milestone);
//        } else {
//            postContent.getChildren().add(nameRow);
//        }
//
//        HBox actionRow = new HBox(16);
//        actionRow.setAlignment(Pos.CENTER_LEFT);
//
//        Button reactBtn = buildReactButton(post);
//        Label timeLabel = UIHelper.muted(post.getTimeAgo());
//        actionRow.getChildren().addAll(reactBtn, timeLabel);
//        postContent.getChildren().add(actionRow);
//
//        topRow.getChildren().addAll(avatar, postContent);
//        card.getChildren().add(topRow);
//        return card;
//    }
//
//    private Button buildReactButton(Post post) {
//        Button btn = new Button();
//        updateReactBtn(btn, post);
//        return btn;
//    }
//
//    private void updateReactBtn(Button btn, Post post) {
//        btn.setText((post.isHasReacted() ? "❤️" : "🤍") + "  " + post.getReactions());
//        btn.setStyle(
//                "-fx-background-color: " + (post.isHasReacted() ? "#fef3c7" : "#f0f8ff") + ";" +
//                        "-fx-text-fill: " + (post.isHasReacted() ? "#d97706" : "#64748b") + ";" +
//                        "-fx-background-radius: 20; -fx-cursor: hand; -fx-font-size: 13px; -fx-padding: 6 14;" +
//                        (post.isHasReacted() ? "-fx-font-weight: bold;" : "")
//        );
//        // Fixed: Call the controller and refresh the UI
//        btn.setOnAction(e -> {
//            controller.handleToggleReaction(post);
//            updateReactBtn(btn, post);
//        });
//    }
//
//    private void showShareDialog() {
//        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
//        dialog.setTitle("Share Milestone");
//        dialog.setHeaderText("What would you like to share?");
//
//        VBox dialogContent = new VBox(10);
//        Object[][] options = {
//                {"🔥", "Streak Milestone", Post.PostType.STREAK},
//                {"🎯", "Workout Achievement", Post.PostType.WORKOUT},
//                {"🏆", "Badge Earned", Post.PostType.BADGE},
//                {"✅", "Goal Achieved", Post.PostType.GOAL}
//        };
//
//        for (Object[] opt : options) {
//            HBox row = new HBox(12);
//            row.setAlignment(Pos.CENTER_LEFT);
//            row.setPadding(new Insets(10));
//            row.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 10; -fx-cursor: hand;");
//
//            Label icon = new Label((String) opt[0]);
//            icon.setStyle("-fx-font-size: 24px;");
//            VBox info = new VBox(2);
//            Label title = UIHelper.label((String) opt[1]);
//            title.setStyle(title.getStyle() + " -fx-font-weight: bold;");
//            info.getChildren().addAll(title, UIHelper.muted("Share your achievement"));
//
//            row.getChildren().addAll(icon, info);
//
//            // Logic: Click to share
//            row.setOnMouseClicked(e -> {
//                controller.shareMilestone((Post.PostType) opt[2]);
//                dialog.setResult(javafx.scene.control.ButtonType.OK);
//                dialog.close();
//            });
//
//            dialogContent.getChildren().add(row);
//        }
//
//        dialog.getDialogPane().setContent(dialogContent);
//        dialog.showAndWait();
//    }
//
//    public ScrollPane getRoot() { return root; }
//}