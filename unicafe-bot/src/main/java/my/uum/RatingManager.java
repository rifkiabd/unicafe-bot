package my.uum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingManager {
    private Connection connection;

    public RatingManager(Connection connection) {
        this.connection = connection;
    }

    public void insertRating(String foodCode, float rating) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO ratings (foodCode, rating) VALUES (?, ?)")) {
            statement.setString(1, foodCode);
            statement.setFloat(2, rating);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public float getAverageRating(String foodCode) {
        float averageRating = 0;
        int count = 0;
    
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT rating FROM ratings WHERE foodCode = ?")) {
            statement.setString(1, foodCode);
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                float rating = resultSet.getFloat("rating");
                averageRating += rating;
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        if (count > 0) {
            averageRating /= count;
        }
    
        return averageRating;
    }
        
}
