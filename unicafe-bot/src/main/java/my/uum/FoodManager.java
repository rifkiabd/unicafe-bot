package my.uum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FoodManager {
    private Connection connection;

    public FoodManager(Connection connection) {
        this.connection = connection;
    }

    public List<Food> getFoodItems(String cafeCode) {
        List<Food> foodItems = new ArrayList<>();
    
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM foods WHERE cafeCoded = ?")) {
    
            statement.setString(1, cafeCode);
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                String foodCode = resultSet.getString("foodCode");
                String foodType = resultSet.getString("food_type");
                String foodName = resultSet.getString("name");
                double foodPrice = resultSet.getDouble("price");
                byte[] foodImage = resultSet.getBytes("image");
                String status = resultSet.getString("status");
    
                Food food = new Food(foodCode, cafeCode, foodType, foodName, foodPrice, foodImage, status);
                foodItems.add(food);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return foodItems;
    }
    
    public List<Food> getFoodItemsAdmin(String emailAdmin) {
        List<Food> foodItems = new ArrayList<>();
    
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM foods WHERE emailAdmin = ?")) {
    
            statement.setString(1, emailAdmin);
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                String foodCode = resultSet.getString("foodCode");
                String cafeCode = resultSet.getString("cafeCode");
                String foodType = resultSet.getString("food_type");
                String foodName = resultSet.getString("name");
                double foodPrice = resultSet.getDouble("price");
                byte[] foodImage = resultSet.getBytes("image");
                String status = resultSet.getString("status");
    
                Food food = new Food(foodCode, cafeCode, foodType, foodName, foodPrice, foodImage, status);
                foodItems.add(food);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return foodItems;
    }

    
    public boolean insertFood(Food food) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO foods (foodCode, cafeCoded, food_type, name, price, image, status) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
    
            statement.setString(1, food.getFoodCode());
            statement.setString(2, food.getCafeCoded());
            statement.setString(3, food.getFoodType());
            statement.setString(4, food.getFoodName());
            statement.setDouble(5, food.getFoodPrice());
            statement.setBytes(6, food.getFoodImage());
            statement.setString(7, food.getStatus());
    
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateFoodImage(Food food) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE foods SET image = ? WHERE foodCode = ? AND cafeCoded = ?")) {

            statement.setBytes(1, food.getFoodImage());
            statement.setString(2, food.getFoodCode());
            statement.setString(3, food.getCafeCoded());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public void updateFood(Food food) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE foods SET food_type = ?, name = ?, price = ?, image = ?, status = ? WHERE foodCode = ? AND cafeCoded = ?")) {
    
            statement.setString(1, food.getFoodType());
            statement.setString(2, food.getFoodName());
            statement.setDouble(3, food.getFoodPrice());
            statement.setBytes(4, food.getFoodImage());
            statement.setString(5, food.getStatus());
            statement.setString(6, food.getFoodCode());
            statement.setString(7, food.getCafeCoded());
    
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean deleteFood(String foodCode, String emailAdmin) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM foods WHERE foodCode = ? AND emailAdmin = ?")) {
    
            statement.setString(1, foodCode);
            statement.setString(2, emailAdmin);
    
            int rowsAffected = statement.executeUpdate();
    
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
    }

    public List<Food> searchFoodByName(String keyword) {
        List<Food> matchingFoods = new ArrayList<>();
    
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM foods WHERE name LIKE ?")) {
    
            statement.setString(1, "%" + keyword + "%");
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                String foodCode = resultSet.getString("foodCode");
                String cafeCode = resultSet.getString("cafeCoded");
                String foodType = resultSet.getString("food_type");
                String foodName = resultSet.getString("name");
                double foodPrice = resultSet.getDouble("price");
                byte[] foodImage = resultSet.getBytes("image");
                String status = resultSet.getString("status");
    
                Food food = new Food(foodCode, cafeCode, foodType, foodName, foodPrice, foodImage, status);
                matchingFoods.add(food);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return matchingFoods;
    }
    

    public boolean isValidFoodCode(String foodCode) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT foodCode FROM foods WHERE foodCode = ?")) {
            statement.setString(1, foodCode);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }   
    
}
