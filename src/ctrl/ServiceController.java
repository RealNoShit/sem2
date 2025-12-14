package controller;
import java.sql.SQLException;
import java.util.List;
import dao.ServiceDAOIF;
import model.Service;

public class ServiceController {

	private final ServiceDAOIF serviceDAO;
	
	public ServiceController(ServiceDAOIF serviceDAO) {
		this.serviceDAO = serviceDAO;
	}
	public Service findServiceByID(int id) throws SQLException {
		return serviceDAO.findServiceByID(id);
	}
	public List<Service> findAllServices() throws SQLException {
		return serviceDAO.findAllServices();
	}
}
