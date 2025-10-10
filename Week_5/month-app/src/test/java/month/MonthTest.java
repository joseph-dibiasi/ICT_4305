package month;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MonthTest {

    @Test
    public void testIntegerMonth() {
        Month<Integer> month = new Month<>();
        month.setMonth(5);
        assertEquals(5, month.getMonth());

        Month<Integer> anotherMonth = new Month<>(12);
        assertEquals(12, anotherMonth.getMonth());
    }

    @Test
    public void testStringMonth() {
        Month<String> month = new Month<>();
        month.setMonth("August");
        assertEquals("August", month.getMonth());

        Month<String> anotherMonth = new Month<>("December");
        assertEquals("December", anotherMonth.getMonth());
    }

    @Test
    public void testNullMonth() {
        Month<String> month = new Month<>();
        month.setMonth(null);
        assertNull(month.getMonth());
    }
}

