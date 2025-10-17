package classes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingOfficeTest {

    private ParkingOffice parkingOffice;

    @BeforeEach
    void setUp() {
        parkingOffice = new ParkingOffice("University Parking", createTestAddress());
    }

    private Address createTestAddress() {
        Address address = new Address();
        address.setStreetAddress1("123 Main St");
        address.setCity("Springfield");
        address.setState("IL");
        address.setZipCode("62701");
        return address;
    }

    @Test
    void testRegisterCustomer() {
        Customer customer = parkingOffice.register("Alice", createTestAddress(), "555-1234");

        assertNotNull(customer.getCustomerId());
        assertEquals("Alice", customer.getName());
        assertEquals(1, parkingOffice.getCustomers().size());
    }

    @Test
    void testRegisterCarForCustomer() {
        Customer customer = parkingOffice.register("Bob", createTestAddress(), "555-5678");
        Car car = parkingOffice.register(customer, "XYZ123", CarType.SUV);

        assertNotNull(car);
        assertEquals("XYZ123", car.getLicense());
        assertEquals(customer.getCustomerId(), car.getOwner());
        assertEquals(1, parkingOffice.getCars().size());
    }

    @Test
    void testEntryFailsWithoutPermit() {
        Customer customer = parkingOffice.register("Charlie", createTestAddress(), "555-0000");
        Car car = parkingOffice.register(customer, "ABC123", CarType.COMPACT);

        // Remove permit to simulate failure case
        car.setPermit(null);

        ParkingLot lot = createLot(UUID.randomUUID(), 5, true, 10.0);
        parkingOffice.getLots().add(lot);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> parkingOffice.entry(lot, car));
        assertEquals("Permit required to enter parking lot.", exception.getMessage());
    }

    @Test
    void testEntryFailsWithExpiredPermit() {
        Customer customer = parkingOffice.register("Diana", createTestAddress(), "555-0001");
        Car car = parkingOffice.register(customer, "DEF456", CarType.COMPACT);
        car.setPermit("Diana");
        car.setPermitExpiration(LocalDate.now().minusDays(1)); // expired

        ParkingLot lot = createLot(UUID.randomUUID(), 5, true, 10.0);
        parkingOffice.getLots().add(lot);

        assertThrows(RuntimeException.class, () -> parkingOffice.entry(lot, car));
    }

    @Test
    void testEntryFailsIfLotIsFull() {
        Customer customer = parkingOffice.register("Eve", createTestAddress(), "555-0002");
        Car car = parkingOffice.register(customer, "GHI789", CarType.SUV);
        car.setPermit("Eve");
        car.setPermitExpiration(LocalDate.now().plusDays(10));

        ParkingLot lot = createLot(UUID.randomUUID(), 0, true, 10.0); // capacity = 0
        parkingOffice.getLots().add(lot);

        assertThrows(RuntimeException.class, () -> parkingOffice.entry(lot, car));
    }

    @Test
    void testSuccessfulEntry() {
        Customer customer = parkingOffice.register("Frank", createTestAddress(), "555-0003");
        Car car = parkingOffice.register(customer, "JKL012", CarType.SUV);
        car.setPermit("Frank");
        car.setPermitExpiration(LocalDate.now().plusDays(10));

        ParkingLot lot = createLot(UUID.randomUUID(), 3, false, 20.0);
        parkingOffice.getLots().add(lot);

        assertDoesNotThrow(() -> parkingOffice.entry(lot, car));
        assertTrue(lot.getParkedCars().contains(car));
    }

    @Test
    void testExitHourlyLot() {
        UUID lotId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        Car car = new Car("Permit1", LocalDate.now().plusDays(10), "MNO345", CarType.SUV, ownerId);

        ParkingLot lot = createLot(lotId, 5, true, 5.0);
        lot.getParkedCars().add(car);

        ParkingCharge charge = new ParkingCharge();
        charge.setLotId(lotId);
        charge.setPermitId(ownerId);
        charge.setIncurred(Instant.now().minusSeconds(7200)); // 2 hours ago
        charge.setAmount(new Money(0.0));

        parkingOffice.getCharges().add(charge);

        assertDoesNotThrow(() -> parkingOffice.exit(lot, car));
        assertFalse(lot.getParkedCars().contains(car));
        assertEquals(10.0, charge.getAmount().getDollars());
    }

    @Test
    void testUpdateDailyFees() {
        UUID lotId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        ParkingLot lot = createLot(lotId, 10, false, 15.0); // daily lot
        Car car = new Car("Permit2", LocalDate.now().plusDays(1), "PQR678", CarType.COMPACT, customerId);

        lot.getParkedCars().add(car);
        parkingOffice.getLots().add(lot);

        ParkingCharge charge = new ParkingCharge();
        charge.setLotId(lotId);
        charge.setPermitId(customerId);
        charge.setAmount(new Money(30.0));

        parkingOffice.getCharges().add(charge);

        parkingOffice.updateDailyFees();

        assertEquals(45.0, charge.getAmount().getDollars());
    }

    @Test
    void testCalculatePermitBillForCompactCar() {
        UUID ownerId = UUID.randomUUID();
        Car car = new Car("Permit3", LocalDate.now().plusDays(10), "STU901", CarType.COMPACT, ownerId);

        ParkingCharge c1 = new ParkingCharge();
        c1.setPermitId(ownerId);
        c1.setAmount(new Money(50.0));

        ParkingCharge c2 = new ParkingCharge();
        c2.setPermitId(ownerId);
        c2.setAmount(new Money(50.0));

        parkingOffice.getCharges().add(c1);
        parkingOffice.getCharges().add(c2);

        Double total = parkingOffice.calculatePermitBill(car);
        assertEquals(80.0, total);
    }

    @Test
    void testCalculateCustomerMonthlyBill() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setName("Greg");
        customer.setAddress(createTestAddress());

        Car car = new Car("Permit4", LocalDate.now().plusDays(5), "VWX234", CarType.SUV, customerId);
        parkingOffice.getCars().add(car);

        ParkingCharge charge = new ParkingCharge();
        charge.setPermitId(customerId);
        charge.setAmount(new Money(100.0));
        parkingOffice.getCharges().add(charge);

        boolean result = parkingOffice.calculateCustomerMonthlyBill(customer);

        assertTrue(result);
        assertTrue(parkingOffice.getCharges().isEmpty());
    }

    @Test
    void testGetCustomerById() {
        Customer customer = parkingOffice.register("Hannah", createTestAddress(), "555-0004");
        UUID id = customer.getCustomerId();

        Customer fetched = parkingOffice.getCustomer(id);
        assertNotNull(fetched);
        assertEquals("Hannah", fetched.getName());
    }

    @Test
    void testAddChargeToExistingCharge() {
        UUID lotId = UUID.randomUUID();
        ParkingLot lot = createLot(lotId, 3, false, 12.0);
        parkingOffice.getLots().add(lot);

        ParkingCharge charge = new ParkingCharge();
        charge.setLotId(lotId);
        charge.setAmount(new Money(36.0));

        Money updated = parkingOffice.addCharge(charge);
        assertEquals(48.0, updated.getDollars());
    }

    // Helper to create ParkingLot
    private ParkingLot createLot(UUID lotId, int capacity, boolean chargeOnExit, double fee) {
        ParkingLot lot = new ParkingLot();
        lot.setLotId(lotId);
        lot.setCapacity(capacity);
        lot.setChargeOnExit(chargeOnExit);
        lot.setLotFee(new Money(fee));
        lot.setAddress(createTestAddress());
        return lot;
    }
}
