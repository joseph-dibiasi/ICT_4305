package classes;

import java.time.LocalDate;
import java.util.HashMap;
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
	 * This method is called each time a car enters a daily lot and each time it
	 * enters and exits an hourly lot. In a daily lot, the rate is incremented by
	 * one whenever the car is scanned for entering the lot and again if it remains
	 * there till midnight. An hourly lot the rate is determined by the time spent
	 * in the lot. If a customer enters the lot or returns to the lot after exiting,
	 * the total parking fee will not change. Once the customer exits the lot, the
	 * hours spent in the lot is calculated in the ParkingLot class so only the
	 * total fee needs to be updated here.
	 */
	public void updateParkingFees(ParkingCharge fee, Boolean dailyRate) {
		if (this.findParkingChargeByLotId(fee.getLotId()) != null) {
			ParkingCharge existingFee = this.findParkingChargeByLotId(fee.getLotId());
			if (dailyRate) {
				existingFee.setRate(existingFee.getRate() + fee.getRate());
				existingFee.setTotalFee(existingFee.getRate() * existingFee.getLotFees());
			} else if (existingFee.getEntryTime() == null) {
				existingFee.setTotalFee(existingFee.getRate() * existingFee.getLotFees());
			}
			this.charges.add(existingFee);
		} else {
			this.charges.add(fee);
		}

	}

	/*
	 * This method would be called for each car when the University Parking
	 * Office calculates the monthly bill for customers. Since customers can
	 * register multiple cars, the total bill for each car is calculated separately.
	 * This allows the 20% compact car discount to apply on a car by car basis.
	 */
	public Double calculatePermitBill() {
		Double total = 0.0;
		for (ParkingCharge charge : charges) {
			total += charge.getAmount().getDollars();
		}

		if (this.getType() == CarType.COMPACT) {
			total = total * 0.8;
		}

		return total;
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
	public ParkingCharge findParkingChargeByLotId(UUID lotId) {
		return charges.stream().filter(charge -> charge.getLotId().equals(lotId)).findFirst().orElse(null);
	}
	public List<ParkingCharge> getCharges() {
		return charges;
	}
	public void setCharges(List<ParkingCharge> charges) {
		this.charges = charges;
	}

}
