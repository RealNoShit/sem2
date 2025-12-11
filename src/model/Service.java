package model;

public class Service {
	private int serviceID;
	private String name;
	private double price;
	
	public Service(int serviceID, String name, double price ) {
		this.serviceID = serviceID;
		this.name = name;
		this.price = price;
	}
	
	public void setServiceID(int serviceID) {
		this.serviceID = serviceID;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public int getServiceID() {
		return serviceID;
	}
	
	public String getName() {
		return name;
	}
	
	public double price() {
		return price;
	}

}
