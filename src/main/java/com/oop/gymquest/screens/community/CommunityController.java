package com.oop.gymquest.screens.community;

import com.oop.gymquest.model.AppState;
import com.oop.gymquest.model.DataStore;
import com.oop.gymquest.model.Post;
import com.oop.gymquest.view.CommunityView;
import javafx.scene.control.ScrollPane;

public class CommunityController {
    private final AppState state;
    private final CommunityView view;

    public CommunityController(AppState state) {
        this.state = state;
        this.view = new CommunityView(state, this); // 'this' connects the two
    }

    public ScrollPane getView() {
        return view.getRoot();
    }

    public void handleToggleReaction(Post post) {
        post.toggleReaction();
        // UI updates are handled inside the View's button action
    }

    public void shareMilestone(Post.PostType type) {
        // ... (Your switch logic to create milestone/content strings) ...
        String milestone = "Example"; // Simplified for brevity
        String content = "Example content";

        Post newPost = new Post(
                (int)(Math.random() * 10000),
                state.getUsername(),
                getAvatarForUserType(state.getUserType()),
                type,
                content,
                milestone,
                "Just now",
                0,
                false
        );

        DataStore.getInstance().getPosts().add(0, newPost);

        // REFRESH THE VIEW
        view.refreshPosts();
    }

    private String getAvatarForUserType(AppState.UserType type) {
        return switch (type) {
            case ADMIN -> "🛡️";
            case TRAINER -> "🏋️‍♂️";
            case MEMBER -> "👤";
        };
    }
}