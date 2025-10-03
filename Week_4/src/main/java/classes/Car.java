package classes;

import java.time.LocalDate;

public class Car {
	
	private String permit;
	
	private LocalDate permitExpiration;

	private String license;
	
	private CarType type;
	
	private String owner;
	
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}
