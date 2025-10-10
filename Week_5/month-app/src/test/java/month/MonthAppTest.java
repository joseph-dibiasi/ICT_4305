package month;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class MonthAppTest {

    @Test
    public void testArrayPopulation() {
        Month<Integer>[] monthNumberArray = new Month[12];
        Month<String>[] monthNameArray = new Month[12];

        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        for (int i = 0; i < 12; i++) {
            monthNumberArray[i] = new Month<>(i + 1);
            monthNameArray[i] = new Month<>(monthNames[i]);
        }

        assertEquals(1, monthNumberArray[0].getMonth());
        assertEquals("January", monthNameArray[0].getMonth());
        assertEquals(12, monthNumberArray[11].getMonth());
        assertEquals("December", monthNameArray[11].getMonth());
    }

    @Test
    public void testArrayListPopulation() {
        ArrayList<Month<Integer>> numberList = new ArrayList<>();
        ArrayList<Month<String>> nameList = new ArrayList<>();

        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        for (int i = 0; i < 12; i++) {
            numberList.add(new Month<>(i + 1));
            nameList.add(new Month<>(monthNames[i]));
        }

        assertEquals(12, numberList.size());
        assertEquals("July", nameList.get(6).getMonth());
        assertEquals(8, numberList.get(7).getMonth());
    }

    @Test
    public void testHashMapPopulation() {
        HashMap<Month<Integer>, Month<String>> monthMap = new HashMap<>();

        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        for (int i = 0; i < 12; i++) {
            Month<Integer> key = new Month<>(i + 1);
            Month<String> value = new Month<>(monthNames[i]);
            monthMap.put(key, value);
        }

        // Validate that keys and values exist
        Month<Integer> testKey = new Month<>(1);
        assertTrue(monthMap.containsKey(testKey));  // May fail if hashCode and equals not overridden

        // To make this test pass reliably, you'd need to override equals and hashCode in Month class.
    }
}

