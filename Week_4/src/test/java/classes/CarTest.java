package classes;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @Test
    void testDefaultConstructor() {
        Car car = new Car();
        assertNull(car.getPermit());
        assertNull(car.getPermitExpiration());
        assertNull(car.getLicense());
        assertNull(car.getType());
        assertNull(car.getOwner());
    }

    @Test
    void testParameterizedConstructor() {
        String permit = "John Doe";
        LocalDate expiration = LocalDate.of(2025, 12, 31);
        String license = "ABC-1234";
        CarType type = CarType.SUV;
        UUID owner = UUID.randomUUID();

        Car car = new Car(permit, expiration, license, type, owner);

        assertEquals(permit, car.getPermit());
        assertEquals(expiration, car.getPermitExpiration());
        assertEquals(license, car.getLicense());
        assertEquals(type, car.getType());
        assertEquals(owner, car.getOwner());
    }

    @Test
    void testSetAndGetPermit() {
        Car car = new Car();
        car.setPermit("Alice");
        assertEquals("Alice", car.getPermit());
    }

    @Test
    void testSetAndGetPermitExpiration() {
        Car car = new Car();
        LocalDate date = LocalDate.of(2030, 1, 1);
        car.setPermitExpiration(date);
        assertEquals(date, car.getPermitExpiration());
    }

    @Test
    void testSetAndGetLicense() {
        Car car = new Car();
        car.setLicense("XYZ-7890");
        assertEquals("XYZ-7890", car.getLicense());
    }

    @Test
    void testSetAndGetType() {
        Car car = new Car();
        car.setType(CarType.COMPACT);
        assertEquals(CarType.COMPACT, car.getType());
    }

    @Test
    void testSetAndGetOwner() {
        Car car = new Car();
        UUID ownerId = UUID.randomUUID();
        car.setOwner(ownerId);
        assertEquals(ownerId, car.getOwner());
    }

    @Test
    void testToString() {
        String permit = "Bob";
        LocalDate expiration = LocalDate.of(2026, 6, 15);
        String license = "LMN-4567";
        CarType type = CarType.SUV;
        UUID owner = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");

        Car car = new Car(permit, expiration, license, type, owner);
        String expected = "Car [permit=Bob, permitExpiration=2026-06-15, license=LMN-4567, type=SUV, owner=123e4567-e89b-12d3-a456-556642440000]";

        assertEquals(expected, car.toString());
    }
}
