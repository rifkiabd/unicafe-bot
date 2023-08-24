package my.uum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SystemAdminManager {
    private Connection connection;

    public SystemAdminManager(Connection connection) {
        this.connection = connection;
    }

    public SystemAdmin login(String email, String password) {
        String query = "SELECT * FROM system_admins WHERE email = ? AND password = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                String adminId = resultSet.getString("adminId");
                String retrievedEmail = resultSet.getString("email");
                String retrievedPassword = resultSet.getString("password");
                
                SystemAdmin systemAdmin = new SystemAdmin(adminId, retrievedEmail, retrievedPassword);
                
                return systemAdmin;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return null;
    }
    
    
}

