package dao;

import model.Car;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.DatabaseManager;

public class CarDAO {

    // Kullanıcıya ait araçları getir
    public List<Car> getCarsByUser(int userId) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM car WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Car car = new Car(
                        rs.getInt("car_id"),
                        rs.getInt("user_id"),
                        rs.getString("plate"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getInt("year")
                );
                cars.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

   
    public boolean addCar(Car car) {
        String sql = "INSERT INTO car (user_id, plate, brand, model, year) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, car.getUserId());
            ps.setString(2, car.getPlate());
            ps.setString(3, car.getBrand());
            ps.setString(4, car.getModel());
            ps.setInt(5, car.getYear());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Araç ekleme hatası: " + e.getMessage());
            return false;
        }
    }

    public Car getCarById(int carId) {
        String sql = "SELECT * FROM car WHERE car_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, carId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Car(
                        rs.getInt("car_id"),
                        rs.getInt("user_id"),
                        rs.getString("plate"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getInt("year")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}