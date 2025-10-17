package classes;

public class Money {
	
	public Money() {
		
	}
	
	public Money(Long cents) {
		this.cents = cents;
	}
	
	public Money(Double dollars) {
		this.cents = Math.round(dollars * 100);
	}

	private Long cents;

	public Long getCents() {
		return cents;
	}

	public void setCents(Long cents) {
		this.cents = cents;
	}

	public Double getDollars() {
		return this.cents / 100.0;
	}

	@Override
	public String toString() {
		return "Money [cents=" + cents + "]";
	}
}
