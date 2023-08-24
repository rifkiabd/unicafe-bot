package my.uum;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CafeAdminManager {
    private Connection connection;

    public CafeAdminManager(Connection connection) {
        this.connection = connection;
    }

    public boolean registerCafeAdmin(String emailAddress, String name, String password) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO cafe_admins (email_address, name, password_salt, password_hash) VALUES (?, ?, ?, ?)")) {
            byte[] salt = generateSalt();

            String hashedPassword = hashPassword(password, salt);

            statement.setString(1, emailAddress);
            statement.setString(2, name);
            statement.setBytes(3, salt);
            statement.setString(4, hashedPassword);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    public CafeAdmin loginCafeAdmin(String emailAddress, String password) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM cafe_admins WHERE email_address = ?")) {
            statement.setString(1, emailAddress);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    byte[] salt = resultSet.getBytes("password_salt");
                    String hashedPassword = resultSet.getString("password_hash");
                    if (verifyPassword(password, hashedPassword, salt)) {
                        return new CafeAdmin(emailAddress, name, salt, hashedPassword);
                    }
                }
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }

    public String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        String saltedPassword = password + bytesToHex(salt);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = messageDigest.digest(saltedPassword.getBytes());
        return bytesToHex(hashedBytes);
    }

    public boolean verifyPassword(String password, String hashedPassword, byte[] salt) throws NoSuchAlgorithmException {
        String expectedHashedPassword = hashPassword(password, salt);
        return expectedHashedPassword.equals(hashedPassword);
    }

    public String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
