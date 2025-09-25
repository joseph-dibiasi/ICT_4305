package com.week3;

public class MyDate {
	/*
	 * If no arguments were provided then default the date January 1st, 1970 (epoch
	 * time).
	 */
	public MyDate() {
		this.julianNumber = toJulianNumber(1, 1, 1970);
	};

	/* Creates a new MyDate from an existing MyDate */
	public MyDate(MyDate date) {

		this.julianNumber = toJulianNumber(date.getDay(), date.getMonth(), date.getYear());
	};

	/* Creates a new MyDate from a day, month, and year */
	public MyDate(int day, int month, int year) {
		this.julianNumber = toJulianNumber(day, month, year);
	};

	/* Returns the day of the month for this MyDate */
	public int getDay() {
		return fromJulianNumber()[0];
	};

	/* Returns the month of the year for this MyDate */
	public int getMonth() {
		return fromJulianNumber()[1];
	};

	/* Returns the year for this MyDate */
	public int getYear() {
		return fromJulianNumber()[2];
	};

	/* Returns true if this MyDate represents a date in a leap year */
	public static boolean isLeapYear(int year) {
		boolean isLeapYear = false;
        if (year % 400 == 0) {
        	isLeapYear = true;
        } else if (year % 100 != 0 && year % 4 == 0) {
        	isLeapYear = true;
        }
    	return isLeapYear;
	};

	public static int getLastDayOfMonth(int month, int year) {
		int lastDay;
		switch (month) {
		case 2:
			lastDay = isLeapYear(year) ? 29 : 28;
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			lastDay = 30;
			break;
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			lastDay = 31;
			break;
		default:
			throw new IllegalArgumentException("Invalid input for month: " + month + ". Must be a number between 1 and 12.");
		}
		
		return lastDay;
	};

	/*
	 * This internal method returns the calculated Julian number for the provided
	 * day, month, year This method is static, as it does not require a MyDate
	 * object to perform its computation
	 */
	private static int toJulianNumber(int day, int month, int year) {
		int julianNumber = ((1461 * (year + 4800 + (month - 14) / 12)) / 4)
				+ ((367 * (month - 2 - 12 * ((month - 14) / 12))) / 12)
				- ((3 * ((year + 4900 + (month - 14) / 12) / 100)) / 4) + day - 32075;

		return julianNumber;
	};

	/*
	 * This internal method returns a 3-integer array containing the day, month, and
	 * year of this MyDate
	 */
	private int[] fromJulianNumber() {
		int day;
		int month;
		int year;

		int l = 0;
		int n = 0;
		int i = 0;
		int j = 0;

		l = julianNumber + 68569;
		n = (4 * l) / 146097;
		l = l - (146097 * n + 3) / 4;
		i = (4000 * (l + 1)) / 1461001;
		l = l - (1461 * i) / 4 + 31;
		j = (80 * l) / 2447;
		day = l - (2447 * j) / 80;
		l = j / 11;
		month = j + 2 - (12 * l);
		year = 100 * (n - 49) + i + l;

		int[] date = { day, month, year };

		return date;
	};

	/*
	 * This private data member holds the calculated Julian number for this MyDate
	 */
	private int julianNumber;
	

	public int getJulianNumber() {
		return julianNumber;
	}

	public void setJulianNumber(int julianNumber) {
		this.julianNumber = julianNumber;
	}


}