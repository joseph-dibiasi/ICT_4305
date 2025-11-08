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

        RuntimeException ex = assertThrows(RuntimeException.class, () -> parkingOffice.entry(lot, car));
        assertTrue(ex.getMessage().contains("Permit expired"));
    }

    @Test
    void testEntryFailsIfLotIsFull() {
        Customer customer = parkingOffice.register("Eve", createTestAddress(), "555-0002");
        Car car = parkingOffice.register(customer, "GHI789", CarType.SUV);
        car.setPermit("Eve");
        car.setPermitExpiration(LocalDate.now().plusDays(10));

        ParkingLot lot = createLot(UUID.randomUUID(), 0, true, 10.0); // capacity = 0
        parkingOffice.getLots().add(lot);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> parkingOffice.entry(lot, car));
        assertTrue(ex.getMessage().contains("Parking Lot Full"));
    }
    
    @Test
    void testEntryFailsIfCarAlreadyInLot() {
    	Customer customer = parkingOffice.register("Eve", createTestAddress(), "555-0002");
    	Car car = parkingOffice.register(customer, "GHI789", CarType.SUV);
    	car.setPermit("Eve");
    	car.setPermitExpiration(LocalDate.now().plusDays(10));
    	
    	ParkingLot lot = createLot(UUID.randomUUID(), 2, true, 10.0); // capacity = 0
    	lot.getParkedCars().add(car);
    	parkingOffice.getLots().add(lot);
    	
    	RuntimeException ex = assertThrows(RuntimeException.class, () -> parkingOffice.entry(lot, car));
    	assertTrue(ex.getMessage().contains("Car already parked in the lot."));
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
    void testExitHourlyLotThrowsInvalid() {
    	UUID lotId = UUID.randomUUID();
    	UUID ownerId = UUID.randomUUID();
    	
    	Car car = new Car("Permit1", LocalDate.now().plusDays(10), "MNO345", CarType.SUV, ownerId);
    	
    	ParkingLot lot = createLot(lotId, 5, true, 5.0);
    	lot.getParkedCars().add(car);
    	
    	ParkingCharge charge = new ParkingCharge();
    	charge.setLotId(lotId);
    	charge.setPermitId(ownerId);
    	charge.setIncurred(Instant.now().plusSeconds(7200)); // 2 hours ahead
    	charge.setAmount(new Money(0.0));
        parkingOffice.getCharges().add(charge);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> parkingOffice.exit(lot, car));
        assertTrue(ex.getMessage().contains("Invalid parking time detected."));
    }

    @Test
    void testExitWhenNoChargeExistsThrowsAndRemovesCar() {
        UUID lotId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        Car car = new Car("PermitX", LocalDate.now().plusDays(5), "NOCHG1", CarType.SUV, ownerId);

        ParkingLot lot = createLot(lotId, 5, true, 2.0);
        lot.getParkedCars().add(car);
        // Intentionally DO NOT add a ParkingCharge to parkingOffice.getCharges()

        RuntimeException ex = assertThrows(RuntimeException.class, () -> parkingOffice.exit(lot, car));
        assertTrue(ex.getMessage().contains("Parking Charge Not found"));
        // finally block should remove car even when exception is thrown
        assertFalse(lot.getParkedCars().contains(car));
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
    void testCreateOrUpdateEntryParkingCharge_UpdatesExistingDaily() {
        ParkingLot daily = createLot(UUID.randomUUID(), 5, false, 3.0);
        parkingOffice.getLots().add(daily);

        UUID owner = UUID.randomUUID();
        Car car = new Car("DP", LocalDate.now().plusDays(3), "DUP", CarType.SUV, owner);

        ParkingCharge existing = new ParkingCharge();
        existing.setLotId(daily.getLotId());
        existing.setPermitId(owner);
        existing.setAmount(new Money(2.0));

        parkingOffice.getCharges().add(existing);

        parkingOffice.createOrUpdateEntryParkingCharge(daily, car);

        ParkingCharge found = parkingOffice.findParkingChargeByLotIdAndOwnerId(daily.getLotId(), owner);
        assertNotNull(found);
        // existing amount 2.0 + daily lot fee 3.0 => 5.0
        assertEquals(5.0, found.getAmount().getDollars(), 0.0001);
    }

    @Test
    void testCreateOrUpdateEntryParkingCharge_NewHourlyAndDaily() {
        ParkingLot hourly = createLot(UUID.randomUUID(), 10, true, 2.0);
        parkingOffice.getLots().add(hourly);

        ParkingLot daily = createLot(UUID.randomUUID(), 10, false, 5.0);
        parkingOffice.getLots().add(daily);

        Car car1 = new Car("PERM", LocalDate.now().plusDays(1), "H1", CarType.SUV, UUID.randomUUID());
        parkingOffice.createOrUpdateEntryParkingCharge(hourly, car1);
        ParkingCharge hc = parkingOffice.findParkingChargeByLotIdAndOwnerId(hourly.getLotId(), car1.getOwner());
        assertNotNull(hc);
        assertEquals(0.0, hc.getAmount().getDollars());

        Car car2 = new Car("PERM2", LocalDate.now().plusDays(1), "D1", CarType.SUV, UUID.randomUUID());
        parkingOffice.createOrUpdateEntryParkingCharge(daily, car2);
        ParkingCharge dc = parkingOffice.findParkingChargeByLotIdAndOwnerId(daily.getLotId(), car2.getOwner());
        assertNotNull(dc);
        assertEquals(5.0, dc.getAmount().getDollars());
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
    void testCalculatePermitBillForNonCompactCar() {
        UUID ownerId = UUID.randomUUID();
        Car car = new Car("PermitNC", LocalDate.now().plusDays(10), "NONC1", CarType.SUV, ownerId);

        ParkingCharge c1 = new ParkingCharge();
        c1.setPermitId(ownerId);
        c1.setAmount(new Money(30.0));

        parkingOffice.getCharges().add(c1);

        Double total = parkingOffice.calculatePermitBill(car);
        assertEquals(30.0, total);
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

    @Test
    void testAddChargeThrowsWhenLotMissing() {
        ParkingCharge pc = new ParkingCharge();
        pc.setLotId(UUID.randomUUID()); // not added to office.lots
        pc.setAmount(new Money(10.0));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> parkingOffice.addCharge(pc));
        assertTrue(ex.getMessage().contains("Failed to Process Parking Charge"));
    }

    @Test
    void testRemoveParkingChargesByOwnerIdReturnsFalseWhenNone() {
        UUID owner = UUID.randomUUID();
        boolean removed = parkingOffice.removeParkingChargesByOwnerId(owner);
        assertFalse(removed);
    }

    @Test
    void testFindParkingChargeAndRelatedFinders() {
        UUID owner = UUID.randomUUID();
        UUID lotId = UUID.randomUUID();

        // initially none
        assertNull(parkingOffice.findParkingChargeByLotIdAndOwnerId(lotId, owner));
        assertTrue(parkingOffice.findParkingChargesByOwnerId(owner).isEmpty());

        // add a charge
        ParkingCharge pc = new ParkingCharge();
        pc.setLotId(lotId);
        pc.setPermitId(owner);
        pc.setAmount(new Money(7.0));
        parkingOffice.getCharges().add(pc);

        assertNotNull(parkingOffice.findParkingChargeByLotIdAndOwnerId(lotId, owner));
        assertEquals(1, parkingOffice.findParkingChargesByOwnerId(owner).size());
    }

    @Test
    void testFindCarsByCustomerId() {
        UUID cid = UUID.randomUUID();
        Car c1 = new Car("P-A", LocalDate.now().plusDays(10), "CAR1", CarType.SUV, cid);
        Car c2 = new Car("P-B", LocalDate.now().plusDays(10), "CAR2", CarType.COMPACT, cid);
        parkingOffice.getCars().add(c1);
        parkingOffice.getCars().add(c2);

        List<Car> found = parkingOffice.findCarsByCustomerId(cid);
        assertEquals(2, found.size());
        assertTrue(found.contains(c1));
        assertTrue(found.contains(c2));
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
    
    @Test
    void testGetCustomerIds() {
        Customer c1 = parkingOffice.register("AliceT", createTestAddress(), "111-1111");
        Customer c2 = parkingOffice.register("BobT", createTestAddress(), "222-2222");

        Collection<UUID> ids = parkingOffice.getCustomerIds();

        assertEquals(2, ids.size());
        assertTrue(ids.contains(c1.getCustomerId()));
        assertTrue(ids.contains(c2.getCustomerId()));
    }

    @Test
    void testGetPermitIdsDistinctAcrossCars() {
        UUID owner1 = UUID.randomUUID();
        UUID owner2 = UUID.randomUUID();

        Car c1 = new Car("P1", LocalDate.now().plusDays(5), "L1", CarType.SUV, owner1);
        Car c2 = new Car("P2", LocalDate.now().plusDays(5), "L2", CarType.SUV, owner1);
        Car c3 = new Car("P3", LocalDate.now().plusDays(5), "L3", CarType.COMPACT, owner2);

        parkingOffice.getCars().add(c1);
        parkingOffice.getCars().add(c2);
        parkingOffice.getCars().add(c3);

        Collection<UUID> permits = parkingOffice.getPermitIds();

        assertEquals(2, permits.size());
        assertTrue(permits.contains(owner1));
        assertTrue(permits.contains(owner2));
    }

    @Test
    void testGetPermitIdsForCustomer() {
        Customer cust = parkingOffice.register("CarolT", createTestAddress(), "333-3333");
        // register two cars for same customer
        parkingOffice.register(cust, "C1", CarType.SUV);
        parkingOffice.register(cust, "C2", CarType.COMPACT);
        // another customer/car
        Customer other = parkingOffice.register("DaveT", createTestAddress(), "444-4444");
        parkingOffice.register(other, "O1", CarType.SUV);

        Collection<UUID> custPermits = parkingOffice.getPermitIds(cust);

        // Implementation returns distinct owner ids derived from cars' owner field,
        // so for a customer with one or more cars this should contain the customer's id once.
        assertEquals(1, custPermits.size());
        assertTrue(custPermits.contains(cust.getCustomerId()));
    }

    @Test
    void testGetPermitIdsForNullCustomerReturnsEmpty() {
        Collection<UUID> result = parkingOffice.getPermitIds((Customer) null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
