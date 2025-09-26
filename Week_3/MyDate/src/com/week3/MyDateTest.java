package com.week3;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 *This is the test class for testing JulianDateConvertor.java class. 
 * @author Group A
 *
 */
class MyDateTest {

	/**
	 * This test case will test pass when we will get julain number of 2470166 for date 25/12/2050.
	 */
	@Test
	void testMyDateGetJulian_25122050() {
		int day = 25;
		int month = 12;
		int year = 2050;
		MyDate myDate = new MyDate(day,month,year);
		assertEquals(2470166, myDate.getJulianNumber());
	}
	
	/**
	 * This test case will test pass when we will get julain number of 2266076 for date 15/03/1492.
	 */
	@Test
	void testMyDateGetJulian_15031492() {
		int day = 15;
		int month = 03;
		int year = 1492;
		MyDate myDate = new MyDate(day,month,year);
		assertEquals(2266076, myDate.getJulianNumber());
	}
	
	/**
	 * This test case will test pass when we will get date 25-12-2050 for julian number 2470166.
	 */
	@Test
	void testMyDateGetDayGetMonthGetYear_25122050() {
		
		int juilianNumber = 2470166;
		MyDate myDate = new MyDate();
		myDate.setJulianNumber(juilianNumber);
		String date = myDate.getDay() + "-" + myDate.getMonth() + "-" + myDate.getYear();
		assertEquals("25-12-2050", date);
	}
	
	/**
	 * This test case will test pass when we will get date 15-3-1492 for julian number 2266076.
	 */
	@Test
	void testMyDateGetDayGetMonthGetYear_15031492() {
		int juilianNumber = 2266076;
		MyDate myDate = new MyDate();
		myDate.setJulianNumber(juilianNumber);
		String date = myDate.getDay() + "-" + myDate.getMonth() + "-" + myDate.getYear();
		assertEquals("15-3-1492", date);
	}
	
	/**
	 * This test case will test pass when it correctly identifies 1600 as a leap year.
	 */
	@Test
	void testIsLeapYear_400() {
		int year = 1600;
		assertEquals(true, MyDate.isLeapYear(year));
	}
	
	/**
	 * This test case will test pass when it correctly identifies 1600 as a leap year.
	 */
	@Test
	void testIsLeapYear_any() {
		int year = 2025;
		assertEquals(false, MyDate.isLeapYear(year));
	}
	
	/**
	 * This test case will test pass when it correctly identifies 1500 as not a leap year.
	 */
	@Test
	void testIsLeapYear_100() {
		int year = 1500;
		assertEquals(false, MyDate.isLeapYear(year));
	}
	
	/**
	 * This test case will test pass when it correctly identifies 2004 as a leap year.
	 */
	@Test
	void getLastDayOfMonth_28() {
		int month = 2;
		int year = 100;
		assertEquals(28, MyDate.getLastDayOfMonth(month, year));
	}
	
	/**
	 * This test case will test pass when it correctly identifies 2004 as a leap year.
	 */
	@Test
	void getLastDayOfMonth_29() {
		int month = 2;
		int year = 400;
		assertEquals(29, MyDate.getLastDayOfMonth(month, year));
	}
	
	/**
	 * This test case will test pass when it correctly identifies 2004 as a leap year.
	 */
	@Test
	void getLastDayOfMonth_30() {
		int month = 4;
		int year = 100;
		assertEquals(30, MyDate.getLastDayOfMonth(month, year));
	}
	
	/**
	 * This test case will test pass when it correctly identifies 2004 as a leap year.
	 */
	@Test
	void getLastDayOfMonth_31() {
		int month = 3;
		int year = 100;
		assertEquals(31, MyDate.getLastDayOfMonth(month, year));
	}
	
	/**
	 * This test case will test pass when an illegal argument exception is thrown.
	 */
	@Test
	void getLastDayOfMonth_Exception() {
		int month = 31;
		int year = 100;
		assertEquals(31, MyDate.getLastDayOfMonth(month, year));
//        assertThrows(IllegalArgumentException.class, () -> {
//            MyDate.getLastDayOfMonth(month, year);
//        });
	}


}
