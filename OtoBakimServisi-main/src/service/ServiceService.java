package service;

import dao.ServiceDAO;
import model.Service;
import java.util.List;

public class ServiceService {

    private ServiceDAO serviceDAO;

    public ServiceService() {
        serviceDAO = new ServiceDAO();
    }

    
    public List<Service> getAllServices() {
        return serviceDAO.getAllServices();
    }

    public Service getServiceById(int serviceId) {
        return serviceDAO.getServiceById(serviceId);
    }

   

    
    public boolean addService(String name, int duration, double price) {
        // İş mantığı kontrolü 
        if (name == null || name.isEmpty() || price < 0 || duration <= 0) {
            return false;
        }

       
        // ID otomatik artacağı için 0 veriyoruz
        Service service = new Service(0, name, duration, price);

        // Oluşturulan nesneyi DAO'ya gönderiyoruz
        return serviceDAO.addService(service);
    }

    public boolean deleteService(int serviceId) {
        return serviceDAO.deleteService(serviceId);
    }
}