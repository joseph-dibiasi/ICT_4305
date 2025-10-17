package classes;

public class Money {
	
	public Money() {
		
	}
	
	public Money(long cents) {
		this.cents = cents;
	}

	private long cents;

	public double getDollars() {
		return this.cents / 100.0;
	}
	
	@Override
	public String toString() {
		return "Money [cents=" + cents + "]";
	}
}
