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

	// Customer name. Not unique.
	private String permit;

	private LocalDate permitExpiration;

	// License plate number. Unique.
	private String license;

	private CarType type;

	// Customer ID. Unique.
	private UUID owner;

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


}
