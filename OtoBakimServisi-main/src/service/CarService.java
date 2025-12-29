package service;

import dao.CarDAO;
import model.Car;
import java.util.List;

public class CarService {

    private CarDAO carDAO;

    public CarService() {
        carDAO = new CarDAO();
    }

    
    public boolean addCar(int userId, String plate, String brand, String model, int year) {
        Car car = new Car(
                0, 
                userId,
                plate,
                brand,
                model,
                year
        );

        
        return carDAO.addCar(car);
    }
    
    public Car getCarById(int carId) {
        return carDAO.getCarById(carId);
    }

    public List<Car> getCarsByUser(int userId) {
        return carDAO.getCarsByUser(userId);
    }
}