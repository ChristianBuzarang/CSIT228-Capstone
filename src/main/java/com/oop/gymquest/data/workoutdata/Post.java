package com.oop.gymquest.data.workoutdata;

public class Post {
    public enum PostType { STREAK, WORKOUT, BADGE, GOAL }

    private int id;
    private String userName;
    private String content;
    private String milestone;
    private PostType type;
    private String timeAgo;
    private int reactions;
    private boolean hasReacted;

    public Post(int id, String userName, String content, String milestone, PostType type, String timeAgo, int reactions, boolean hasReacted) {
        this.id = id;
        this.userName = userName;
        this.content = content;
        this.milestone = milestone;
        this.type = type;
        this.timeAgo = timeAgo;
        this.reactions = reactions;
        this.hasReacted = hasReacted;
    }

    public int getId() { return id; }
    public String getUserName() { return userName; }
    public String getContent() { return content; }
    public String getMilestone() { return milestone; }
    public PostType getType() { return type; }
    public String getTimeAgo() { return timeAgo; }
    public int getReactions() { return reactions; }
    public boolean isHasReacted() { return hasReacted; }
}
