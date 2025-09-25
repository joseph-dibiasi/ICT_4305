package ict4305.week2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import ict4305.week2.JulianDateConvertor;
import org.junit.jupiter.api.Test;

/**
 *This is the test class for testing JulianDateConvertor.java class. 
 * @author Group A
 *
 */
class JulianDateConvertorTest {

	/**
	 * This test case will test pass when we will get julain number of 2470166 for date 25/12/2050.
	 */
	@Test
	void testDateToJulian_25122050() {
		int day = 25;
		int month = 12;
		int year = 2050;
		JulianDateConvertor juilian = new JulianDateConvertor(day,month,year);
		assertEquals(2470166, juilian.convertDateToJulian());
	}
	
	/**
	 * This test case will test pass when we will get date 25-12-2050 for julian number 2470166.
	 */
	@Test
	void testJulianToDate_25122050() {
		
		int juilianNumber = 2470166;
		JulianDateConvertor juilianConvertor = new JulianDateConvertor(juilianNumber);
		
		assertEquals("25-12-2050", juilianConvertor.convertJulianToDate());
	}
	
	/**
	 * This test case will test pass when we will get julian number 2470166 for date "25122050".
	 */
	@Test
	void testDateStringToJulian_25122050() {
		String dateString = "25122050";
		JulianDateConvertor juilianConvertor = new JulianDateConvertor(dateString);
		assertEquals(2470166, juilianConvertor.convertDateToJulian());
	}
	
	/**
	 * This test case will test pass when we will get julain number of 2266076 for date 15/03/1492.
	 */
	@Test
	void testDateToJulian_15031492() {
		int day = 15;
		int month = 03;
		int year = 1492;
		JulianDateConvertor juilianConvertor = new JulianDateConvertor(day,month,year);
		assertEquals(2266076, juilianConvertor.convertDateToJulian());
	}
	
	/**
	 * This test case will test pass when we will get date 15-3-1492 for julian number 2266076.
	 */
	@Test
	void testJulianToDate_15031492() {
		int juilianNumber = 2266076;
		JulianDateConvertor juilianConvertor = new JulianDateConvertor(juilianNumber);
		
		assertEquals("15-3-1492", juilianConvertor.convertJulianToDate());
	}
	
	/**
	 * This test case will test pass when we will get julian number 2266076 for date "15031492".
	 */
	@Test
	void testDateStringToJulian_15031492() {
		String dateString = "15031492";
		JulianDateConvertor juilianConvertor = new JulianDateConvertor(dateString);
		assertEquals(2266076, juilianConvertor.convertDateToJulian());
	}


}
