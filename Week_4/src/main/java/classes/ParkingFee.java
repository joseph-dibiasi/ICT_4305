package classes;

import java.time.LocalDateTime;
import java.util.UUID;

public class ParkingFee {

	public ParkingFee(Integer rate, Boolean dailyRate, Double lotFee, UUID lotId) {
		this.rate = rate;
		this.dailyRate = dailyRate;
		this.lotFee = lotFee;
		this.lotId = lotId;
	}

	public ParkingFee(Integer rate, Boolean dailyRate, Double lotFee, UUID lotId, LocalDateTime now) {
		this.rate = rate;
		this.dailyRate = dailyRate;
		this.lotFee = lotFee;
		this.lotId = lotId;
		this.entryTime = now;
	}

	private Integer rate;
	private Boolean dailyRate;

	private Double lotFee;
	private UUID lotId;
	private LocalDateTime entryTime;
	private Double totalFee;

	public Integer getRate() {
		return rate;
	}

	public void setRate(Integer rate) {
		this.rate = rate;
	}

	public Boolean getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(Boolean dailyRate) {
		this.dailyRate = dailyRate;
	}

	public Double getLotFees() {
		return lotFee;
	}

	public void setLotFees(Double lotFee) {
		this.lotFee = lotFee;
	}

	public UUID getLotId() {
		return lotId;
	}

	public void setLotId(UUID lotId) {
		this.lotId = lotId;
	}

	public LocalDateTime getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(LocalDateTime entryTime) {
		this.entryTime = entryTime;
	}

	public Double getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(Double totalFee) {
		this.totalFee = totalFee;
	}

}
