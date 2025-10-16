package classes;

import java.time.Instant;
import java.util.UUID;

public class ParkingCharge {
	
	// Customer ID. Unique.
	private UUID permitId;
	// Parking Lot ID. Unique.
	private UUID lotId;
	private Instant incurred;
	private Money amount;
	
	public UUID getPermitId() {
		return permitId;
	}
	public void setPermitId(UUID permitId) {
		this.permitId = permitId;
	}
	public UUID getLotId() {
		return lotId;
	}
	public void setLotId(UUID lotId) {
		this.lotId = lotId;
	}
	public Instant getIncurred() {
		return incurred;
	}
	public void setIncurred(Instant incurred) {
		this.incurred = incurred;
	}
	public Money getAmount() {
		return amount;
	}
	public void setAmount(Money amount) {
		this.amount = amount;
	}
	
	@Override
	public String toString() {
		return "ParkingCharge [permidId=" + permitId + ", lotId=" + lotId + ", incurred=" + incurred + ", amount="
				+ amount + "]";
	}

}
