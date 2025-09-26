package com.week3;

import java.util.Scanner;

public class MyDate {

	public static void main(String[] args) {
		int day;
		int month;
		int year;
		MyDate myDate;
		Scanner scanner = new Scanner(System.in); // Create a Scanner object
		System.out.println("Hello! Would you like to input date for MyDate object? (Y/N)");
		String input = scanner.nextLine();
		if (input.equalsIgnoreCase("y")) {
			System.out.println("Please enter day, month, and year to create MyDate object.");
			System.out.print("Enter day: ");
			day = scanner.nextInt(); // Read an integer

			System.out.print("Enter month: ");
			month = scanner.nextInt(); // Read an integer

			System.out.print("Enter year: ");
			year = scanner.nextInt(); // Read an integer
			myDate = new MyDate(day, month, year);

		} else {
			System.out.println("Defaulting to Epoch Time.");
			myDate = new MyDate();
		}
		scanner.close(); // close resource.

		System.out.println("The day for this MyDate object is: " + myDate.getDay());
		System.out.println("The month for this MyDate object is: " + myDate.getMonth());
		System.out.println("The year for this MyDate object is: " + myDate.getYear());
		System.out.println("The last day of the month for this MyDate object is: " + getLastDayOfMonth(myDate.getMonth(), myDate.getYear()));
		String leapYear = isLeapYear(myDate.getYear()) ? "is" : "is not";
		System.out.println("The year for this MyDate object " + leapYear + " a leap year.");
		System.out.println("The julian number for this date is: " + MyDate.toJulianNumber(myDate.getDay(), myDate.getMonth(), myDate.getYear()));
	}

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
		int day = fromJulianNumber()[0];
		return day;
	};

	/* Returns the month of the year for this MyDate */
	public int getMonth() {
		int month = fromJulianNumber()[1];
		return month;
	};

	/* Returns the year for this MyDate */
	public int getYear() {
		int year = fromJulianNumber()[2];
		return year;
	};

	/* Returns true if this MyDate represents a date in a leap year */
	public static boolean isLeapYear(int year) {
		boolean isLeapYear = false;
		if (year < 1) {
			System.out.println("Invalid year entered. Defaulting to Epoch Time year.");
		}
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
			System.out.println("Invalid month entered. Defaulting to 31 days.");
			lastDay = 31;
			break;
		}

		return lastDay;
	};

	/*
	 * This internal method returns the calculated Julian number for the provided
	 * day, month, year This method is static, as it does not require a MyDate
	 * object to perform its computation
	 */
	private static int toJulianNumber(int day, int month, int year) {
		if (year < 1) {
			System.out.println("Invalid year entered. Defaulting to Epoch Time year.");
			year = 1970;
		}
		if (month < 1 || month > 12) {
			System.out.println("Invalid month entered. Defaulting to Epoch Time month.");
			month = 1;
		}
		if (day < 1 || day > getLastDayOfMonth(month, year)) {
			System.out.println("Invalid day entered. Defaulting to Epoch Time day.");
			day = 1;
		}
		
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

	/*
	 * Public getter and setter for the calculated Julian number.
	 */
	public int getJulianNumber() {
		return julianNumber;
	}

	public void setJulianNumber(int julianNumber) {
		this.julianNumber = julianNumber;
	}

}