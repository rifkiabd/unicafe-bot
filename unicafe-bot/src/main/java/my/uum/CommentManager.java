package my.uum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentManager {
    private Connection connection;

    public CommentManager(Connection connection) {
        this.connection = connection;
    }

    public boolean insertComment(Comment comment) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO comments (cafeCode, comment) VALUES (?, ?)")) {
                statement.setString(1, comment.getCafeCode());
                statement.setString(1, comment.getComment());
            
            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Comment> getComments(String cafeCode) {
        List<Comment> comments = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM comments WHERE cafeCode = ?")) {
            statement.setString(1, cafeCode);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String commentText = resultSet.getString("comment");

                Comment comment = new Comment(cafeCode, commentText);
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comments;
    }
}
