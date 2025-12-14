package controller;
import java.util.List;
import dao.ServiceDAOIF;
import model.Service;

public class ServiceController {

    private ServiceDAOIF serviceDAO;

    public ServiceController(ServiceDAOIF serviceDAO) {
        this.serviceDAO = serviceDAO;
    }

    public Service findServiceByID(int id) {
        return serviceDAO.findServiceByID(id);
    }

    public List<Service> findAllServices() {
        return serviceDAO.findAllServices();
    }
}