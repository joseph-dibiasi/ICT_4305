package classes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CarTypeTest {

    @Test
    public void testEnumValues() {
        CarType[] vals = CarType.values();
        assertTrue(vals.length >= 2);
        assertEquals(CarType.SUV, CarType.valueOf("SUV"));
        assertEquals(CarType.COMPACT, CarType.valueOf("COMPACT"));
    }
}