package classes;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ParkingLot {

	// Parking Lot ID. Unique.
	private UUID lotId;

	private Address address;

	private Integer capacity;

	// True = Hourly Rate. False = Daily Rate.
	private Boolean chargeOnExit;

	private Double lotFee;

	private Set<Car> parkedCars;

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
	public void entry(Car car) {
		try {
			if (car.getPermit() == null) {
				throw new RuntimeException("Permit required to enter parking lot.");
			}
			if (car.getPermitExpiration().isBefore(LocalDateTime.now().toLocalDate())) {
				throw new RuntimeException("Permit expired. Please contact Parking Office.");
			}
			if (getParkedCars().size() >= getCapacity()) {
				System.err.println("Parking Lot Full.");
				throw new RuntimeException("Parking Lot Full.");
			}

			if (!this.chargeOnExit) {
				ParkingFee fee = new ParkingFee(1, Boolean.TRUE, this.lotFee, this.lotId);
				car.updateParkingFees(fee, Boolean.TRUE);
			} else {
				ParkingFee fee = new ParkingFee(0, Boolean.FALSE, this.lotFee, this.lotId, LocalDateTime.now());
				car.updateParkingFees(fee, Boolean.FALSE);
			}
			if (this.parkedCars.contains(car)) {
				System.err.println("Car already parked in the lot.");
				throw new RuntimeException("Car already parked in the lot.");
			}
			this.parkedCars.add(car);
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
		for (Car car : this.parkedCars) {
			ParkingFee fee = new ParkingFee(1, Boolean.TRUE, this.lotFee, this.lotId);
			car.updateParkingFees(fee, Boolean.TRUE);
		}
	}

	public UUID getLotId() {
		return lotId;
	}

	public void setLotId(UUID lotId) {
		this.lotId = lotId;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	@Override
	public String toString() {
		return "ParkingLot [lotId=" + lotId + ", address=" + address + ", capacity=" + capacity + "]";
	}

	public Boolean getChargeOnExit() {
		return chargeOnExit;
	}

	public void setChargeOnExit(Boolean chargeOnExit) {
		this.chargeOnExit = chargeOnExit;
	}

	public Double getLotFee() {
		return lotFee;
	}

	public void setLotFee(Double lotFee) {
		this.lotFee = lotFee;
	}

	public Set<Car> getParkedCars() {
		if (parkedCars == null) {
			setParkedCars(new HashSet<Car>());
		}
		return parkedCars;
	}

	public void setParkedCars(Set<Car> parkedCars) {
		this.parkedCars = parkedCars;
	}

}
