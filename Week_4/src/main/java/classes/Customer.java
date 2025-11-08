package classes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Objects;
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
		return "Customer [customerId=" + customerId + ", name=" + name + ", address=" + address.getAddressInfo() + ", phoneNumber="
				+ phoneNumber + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, cars, customerId, name, phoneNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		return Objects.equals(address, other.address) && Objects.equals(cars, other.cars)
				&& Objects.equals(customerId, other.customerId) && Objects.equals(name, other.name)
				&& Objects.equals(phoneNumber, other.phoneNumber);
	}

}
