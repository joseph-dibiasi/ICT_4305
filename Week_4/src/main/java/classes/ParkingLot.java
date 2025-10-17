package classes;

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

	private Money lotFee;

	private Set<Car> parkedCars;

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

	public Money getLotFee() {
		return lotFee;
	}

	public void setLotFee(Money lotFee) {
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
