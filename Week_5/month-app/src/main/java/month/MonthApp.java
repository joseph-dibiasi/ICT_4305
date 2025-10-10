package month;

import java.util.ArrayList;
import java.util.HashMap;

public class MonthApp {
    public static void main(String[] args) {
        // Define arrays to hold Month objects
        Month<Integer>[] monthNumberArray = new Month[12];  // Ignore warnings
        Month<String>[] monthNameArray = new Month[12];     // Ignore warnings

        // Define ArrayLists
        ArrayList<Month<Integer>> monthNumberList = new ArrayList<>();
        ArrayList<Month<String>> monthNameList = new ArrayList<>();

        // Define HashMap: monthNumber (Integer) => monthName (String)
        HashMap<Month<Integer>, Month<String>> monthMap = new HashMap<>();

        // Month names array
        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        // Populate collections
        for (int i = 0; i < 12; i++) {
            int monthNum = i + 1;
            String monthNameStr = monthNames[i];

            // Create Month<Integer> and Month<String> objects
            Month<Integer> monthNumber = new Month<>(monthNum);
            Month<String> monthName = new Month<>(monthNameStr);

            // Add to arrays
            monthNumberArray[i] = monthNumber;
            monthNameArray[i] = monthName;

            // Add to ArrayLists
            monthNumberList.add(monthNumber);
            monthNameList.add(monthName);

            // Add to HashMap
            monthMap.put(monthNumber, monthName);
        }

        // Print from arrays
        System.out.println("From Arrays:");
        for (int i = 0; i < 12; i++) {
            System.out.println(monthNumberArray[i].getMonth() + " = " + monthNameArray[i].getMonth());
        }

        // Print from ArrayLists
        System.out.println("\nFrom ArrayLists:");
        for (int i = 0; i < 12; i++) {
            System.out.println(monthNumberList.get(i).getMonth() + " = " + monthNameList.get(i).getMonth());
        }

        // Print from HashMap
        System.out.println("\nFrom HashMap:");
        for (Month<Integer> key : monthMap.keySet()) {
            System.out.println(key.getMonth() + " = " + monthMap.get(key).getMonth());
        }
    }
}