package month;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class MonthAppTest {

	private Month<Integer>[] monthNumberArray;
	private Month<String>[] monthNameArray;
	private ArrayList<Month<Integer>> monthNumberList;
	private ArrayList<Month<String>> monthNameList;
	private HashMap<Month<Integer>, Month<String>> monthMap;

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void setUp()
			throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
		monthNumberArray = new Month[12];
		monthNameArray = new Month[12];
		monthNumberList = new ArrayList<>();
		monthNameList = new ArrayList<>();
		monthMap = new HashMap<>();

		Method populateMethod = MonthApp.class.getDeclaredMethod("populateMonthData", Month[].class, Month[].class,
				ArrayList.class, ArrayList.class, HashMap.class);
		populateMethod.setAccessible(true);
		populateMethod.invoke(null, monthNumberArray, monthNameArray, monthNumberList, monthNameList, monthMap);

	}

	@Test
	public void testArrayPopulation() {
		assertEquals(12, monthNumberArray.length);
		assertEquals(12, monthNameArray.length);

		assertEquals(1, monthNumberArray[0].getMonth());
		assertEquals("January", monthNameArray[0].getMonth());

		assertEquals(12, monthNumberArray[11].getMonth());
		assertEquals("December", monthNameArray[11].getMonth());
	}

	@Test
	public void testArrayListPopulation() {
		assertEquals(12, monthNumberList.size());
		assertEquals(12, monthNameList.size());

		assertEquals(1, monthNumberList.get(0).getMonth());
		assertEquals("January", monthNameList.get(0).getMonth());
	}

	@Test
	public void testHashMapPopulation() {
		for (int i = 0; i < 12; i++) {
			Month<Integer> key = monthNumberArray[i];
			Month<String> value = monthMap.get(key);
			assertNotNull(value, "Month name should not be null for key " + key.getMonth());
			assertEquals(monthNameArray[i].getMonth(), value.getMonth());
		}
	}

	@Test
	public void testReadOutMonthDataOutput() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(outContent));

		// Call the public readOutMonthData method
		MonthApp.readOutMonthData(monthNumberArray, monthNameArray, monthNumberList, monthNameList, monthMap);

		System.setOut(originalOut); // Reset to original System.out

		String output = outContent.toString();

		// Basic validations on output
		assertTrue(output.contains("Print out using Arrays:"), "Should print array section.");
		assertTrue(output.contains("1 = January"), "Should print January correctly.");
		assertTrue(output.contains("12 = December"), "Should print December correctly.");
		assertTrue(output.contains("Print out using ArrayLists:"), "Should print ArrayList section.");
		assertTrue(output.contains("Print out using HashMap:"), "Should print HashMap section.");
	}

	@Test
	public void testMainMethodRunsSuccessfully() {
		assertDoesNotThrow(() -> MonthApp.main(new String[0]));
	}
}
