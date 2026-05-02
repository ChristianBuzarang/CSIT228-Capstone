package com.oop.gymquest.data;
import com.oop.gymquest.data.workoutdata.Exercise;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExerciseDAO {
    public static List<Exercise> getAll() {
        List<Exercise> list = new ArrayList<>();
        String sql = "SELECT * FROM exercises";
        try (Connection c = MySQLConnection.getConnection();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Exercise(rs.getInt("id"), rs.getString("name"),
                        rs.getInt("sets"), rs.getString("reps"),
                        rs.getString("emoji"), rs.getString("category")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}