package my.uum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CafeManager {
    private Connection connection;

    public CafeManager(Connection connection) {
        this.connection = connection;
    }

    public boolean insertCafe(Cafe cafe) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO cafes (cafeCode, name, inasis_name, office_telNo, mobile_telNo, location, location_link, open_time, close_time, holiday_status, description, emailAdmin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, cafe.getCafeCode());
            statement.setString(2, cafe.getName());
            statement.setString(3, cafe.getInasisName());
            statement.setString(4, cafe.getOfficeTelNo());
            statement.setString(5, cafe.getMobileTelNo());
            statement.setString(6, cafe.getLocation());
            statement.setString(7, cafe.getLocationLink());
            statement.setString(8, cafe.getOpenTime());
            statement.setString(9, cafe.getCloseTime());
            statement.setString(10, cafe.getHolidayStatus());
            statement.setString(11, cafe.getDescription());
            statement.setString(12, cafe.getEmailAdmin());

            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Cafe> getViewCafes() {
        List<Cafe> cafes = new ArrayList<>();
    
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM view_cafes")) {
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                String cafeCode = resultSet.getString("cafeCode");
                String name = resultSet.getString("name");
                String inasisName = resultSet.getString("inasis_name");
                String officeTelNo = resultSet.getString("office_telNo");
                String mobileTelNo = resultSet.getString("mobile_telNo");
                String location = resultSet.getString("location");
                String locationLink = resultSet.getString("location_link");
                String openTime = resultSet.getString("open_time");
                String closeTime = resultSet.getString("close_time");
                String holidayStatus = resultSet.getString("holiday_status");
                String description = resultSet.getString("description");
                String emailAdmin = resultSet.getString("emailAdmin");
    
                Cafe cafe = new Cafe(cafeCode, name, inasisName, officeTelNo, mobileTelNo, location, locationLink,
                        openTime, closeTime, holidayStatus, description, emailAdmin);
                cafes.add(cafe);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return cafes;
    }    

    public boolean insertCafeApproval(String cafeCode) {
        try (PreparedStatement selectStatement = connection.prepareStatement(
                "SELECT * FROM cafes WHERE cafeCode = ?")) {
            selectStatement.setString(1, cafeCode);
    
            ResultSet resultSet = selectStatement.executeQuery();
    
            if (resultSet.next()) {
                Cafe cafe = new Cafe(resultSet.getString("cafeCode"), resultSet.getString("name"), resultSet.getString("inasis_name"),
                        resultSet.getString("office_telNo"), resultSet.getString("mobile_telNo"),
                        resultSet.getString("location"), resultSet.getString("location_link"),
                        resultSet.getString("open_time"), resultSet.getString("close_time"),
                        resultSet.getString("holiday_status"), resultSet.getString("description"),
                        resultSet.getString("emailAdmin"));
    
                try (PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO cafe_approval (cafeCode, name, inasis_name, office_telNo, mobile_telNo, location, location_link, open_time, close_time, holiday_status, description, emailAdmin) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    insertStatement.setString(1, cafe.getCafeCode());
                    insertStatement.setString(2, cafe.getName());
                    insertStatement.setString(3, cafe.getInasisName());
                    insertStatement.setString(4, cafe.getOfficeTelNo());
                    insertStatement.setString(5, cafe.getMobileTelNo());
                    insertStatement.setString(6, cafe.getLocation());
                    insertStatement.setString(7, cafe.getLocationLink());
                    insertStatement.setString(8, cafe.getOpenTime());
                    insertStatement.setString(9, cafe.getCloseTime());
                    insertStatement.setString(10, cafe.getHolidayStatus());
                    insertStatement.setString(11, cafe.getDescription());
                    insertStatement.setString(12, cafe.getEmailAdmin());
    
                    int rowsAffected = insertStatement.executeUpdate();
    
                    if (rowsAffected > 0) {
                        try (PreparedStatement deleteStatement = connection.prepareStatement(
                                "DELETE FROM cafes WHERE cafeCode = ?")) {
                            deleteStatement.setString(1, cafeCode);
                            deleteStatement.executeUpdate();
                        }
                    }
    
                    return rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
    }    

    public List<Cafe> getViewApproval() {
        List<Cafe> approvedCafes = new ArrayList<>();
    
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM cafe_approval")) {
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                String cafeCode = resultSet.getString("cafeCode");
                String name = resultSet.getString("name");
                String inasisName = resultSet.getString("inasis_name");
                String officeTelNo = resultSet.getString("office_telNo");
                String mobileTelNo = resultSet.getString("mobile_telNo");
                String location = resultSet.getString("location");
                String locationLink = resultSet.getString("location_link");
                String openTime = resultSet.getString("open_time");
                String closeTime = resultSet.getString("close_time");
                String holidayStatus = resultSet.getString("holiday_status");
                String description = resultSet.getString("description");
                String emailAdmin = resultSet.getString("emailAdmin");
    
                Cafe cafe = new Cafe(cafeCode, name, inasisName, officeTelNo, mobileTelNo, location,
                        locationLink, openTime, closeTime, holidayStatus, description, emailAdmin);
                approvedCafes.add(cafe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return approvedCafes;
    }    
    
    public boolean updateCafe(Cafe cafe) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE cafe_approval SET name = ?, inasis_name = ?, office_telNo = ?, mobile_telNo = ?, location = ?, location_link = ?, open_time = ?, close_time = ?, holiday_status = ?, description = ?, emailAdmin = ? " +
                "WHERE cafeCode = ?")) {
            statement.setString(1, cafe.getName());
            statement.setString(2, cafe.getInasisName());
            statement.setString(3, cafe.getOfficeTelNo());
            statement.setString(4, cafe.getMobileTelNo());
            statement.setString(5, cafe.getLocation());
            statement.setString(6, cafe.getLocationLink());
            statement.setString(7, cafe.getOpenTime());
            statement.setString(8, cafe.getCloseTime());
            statement.setString(9, cafe.getHolidayStatus());
            statement.setString(10, cafe.getDescription());
            statement.setString(11, cafe.getEmailAdmin());
            statement.setString(12, cafe.getCafeCode());
    
            int rowsAffected = statement.executeUpdate();
    
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
    }
    
    public boolean deleteCafe(String cafeCode) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM cafe_approval WHERE cafeCode = ?")) {
            statement.setString(1, cafeCode);
    
            int rowsAffected = statement.executeUpdate();
    
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
    }
    

    public List<String> getCafeNames() {
        List<String> cafeNames = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT name FROM cafe_approval")) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String cafeName = resultSet.getString("name");
                cafeNames.add(cafeName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cafeNames;
    }

    public List<String> getCafeCodes() {
        List<String> cafeCodes = new ArrayList<>();
    
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT cafeCode FROM cafe_approval")) {
    
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                String cafeCode = resultSet.getString("cafeCode");
                cafeCodes.add(cafeCode);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return cafeCodes;
    }
    
    public boolean updateHolidayStatus(String emailAddress, String updateStatus) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE cafe_approval SET holiday_status = ? WHERE emailAdmin = ?")) {
            statement.setString(1, updateStatus);
            statement.setString(2, emailAddress);

            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public void deleteFood(String foodCode, String emailAdmin) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM foods WHERE foodCode = ? AND emailAdmin = ?")) {
    
            statement.setString(1, foodCode);
            statement.setString(2, emailAdmin);
    
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

}
