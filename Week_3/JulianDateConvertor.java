package ict4305.week2;



/**
 * This class set the date and convert that date into Julian number and vice versa. 
 * 
 * @author Group A 
 *
 */
public class JulianDateConvertor {
	
	private int day;
	private int month;
	private int year;
	private int julianNumber;
	
	/**
	 * This constructor will take dateString as an input in <b>DDMMYYYY</b> format
	 * and set the day, month, and year attributes.
	 * 
	 * @param dateString
	 */
	public JulianDateConvertor(String dateString) {
		
		day = Integer.parseInt(dateString.substring(0, 2));
		month = Integer.parseInt(dateString.substring(2, 4));
		year = Integer.parseInt(dateString.substring(4));
	}

	/**
	 * This constructor will take the juilianNumber and set the attribute juilianNumber.
	 * 
	 * @param julianNumber
	 */
	public JulianDateConvertor(int julianNumber) {
		
		this.julianNumber = julianNumber;
	}

	/**
	 * This constructor take day, month, and year as an input and set the attributes accordingly. 
	 * 
	 * @param day
	 * @param month
	 * @param year
	 */
	public JulianDateConvertor(int day, int month, int year) {
		
		this.day = day;
		this.month = month;
		this.year = year;
	}
	
	/**
	 * This method will return the date in form of Julian number.
	 * 
	 * @return Juilian Number as Integer
	 */
	public int convertDateToJulian() {
		
		julianNumber = (( 1461 * (year + 4800 + (month - 14 ) / 12 ) ) / 4 )+ 
				(( 367 * (month - 2 - 12 * ( (month - 14 ) / 12 ) ) ) / 12) - 
				(( 3 * ( ( year + 4900 + ( month - 14 ) / 12 ) / 100 ) ) / 4) +
				day - 32075;
		
		return julianNumber;
	}
	
	/**
	 * This method will convert the Julian number into day, month, and year.
	 * Then it will create a date string in <b>DD-MM-YYYY</b> format and will return date string.
	 * 
	 * @return Date String
	 */
	public String convertJulianToDate() {
	
		String date = "";
		int l =0;
		int n = 0;
		int i = 0;
		int j=0;
		
		l = julianNumber + 68569; 
		n = ( 4 * l ) / 146097 ;
		l = l - ( 146097 * n + 3 ) / 4 ;
		i = ( 4000 * ( l + 1 ) ) / 1461001; 
		l = l - ( 1461 * i ) / 4 + 31 ;
		j = ( 80 * l ) / 2447 ;
		day = l - ( 2447 * j ) / 80; 
		l = j / 11 ;
		month = j + 2 - ( 12 * l ); 
		year = 100 * ( n - 49 ) + i + l; 
		
		
		date = day+"-"+month+"-"+year;
		
		return date;
	}
	
	

}
