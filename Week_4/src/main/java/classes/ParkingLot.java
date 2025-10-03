package classes;

public class ParkingLot {
	
	private String lotId;
	
	private Address address;
	
	private Integer capacity;
	
	public void entry(Car car) {
		
	}

	public String toString() {
		return "";
	}

	public String getLotId() {
		return lotId;
	}

	public void setLotId(String lotId) {
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
}
