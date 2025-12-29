package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {

    private int appointmentId;
    private int userId;
    private int carId;
    private int serviceId;
    private LocalDate date;
    private LocalTime time;
    private String status; // PENDING, APPROVED, CANCELLED

   
    public Appointment(int appointmentId,
                       int userId,
                       int carId,
                       int serviceId,
                       LocalDate date,
                       LocalTime time,
                       String status) {

        this.appointmentId = appointmentId;
        this.userId = userId;
        this.carId = carId;
        this.serviceId = serviceId;
        this.date = date;
        this.time = time;
        this.status = status;
    }

   
    public Appointment(int userId,
                       int carId,
                       int serviceId,
                       LocalDate date,
                       LocalTime time,
                       String status) {

        this.userId = userId;
        this.carId = carId;
        this.serviceId = serviceId;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getUserId() {
        return userId;
    }

    public int getCarId() {
        return carId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
