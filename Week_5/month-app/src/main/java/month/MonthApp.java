package month;

import java.util.ArrayList;
import java.util.HashMap;

public class MonthApp {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// Arrays: holds Month<Integer> and Month<String>. Requires preset size.
		Month<Integer>[] monthNumberArray = new Month[12]; // Ignore warnings
		Month<String>[] monthNameArray = new Month[12]; // Ignore warnings

		// ArrayLists: holds Month<Integer> and Month<String>. Variable size.
		ArrayList<Month<Integer>> monthNumberList = new ArrayList<>();
		ArrayList<Month<String>> monthNameList = new ArrayList<>();

		// HashMap: Key = Month<Integer>, Value = Month<String>.
		HashMap<Month<Integer>, Month<String>> monthMap = new HashMap<>();

		populateMonthData(monthNumberArray, monthNameArray, monthNumberList, monthNameList, monthMap);

		readOutMonthData(monthNumberArray, monthNameArray, monthNumberList, monthNameList, monthMap);
	}

	public static void readOutMonthData(Month<Integer>[] monthNumberArray, Month<String>[] monthNameArray,
			ArrayList<Month<Integer>> monthNumberList, ArrayList<Month<String>> monthNameList,
			HashMap<Month<Integer>, Month<String>> monthMap) {
		// Read out and print month data from Arrays using For Loop.
		System.out.println("Print out using Arrays:");
		for (int i = 0; i < 12; i++) {
			System.out.println(monthNumberArray[i].getMonth() + " = " + monthNameArray[i].getMonth());
		}

		// Read out and print month data from ArrayLists using For Loop.
		System.out.println("\nPrint out using ArrayLists:");
		for (int i = 0; i < 12; i++) {
			System.out.println(monthNumberList.get(i).getMonth() + " = " + monthNameList.get(i).getMonth());
		}

		// Read out and print month data from HashMap.
		System.out.println("\nPrint out using HashMap:");
		// Since HashMaps are unordered, Stream is used to sort the months then print
		// them using a ForEach Loop.
		monthMap.entrySet().stream().sorted((e1, e2) -> e1.getKey().getMonth() - e2.getKey().getMonth()) // compare
																											// month
																											// numbers
				.forEach(entry -> System.out.println(entry.getKey().getMonth() + " = " + entry.getValue().getMonth()));
	}

	private static void populateMonthData(Month<Integer>[] monthNumberArray, Month<String>[] monthNameArray,
			ArrayList<Month<Integer>> monthNumberList, ArrayList<Month<String>> monthNameList,
			HashMap<Month<Integer>, Month<String>> monthMap) {
		// Month names listed sequentially to correspond with month numbers.
		String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
				"October", "November", "December" };

		// Populate collections using For Loop. Same index can be used for all
		// collections.
		for (int i = 0; i < 12; i++) {
			// Month number and name correspond to the same index
			int monthNum = i + 1;
			String monthNameStr = monthNames[i];

			// Create Month<Integer> and Month<String> objects.
			Month<Integer> monthNumber = new Month<>(monthNum);
			Month<String> monthName = new Month<>(monthNameStr);

			// Adding to arrays.
			monthNumberArray[i] = monthNumber;
			monthNameArray[i] = monthName;

			// Adding to ArrayLists.
			monthNumberList.add(monthNumber);
			monthNameList.add(monthName);

			// Adding to HashMap.
			monthMap.put(monthNumber, monthName);
		}
	}
}