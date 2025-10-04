package classes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.UUID;

public class Car {

	public Car() {
	}

	public Car(String permit, LocalDate permitExpiration, String license, CarType type, UUID owner) {
		super();
		this.permit = permit;
		this.permitExpiration = permitExpiration;
		this.license = license;
		this.type = type;
		this.owner = owner;
	}

	private String permit;

	private LocalDate permitExpiration;

	private String license;

	private CarType type;

	private UUID owner;

	private HashMap<UUID, ParkingFee> parkingFees;

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
	public void updateParkingFees(ParkingFee fee, Boolean dailyRate) {
		if (this.getParkingFees().containsKey(fee.getLotId())) {
			ParkingFee existingFee = this.getParkingFees().get(fee.getLotId());
			if (dailyRate) {
				existingFee.setRate(existingFee.getRate() + fee.getRate());
				existingFee.setTotalFee(existingFee.getRate() * existingFee.getLotFees());
			} else if (existingFee.getEntryTime() == null) {
				existingFee.setTotalFee(existingFee.getRate() * existingFee.getLotFees());
			}
			this.parkingFees.put(fee.getLotId(), existingFee);
		} else {
			this.parkingFees.put(fee.getLotId(), fee);
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
		for (ParkingFee fee : parkingFees.values()) {
			total += fee.getTotalFee();
		}

		if (this.getType() == CarType.COMPACT) {
			total = total * 0.8;
		}

		return total;
	}

	public String getPermit() {
		return permit;
	}

	public void setPermit(String permit) {
		this.permit = permit;
	}

	public LocalDate getPermitExpiration() {
		return permitExpiration;
	}

	public void setPermitExpiration(LocalDate permitExpiration) {
		this.permitExpiration = permitExpiration;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public CarType getType() {
		return type;
	}

	public void setType(CarType type) {
		this.type = type;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "Car [permit=" + permit + ", permitExpiration=" + permitExpiration + ", license=" + license + ", type="
				+ type + ", owner=" + owner + "]";
	}

	public HashMap<UUID, ParkingFee> getParkingFees() {
		if (parkingFees == null) {
			setParkingFees(new HashMap<UUID, ParkingFee>());
		}
		return parkingFees;
	}

	public void setParkingFees(HashMap<UUID, ParkingFee> parkingFees) {
		this.parkingFees = parkingFees;
	}

}
