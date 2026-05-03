package com.oop.gymquest.data;

import com.oop.gymquest.model.Post;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts ORDER BY postid DESC";

        try (Connection c = MySQLConnection.getConnection();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                posts.add(new Post(
                        rs.getInt("postid"),
                        rs.getString("username"),
                        "👤", // Default avatar emoji
                        Post.PostType.valueOf(rs.getString("post_type")),
                        rs.getString("content"),
                        rs.getString("milestone"),
                        "Just now",
                        rs.getInt("reactions"),
                        false
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public void savePost(Post post) {
        String sql = "INSERT INTO posts (username, post_type, content, milestone) VALUES (?, ?, ?, ?)";
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement pstmt = c.prepareStatement(sql)) {

            pstmt.setString(1, post.getUserName());
            pstmt.setString(2, post.getType().name());
            pstmt.setString(3, post.getContent());
            pstmt.setString(4, post.getMilestone());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}