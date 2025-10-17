package classes;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
	 * This method is responsible for creating the entry ParkingFee object for each
	 * car, but there are several validations that need to be done first. If any
	 * validation fails, an exception is thrown and not caught. That is because any
	 * validation failure means a car is not allowed to enter the lot. While most of
	 * these errors would simply return a more graceful error message, the last
	 * checked exception could represent something more serious. If this exception
	 * were to be thrown, it is possible a separate process would alert the Parking
	 * Office to potential malfeasance.
	 */
	public void entry(ParkingLot lot, Car car) {
		try {
			if (car.getPermit() == null) {
				throw new RuntimeException("Permit required to enter parking lot.");
			}
			if (car.getPermitExpiration().isBefore(LocalDateTime.now().toLocalDate())) {
				throw new RuntimeException("Permit expired. Please contact Parking Office.");
			}
			if (lot.getParkedCars().size() >= lot.getCapacity()) {
				System.err.println("Parking Lot Full.");
				throw new RuntimeException("Parking Lot Full.");
			}

			
			if (lot.getParkedCars().contains(car)) {
				System.err.println("Car already parked in the lot.");
				throw new RuntimeException("Car already parked in the lot.");
			}
			this.createParkingCharge(lot, car);
			lot.getParkedCars().add(car);
		} catch (Exception e) {
			System.err.println("Failed to validate entry: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	/*
	 * Calculating the hourly rate here allows us to use the same updateParkingFees
	 * method with only slight variation to account for the daily vs hourly rate.
	 * Once the hourly rate is calculated, the entry time is nulled out. This allows
	 * the user to enter and exit the lot multiple times and only be charged for the
	 * time spent in the lot. Finally, the car is removed from the set of parked
	 * cars so a user can re-enter the lot if desired and so new cars can enter the
	 * lot if it was previously at capacity.
	 */
	public void exit(Car car) {
		try {

			if (this.chargeOnExit) {
				ParkingFee fee = car.getParkingFees().get(this.lotId);
				LocalDateTime entryTime = fee.getEntryTime();
				LocalDateTime exitTime = LocalDateTime.now();

				Integer hoursBetween = (int) ChronoUnit.HOURS.between(entryTime, exitTime);
				fee.setRate(fee.getRate() + hoursBetween);
				fee.setEntryTime(null);
				car.updateParkingFees(fee, Boolean.FALSE);
			}
			this.parkedCars.remove(car);
		} catch (Exception e) {
			throw new RuntimeException("Failed to process exit: " + e.getMessage());
		}
	}

	/*
	 * Using a nightly batch process, this method would be called at midnight for
	 * every parking lot using the daily rate in the university system. Once called,
	 * the parking lot will update the parking fees for each car currently parked in
	 * the lot. The updateDailyFees method does not check for chargeOnExit because
	 * that will be the responsibility of the nightly process.
	 */
	public void updateDailyFees() {
		for (ParkingLot lot : lots.stream().filter(lot -> !lot.getChargeOnExit()).toList()) {
				for (Car car : lot.getParkedCars()) {
					ParkingCharge charge = new ParkingCharge();
					charge.setLotId(lot.getLotId());
					charge.setPermitId(car.getOwner());
					charge.setIncurred(Instant.now());
					charge.setAmount(lot.getLotFee());
					charges.add(charge);
				}
		}
	}
	
	/*
	 * Parking charges are created to track amount owed for each car.
	 */
	public void createParkingCharge(ParkingLot lot, Car car) {
			ParkingCharge charge = new ParkingCharge();
			charge.setLotId(lot.getLotId());
			charge.setPermitId(car.getOwner());
			charge.setIncurred(Instant.now());
			charge.setAmount(lot.getLotFee());
		
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
	public Double calculatePermitBill(Car car) {
		Double total = 0.0;
		List<ParkingCharge> charges = findParkingChargesByOwnerId(car.getOwner());
		for (ParkingCharge charge : charges) {
			total += charge.getAmount().getDollars();
		}

		if (car.getType() == CarType.COMPACT) {
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
	public String calculateCustomerMonthlyBill(Customer customer) {
		Double total = 0.0;
		for (Car car : this.findCarsByCustomerId(customer.getCustomerId())) {
			total += this.calculatePermitBill(car);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("Customer Monthly Bill for: ").append(customer.getName()).append("\n");
		sb.append("Bill amount: $").append(total).append("\n");
		sb.append("Sent to: $").append(customer.getAddress());

		return sb.toString();
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
	public List<ParkingCharge> findParkingChargesByOwnerId(UUID ownerId) {
		return charges.stream().filter(charge -> charge.getPermitId().equals(ownerId)).toList();
	}
	public List<Car> findCarsByCustomerId(UUID customerId) {
		return cars.stream().filter(car -> car.getOwner().equals(customerId)).toList();
	}
	public List<ParkingCharge> getCharges() {
		return charges;
	}
	public void setCharges(List<ParkingCharge> charges) {
		this.charges = charges;
	}

}
