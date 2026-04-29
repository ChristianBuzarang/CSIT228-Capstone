package com.gymquest.model;

public class Post {
    public enum PostType { STREAK, WORKOUT, BADGE, GOAL }

    private int id;
    private String userName;
    private String userAvatar;
    private PostType type;
    private String content;
    private String milestone;
    private String timeAgo;
    private int reactions;
    private boolean hasReacted;

    public Post(int id, String userName, String userAvatar, PostType type,
                String content, String milestone, String timeAgo, int reactions, boolean hasReacted) {
        this.id = id;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.type = type;
        this.content = content;
        this.milestone = milestone;
        this.timeAgo = timeAgo;
        this.reactions = reactions;
        this.hasReacted = hasReacted;
    }

    public int getId() { return id; }
    public String getUserName() { return userName; }
    public String getUserAvatar() { return userAvatar; }
    public PostType getType() { return type; }
    public String getContent() { return content; }
    public String getMilestone() { return milestone; }
    public String getTimeAgo() { return timeAgo; }
    public int getReactions() { return reactions; }
    public boolean isHasReacted() { return hasReacted; }

    public void toggleReaction() {
        hasReacted = !hasReacted;
        reactions += hasReacted ? 1 : -1;
    }
}
