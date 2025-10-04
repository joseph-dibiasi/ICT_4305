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

	/*
	 * Since customers can have multiple cars, the customer bill needs to include
	 * all permits for that customer. The University Parking Office would call this
	 * method to calculate the total monthly bill using the permit bill for each car
	 * registered to the customer. Address is not included, presumably an automated
	 * service would be responsible for retrieving all customers and calculating
	 * their bills. Since this service would already have the customer object, it
	 * could then call the getAddress method to know where to send the bill. Another
	 * aspect not included is the car parking fees being wiped after each month
	 * (otherwise your bill would rise indefinitely!). It was not included because
	 * this seems like it would be the responsibility of the service calling this
	 * method once the bill had been successfully calculated and sent to the proper
	 * address; something that seemed out of scope of the customer class. If the
	 * process failed at any stage, we would still want to know what they should
	 * owe!
	 */
	public Double calculateCustomerMonthlyBill() {
		Double total = 0.0;
		for (Car car : getCars().values()) {
			total += car.calculatePermitBill();
		}
		return total;
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
