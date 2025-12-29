package dao;

import model.Appointment;
import util.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

     
    public boolean isSlotOccupied(LocalDate date, LocalTime startTime, int durationMinutes) {
        LocalTime endTime = startTime.plusMinutes(durationMinutes);

       
        String sql = "SELECT COUNT(*) FROM appointment a " +
                     "JOIN service s ON a.service_id = s.service_id " +
                     "WHERE a.appointment_date = ? AND a.status <> 'CANCELLED' AND (" +
                     "(? >= a.appointment_time AND ? < ADDTIME(a.appointment_time, SEC_TO_TIME(s.duration * 60))) OR " +
                     "(? < ADDTIME(a.appointment_time, SEC_TO_TIME(s.duration * 60)) AND ? >= a.appointment_time))";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            ps.setTime(2, Time.valueOf(startTime));
            ps.setTime(3, Time.valueOf(startTime));
            ps.setTime(4, Time.valueOf(endTime));
            ps.setTime(5, Time.valueOf(endTime));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int totalActiveAppointments = rs.getInt(1);
                    System.out.println("Kapasite Kontrol: " + date + " " + startTime + " aralığında " + totalActiveAppointments + " aktif randevu var.");
                    return totalActiveAppointments >= 3; 
                }
            }
        } catch (SQLException e) {
            System.err.println("isSlotOccupied Hatası: " + e.getMessage());
        }
        return false;
    }

    public boolean createAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointment (user_id, car_id, service_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointment.getUserId());
            ps.setInt(2, appointment.getCarId());
            ps.setInt(3, appointment.getServiceId());
            ps.setDate(4, Date.valueOf(appointment.getDate()));
            ps.setTime(5, Time.valueOf(appointment.getTime()));
            ps.setString(6, appointment.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("createAppointment Hatası: " + e.getMessage());
            return false;
        }
    }

    public List<Appointment> getAppointmentsByUser(int userId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointment WHERE user_id = ? ORDER BY appointment_date, appointment_time";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { list.add(mapRow(rs)); }
            }
        } catch (SQLException e) { }
        return list;
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointment ORDER BY appointment_date, appointment_time";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { list.add(mapRow(rs)); }
        } catch (SQLException e) { }
        return list;
    }

    public boolean updateStatus(int appointmentId, String status) {
        String sql = "UPDATE appointment SET status = ? WHERE appointment_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        return new Appointment(
                rs.getInt("appointment_id"),
                rs.getInt("user_id"),
                rs.getInt("car_id"),
                rs.getInt("service_id"),
                rs.getDate("appointment_date").toLocalDate(),
                rs.getTime("appointment_time").toLocalTime(),
                rs.getString("status")
        );
    }
}