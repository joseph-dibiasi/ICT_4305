package classes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ParkingOffice {

	private String name;
	private Address address;
	private List<Customer> customers;
	private List<Car> cars;
	private List<ParkingLot> lots;
	private List<ParkingCharge> charges;

	public ParkingOffice() {
	    this.customers = new ArrayList<>();
	    this.cars = new ArrayList<>();
	    this.lots = new ArrayList<>();
	    this.charges = new ArrayList<>();
	}

	public ParkingOffice(String name, Address address) {
		this.name = name;
		this.address = address;
	    this.customers = new ArrayList<>();
	    this.cars = new ArrayList<>();
	    this.lots = new ArrayList<>();
	    this.charges = new ArrayList<>();	}

	public Customer register(String name, Address address, String phone) {
		Customer customer = new Customer();
		customer.setCustomerId(UUID.randomUUID());
		customer.setName(name);
		customer.setAddress(address);
		customer.setPhoneNumber(phone);
		this.getCustomers().add(customer);
		return customer;

	}

	public Car register(Customer customer, String license, CarType type) {
		Car car = customer.register(license, type);
		this.getCars().add(car);
		return car;

	}
	
	/**
	 * Return collection of all customer ids.
	 */
	public List<UUID> getCustomerIds() {
	    return this.customers.stream()
	            .map(Customer::getCustomerId)
	            .toList();
	}

	/**
	 * Return distinct collection of all permit ids (derived from cars' owner ids).
	 */
	public List<UUID> getPermitIds() {
	    return this.cars.stream()
	            .map(Car::getOwner)
	            .distinct()
	            .toList();
	}

	/**
	 * Return distinct collection of permit ids for the specified customer.
	 */
	public List<UUID> getPermitIds(Customer customer) {
	    if (customer == null) {
	        return List.of();
	    }
	    UUID customerId = customer.getCustomerId();
	    return this.cars.stream()
	            .filter(car -> car.getOwner().equals(customerId))
	            .map(Car::getOwner)
	            .distinct()
	            .toList();
	}
	

	/*
	 * This method is responsible for creating the entry ParkingFee object for each
	 * car, but there are several validations that need to be done first. If any
	 * validation fails, an exception is thrown and not caught. That is because any
	 * validation failure means a car is not allowed to enter the lot. While most of
	 * these errors would simply return a more graceful error message, the last
	 * checked exception could represent something more serious. The Parking Office
	 * may store this type of error for further inquiry.
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
				throw new RuntimeException("Parking Lot Full.");
			}
			if (lot.getParkedCars().contains(car)) {
				throw new RuntimeException("Car already parked in the lot.");
			}

			this.createOrUpdateEntryParkingCharge(lot, car);
			lot.getParkedCars().add(car);
		} catch (Exception e) {
			System.err.println("Failed to validate entry: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	/*
	 * When cars exit a lot, the car is removed from the lot's ParkedCars list. This
	 * prevents customers from being charged additional daily rates on their permit
	 * and allows new cars to park in the lot. If a lot charges an hourly rate, the
	 * total charge is calculated and added to the existing parking charges for the
	 * lot. If an error is thrown during this process, the system throws an error to
	 * alert the Parking Office but the car is still removed from the ParkedCars
	 * list to allow new cars to park.
	 */
	public void exit(ParkingLot lot, Car car) {
		try {

			if (lot.getChargeOnExit()) {
				ParkingCharge charge = this.findParkingChargeByLotIdAndOwnerId(lot.getLotId(), car.getOwner());
				if (charge == null) {
					throw new RuntimeException("Parking Charge Not found! Unable to Calculate Hourly Rate.");
				}
				Instant entryTime = charge.getIncurred();
				Instant exitTime = Instant.now();

				Integer hoursBetween = (int) ChronoUnit.HOURS.between(entryTime, exitTime);
				
				if (hoursBetween < 0) {
				    throw new RuntimeException("Invalid parking time detected.");
				}
				
				Double hourlyChargeInDollars = hoursBetween * lot.getLotFee().getDollars();

				Double currentParkingLotChargesInDollars = charge.getAmount().getDollars();
				Double updatedParkingLotChargesInDollars = currentParkingLotChargesInDollars + hourlyChargeInDollars;

				Money chargeAmount = new Money(updatedParkingLotChargesInDollars);
				charge.setAmount(chargeAmount);
				charge.setIncurred(null);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to process exit: " + e.getMessage());
		} finally {
			lot.getParkedCars().remove(car);
		}
	}

	/*
	 * Using a nightly batch process, this method would be called at midnight to
	 * update the fees for any car still parked in a daily rate parking lot.
	 */
	public void updateDailyFees() {
		for (ParkingLot lot : lots.stream().filter(lot -> !lot.getChargeOnExit()).toList()) {
			for (Car car : lot.getParkedCars()) {
				ParkingCharge charge = this.findParkingChargeByLotIdAndOwnerId(lot.getLotId(), car.getOwner());
				charge.setAmount(addCharge(charge));
			}
		}
	}

	/*
	 * Upon entering a parking lot for the first time, a parking charge will be
	 * created for a car in relation to that lot. If it is an hourly lot there will
	 * be no initial fee. If a car has previously entered the lot, the existing
	 * parking charge will be found and updated. For daily lots this involves adding
	 * an additional daily charge. Customers will be charged multiple times if they
	 * leave and reenter the same daily lot within one day as these are long-term
	 * spots. If this is an hourly lot, the Instant will be captured and added to
	 * the charge to calculate the rate when the car exits the lot.
	 */
	public void createOrUpdateEntryParkingCharge(ParkingLot lot, Car car) {
		ParkingCharge charge = this.findParkingChargeByLotIdAndOwnerId(lot.getLotId(), car.getOwner());

		if (charge != null) {
			if (!lot.getChargeOnExit()) {

				charge.setAmount(addCharge(charge));
			} else {
				charge.setIncurred(Instant.now());
			}
		} else {
			charge = new ParkingCharge();
			charge.setLotId(lot.getLotId());
			charge.setPermitId(car.getOwner());
			charge.setIncurred(Instant.now());
			if (lot.getChargeOnExit()) {
				Long noInitialFee = 0L;
				charge.setAmount(new Money(noInitialFee));
			} else {
				charge.setAmount(lot.getLotFee());
			}
			this.getCharges().add(charge);
		}

	}

	/*
	 * This method would be called for each car when the University Parking Office
	 * calculates the monthly bill for customers. Since customers can register
	 * multiple cars, the total bill for each car is calculated separately. This
	 * allows the 20% compact car discount to apply on a car by car basis.
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
	 * method to calculate the total monthly bill using the permit bills for each
	 * car registered to the customer and send it to their address. If this process
	 * succeeded, all parking charges would be removed for the given customerId
	 */
	public Boolean calculateCustomerMonthlyBill(Customer customer) {
		try {
			Double total = 0.0;
			for (Car car : this.findCarsByCustomerId(customer.getCustomerId())) {
				total += this.calculatePermitBill(car);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("Customer Monthly Bill for: ").append(customer.getName()).append("\n");
			sb.append("Bill Amount: $").append(total).append("\n");
			sb.append("Successfuly Sent to: $").append(customer.getAddress().getAddressInfo());
			System.out.println(sb.toString());
			return removeParkingChargesByOwnerId(customer.getCustomerId());
		} catch (Exception e) {
			throw new RuntimeException("Failed to Process Customer Monthly Bill: " + e.getMessage());
		}
	}

	/*
	 * For a method to return only a single customer, name is not reliable. A large
	 * university could have several people with the same name; using the unique
	 * customerId is more reliable.
	 */
	public Customer getCustomer(UUID customerId) {
		return this.getCustomers().stream().filter(c -> c.getCustomerId().equals(customerId)).findFirst().orElse(null);

	}

	/*
	 * Returns updated charge. Used only for Daily Rate lots.
	 */
	public Money addCharge(ParkingCharge parkingCharge) {

		try {
			Double currentParkingLotChargesInDollars = parkingCharge.getAmount().getDollars();
			Double updatedParkingLotChargesInDollars = currentParkingLotChargesInDollars
					+ this.findLotFeeByLotId(parkingCharge.getLotId()).getDollars();

			Money chargeAmount = new Money(updatedParkingLotChargesInDollars);
			return chargeAmount;
		} catch (Exception e) {
			throw new RuntimeException("Failed to Process Parking Charge: " + e.getMessage());
		}
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

	public Money findLotFeeByLotId(UUID lotId) {
		ParkingLot lot = lots.stream().filter(charge -> charge.getLotId().equals(lotId)).findFirst().orElse(null);
		return lot.getLotFee();
	}

	/*
	 * There is only a single ParkingCharge per permit per lot. LotId and OwnerId
	 * are both unique identifiers pertaining to Parking Lots and Car Permits. Using
	 * both allows specific retrieval of ParkingCharges for updating permit bills.
	 */
	public ParkingCharge findParkingChargeByLotIdAndOwnerId(UUID lotId, UUID ownerId) {
		return charges.stream()
				.filter(charge -> charge.getLotId().equals(lotId) && charge.getPermitId().equals(ownerId)).findFirst()
				.orElse(null);
	}

	/*
	 * Retrieve all Cars matching given customerId. Used for calculating car permit
	 * bill.
	 */
	public List<ParkingCharge> findParkingChargesByOwnerId(UUID ownerId) {
		return charges.stream().filter(charge -> charge.getPermitId().equals(ownerId)).toList();
	}

	/*
	 * Once parking charges are successfully sent to a customer all charges matching
	 * that customer id are removed from the ParkingCharge list.
	 */
	public Boolean removeParkingChargesByOwnerId(UUID ownerId) {
		return charges.removeIf(charge -> charge.getPermitId().equals(ownerId));
	}

	/*
	 * Retrieve all Cars matching given customerId. Used for calculating monthly
	 * customer bill.
	 */
	public List<Car> findCarsByCustomerId(UUID customerId) {
		return cars.stream().filter(car -> car.getOwner().equals(customerId)).toList();
	}

	public List<ParkingCharge> getCharges() {
		return charges;
	}

	public void setCharges(List<ParkingCharge> charges) {
		this.charges = charges;
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, cars, charges, customers, lots, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParkingOffice other = (ParkingOffice) obj;
		return Objects.equals(address, other.address) && Objects.equals(cars, other.cars)
				&& Objects.equals(charges, other.charges) && Objects.equals(customers, other.customers)
				&& Objects.equals(lots, other.lots) && Objects.equals(name, other.name);
	}

}
