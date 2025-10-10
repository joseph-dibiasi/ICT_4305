package month;

public class Month<T> {
	private T month;

	// Constructor
	public Month() {
	}

	public Month(T month) {
		this.month = month;
	}

	// Getter
	public T getMonth() {
		return month;
	}

	// Setter
	public void setMonth(T month) {
		this.month = month;
	}
}