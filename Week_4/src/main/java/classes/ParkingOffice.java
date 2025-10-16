package classes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ParkingOffice {
	
	private String name;
	private Address address;
	private List<Customer> customers;
	private List<Car> cars;
	private List<ParkingLot> lots;
	private List<ParkingCharge> charges;
	
	public Customer register(String name, Address address, String phone) {
		Customer customer = new Customer();
		customer.setCustomerId(UUID.randomUUID());
		customer.setName(name);
		customer.setAddress(address);
		customer.setPhoneNumber(phone);
		return customer;

	}
	
	
	public Car register(Customer customer, String license, CarType type) {
		Car car = new Car(customer.getName(), LocalDate.now().plusYears(1), license, type, customer.getCustomerId());
		return car;

	}
	
	/*
	 * For a method to return only a single customer, name is not reliable.
	 * A large university could have several people with the same name;
	 * using the unique customerId is more reliable.
	 */
	public Customer getCustomer(UUID customerId) {
		return this.getCustomers().stream().filter(c -> c.getCustomerId().equals(customerId)).findFirst().orElse(null);

	}
	
	public Money addCharge(ParkingCharge parkingCharge) {
		return null;
	}
	  

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public List<Customer> getCustomers() {
		return customers;
	}
	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}
	public List<Car> getCars() {
		return cars;
	}
	public void setCars(List<Car> cars) {
		this.cars = cars;
	}
	public List<ParkingLot> getLots() {
		return lots;
	}
	public void setLots(List<ParkingLot> lots) {
		this.lots = lots;
	}
	public List<ParkingCharge> getCharges() {
		return charges;
	}
	public void setCharges(List<ParkingCharge> charges) {
		this.charges = charges;
	}

}
