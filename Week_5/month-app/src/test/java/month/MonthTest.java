package month;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MonthTest {

    @Test
    public void testDefaultConstructor() {
        Month<String> month = new Month<>();
        assertNull(month.getMonth(), "Default constructor should set month to null.");
    }

    @Test
    public void testParameterizedConstructor() {
        Month<Integer> month = new Month<>(10);
        assertEquals(10, month.getMonth(), "Constructor should set the correct value.");
    }

    @Test
    public void testGetterSetter() {
        Month<String> month = new Month<>();
        month.setMonth("October");
        assertEquals("October", month.getMonth(), "Getter should return the value set by setter.");
    }

    @Test
    public void testDifferentTypes() {
        Month<Double> month = new Month<>(5.5);
        assertEquals(5.5, month.getMonth(), "Generic type should handle different data types.");
    }

    @Test
    public void testHashMapUsage() {
        Month<Integer> key = new Month<>(1);
        Month<String> value = new Month<>("January");
        java.util.HashMap<Month<Integer>, Month<String>> map = new java.util.HashMap<>();
        map.put(key, value);

        assertEquals("January", map.get(key).getMonth());
    }
}
