package dao;

import java.util.List;
import model.Service;

public interface ServiceDAOIF {

    Service findServiceByID(int serviceID);

    List<Service> findAllServices();
}
