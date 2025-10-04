package classes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ParkingLotTest {

    private ParkingLot parkingLot;
    private UUID lotId;
    private Car car;

    @BeforeEach
    void setUp() {
        lotId = UUID.randomUUID();

        parkingLot = new ParkingLot();
        parkingLot.setLotId(lotId);
        parkingLot.setCapacity(2);
        parkingLot.setChargeOnExit(false);
        parkingLot.setLotFee(10.0);
        parkingLot.setParkedCars(new HashSet<>());

        car = new Car();
        car.setLicense("ABC123");
        car.setOwner(UUID.randomUUID());
        car.setType(CarType.SUV);
        car.setPermit("PERMIT123");
        car.setPermitExpiration(LocalDate.now().plusDays(1));
        car.setParkingFees(new java.util.HashMap<>());
    }

    @Test
    void testEntryChargeOnEntrySuccess() {
        parkingLot.setChargeOnExit(false);

        parkingLot.entry(car);

        assertTrue(parkingLot.getParkedCars().contains(car));
        assertTrue(car.getParkingFees().containsKey(lotId));
        assertEquals(1, car.getParkingFees().get(lotId).getRate());
        assertEquals(10.0, car.getParkingFees().get(lotId).getLotFees(), 0.0001);
    }

    @Test
    void testEntryChargeOnExitSuccess() {
        parkingLot.setChargeOnExit(true);

        parkingLot.entry(car);

        assertTrue(parkingLot.getParkedCars().contains(car));
        assertNotNull(car.getParkingFees().get(lotId).getEntryTime());
        assertEquals(0, car.getParkingFees().get(lotId).getRate());
    }

    @Test
    void testEntryFailsWithoutPermit() {
        car.setPermit(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> parkingLot.entry(car));
        assertEquals("Permit required to enter parking lot.", ex.getMessage());
    }

    @Test
    void testEntryFailsWithExpiredPermit() {
        car.setPermitExpiration(LocalDate.now().minusDays(1));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> parkingLot.entry(car));
        assertEquals("Permit expired. Please contact Parking Office.", ex.getMessage());
    }

    @Test
    void testEntryFailsIfLotIsFull() {
        parkingLot.setCapacity(1);
        Car car1 = new Car();
        car1.setPermit("PERMIT1");
        car1.setPermitExpiration(LocalDate.now().plusDays(1));
        car1.setLicense("CAR1");
        car1.setOwner(UUID.randomUUID());
        car1.setParkingFees(new java.util.HashMap<>());

        parkingLot.entry(car1);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> parkingLot.entry(car));
        assertEquals("Parking Lot Full.", ex.getMessage());
    }

    @Test
    void testEntryFailsIfCarAlreadyParked() {
        parkingLot.getParkedCars().add(car);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> parkingLot.entry(car));
        assertEquals("Car already parked in the lot.", ex.getMessage());
    }

    @Test
    void testExitUpdatesFeeForChargeOnExit() throws InterruptedException {
        parkingLot.setChargeOnExit(true);
        parkingLot.entry(car);

        // Simulate 2 hours later by manipulating entry time
        ParkingFee fee = car.getParkingFees().get(lotId);
        fee.setEntryTime(LocalDateTime.now().minusHours(2));

        parkingLot.exit(car);

        assertFalse(parkingLot.getParkedCars().contains(car));
        assertEquals(2, car.getParkingFees().get(lotId).getRate()); // 2 hours
        assertNull(car.getParkingFees().get(lotId).getEntryTime());
    }

    @Test
    void testExitNoExceptionWhenChargeOnEntry() {
        parkingLot.setChargeOnExit(false);
        parkingLot.entry(car);

        assertDoesNotThrow(() -> parkingLot.exit(car));
        assertFalse(parkingLot.getParkedCars().contains(car));
    }

    @Test
    void testUpdateDailyFeesAddsFeesForAllParkedCars() {
        parkingLot.setChargeOnExit(false);
        Car car2 = new Car();
        car2.setPermit("PERMIT2");
        car2.setPermitExpiration(LocalDate.now().plusDays(1));
        car2.setLicense("XYZ789");
        car2.setOwner(UUID.randomUUID());
        car2.setParkingFees(new java.util.HashMap<>());

        parkingLot.entry(car);
        parkingLot.entry(car2);

        parkingLot.updateDailyFees();

        // After update, rate should be 2 for both cars (initial + daily update)
        assertEquals(2, car.getParkingFees().get(lotId).getRate());
        assertEquals(2, car2.getParkingFees().get(lotId).getRate());
    }
}
