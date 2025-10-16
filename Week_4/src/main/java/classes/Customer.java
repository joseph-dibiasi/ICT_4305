package classes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.UUID;

public class Customer {

	// Campus ID. Unique.
	private UUID customerId;

	private String name;

	private Address address;

	private String phoneNumber;

	private HashMap<String, Car> cars;

	/*
	 * Every car registered to a customer is valid for one year from the date of
	 * registration. A customer can register multiple cars.
	 */
	public Car register(String license, CarType carType) {
		Car registeredCar = new Car(getName(), LocalDate.now().plusYears(1), license, carType, getCustomerId());

		this.getCars().put(registeredCar.getLicense(), registeredCar);
		return registeredCar;
	}

	public UUID getCustomerId() {
		return customerId;
	}

	public void setCustomerId(UUID customerId) {
		this.customerId = customerId;
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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public HashMap<String, Car> getCars() {
		if (cars == null) {
			setCars(new HashMap<String, Car>());
		}
		return cars;
	}

	public void setCars(HashMap<String, Car> cars) {
		this.cars = cars;
	}

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", name=" + name + ", address=" + address + ", phoneNumber="
				+ phoneNumber + "]";
	}

}
