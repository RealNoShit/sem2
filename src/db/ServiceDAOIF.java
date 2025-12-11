package dao;
import java.sql.SQLException;
import java.util.List;
import model.Service;

public interface ServiceDAOIF {
	
	Service findServiceByID(int id) throws SQLException;
	
	List<Service> findAllServices() throws SQLException;

}
