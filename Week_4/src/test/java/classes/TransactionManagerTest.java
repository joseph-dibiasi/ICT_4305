// File: ICT_4305/Week_4/src/test/java/classes/TransactionManagerTest.java
package classes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TransactionManagerTest {

    private TransactionManager tm;

    @BeforeEach
    void setUp() throws Exception {
        tm = new TransactionManager();
        // initialize private 'charges' list via reflection to avoid NPEs in production code
        setPrivateField(tm, "charges", new ArrayList<ParkingCharge>());
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void testParkThrowsWhenNoPermit() {
        ParkingLot lot = new ParkingLot();
        lot.setLotId(UUID.randomUUID());
        lot.setCapacity(5);
        lot.setParkedCars(new HashSet<>());
        lot.setChargeOnExit(true);
        lot.setLotFee(new Money(100L));

        Car car = new Car();
        car.setLicense("L1");
        car.setOwner(UUID.randomUUID());
        // permit is null -> should throw
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            tm.park(LocalDateTime.now(), lot, car)
        );
        assertTrue(ex.getMessage().contains("Permit required"));
    }

    @Test
    void testParkThrowsWhenPermitExpired() {
        ParkingLot lot = new ParkingLot();
        lot.setLotId(UUID.randomUUID());
        lot.setCapacity(5);
        lot.setParkedCars(new HashSet<>());
        lot.setChargeOnExit(true);
        lot.setLotFee(new Money(100L));

        Car car = new Car();
        car.setPermit("Name");
        car.setPermitExpiration(LocalDateTime.now().toLocalDate().minusDays(1)); // expired
        car.setLicense("L2");
        car.setOwner(UUID.randomUUID());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                tm.park(LocalDateTime.now(), lot, car)
        );
        assertTrue(ex.getMessage().contains("Permit expired"));
    }

    @Test
    void testParkCreatesParkingCharge_hourlyAndDaily() throws Exception {
        // hourly lot
        ParkingLot hourly = new ParkingLot();
        hourly.setLotId(UUID.randomUUID());
        hourly.setCapacity(5);
        hourly.setParkedCars(new HashSet<>());
        hourly.setChargeOnExit(true);
        hourly.setLotFee(new Money(150L)); // $1.50/hour

        Car carH = new Car();
        carH.setLicense("H1");
        carH.setOwner(UUID.randomUUID());
        carH.setPermit("Permit");
        carH.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(10));

        ParkingCharge chargeH = tm.park(LocalDateTime.now(), hourly, carH);
        assertNotNull(chargeH);
        assertEquals(hourly.getLotId(), chargeH.getLotId());
        assertEquals(carH.getOwner(), chargeH.getPermitId());
        // hourly lots start with zero dollars
        assertEquals(0L, chargeH.getAmount().getCents());

        // daily lot
        ParkingLot daily = new ParkingLot();
        daily.setLotId(UUID.randomUUID());
        daily.setCapacity(5);
        daily.setParkedCars(new HashSet<>());
        daily.setChargeOnExit(false);
        daily.setLotFee(new Money(200L)); // $2.00/day

        Car carD = new Car();
        carD.setLicense("D1");
        carD.setOwner(UUID.randomUUID());
        carD.setPermit("P");
        carD.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(10));

        ParkingCharge chargeD = tm.park(LocalDateTime.now(), daily, carD);
        assertNotNull(chargeD);
        assertEquals(daily.getLotId(), chargeD.getLotId());
        assertEquals(carD.getOwner(), chargeD.getPermitId());
        // daily lots apply initial lot fee
        assertEquals(200L, chargeD.getAmount().getCents());
    }

    @Test
    void testLeaveHourlyCalculatesAmountAndRemovesCar() throws Exception {
        // prepare lot and manager charges
        ParkingLot lot = new ParkingLot();
        lot.setLotId(UUID.randomUUID());
        lot.setCapacity(5);
        Set<Car> parked = new HashSet<>();
        lot.setParkedCars(parked);
        lot.setChargeOnExit(true);
        lot.setLotFee(new Money(100L)); // $1/hour

        Car car = new Car();
        car.setLicense("L3");
        car.setOwner(UUID.randomUUID());
        car.setPermit("OK");
        car.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(5));
        lot.getParkedCars().add(car);

        // create an existing charge (entry) and add it to manager's charges
        ParkingCharge existing = new ParkingCharge();
        existing.setLotId(lot.getLotId());
        existing.setPermitId(car.getOwner());
        existing.setIncurred(Instant.now().minus(2, ChronoUnit.HOURS)); // 2 hours ago
        existing.setAmount(new Money(0L)); // starting at $0
        // add to private charges list
        @SuppressWarnings("unchecked")
        List<ParkingCharge> charges = (List<ParkingCharge>) getPrivateField(tm, "charges");
        charges.add(existing);

        Instant exit = Instant.now();
        ParkingCharge result = tm.leave(exit, lot, car);

        // amount should be updated: 2 hours * $1.0 = $2.0
        assertEquals(200L, result.getAmount().getCents());
        assertNull(result.getIncurred());
        assertFalse(lot.getParkedCars().contains(car));
    }

    @Test
    void testLeaveHourlyWhenChargeNotFoundThrows() {
        ParkingLot lot = new ParkingLot();
        lot.setLotId(UUID.randomUUID());
        lot.setCapacity(5);
        lot.setParkedCars(new HashSet<>());
        lot.setChargeOnExit(true);
        lot.setLotFee(new Money(100L));

        Car car = new Car();
        car.setLicense("L4");
        car.setOwner(UUID.randomUUID());
        car.setPermit("P");
        car.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(2));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                tm.leave(Instant.now(), lot, car)
        );
        assertTrue(ex.getMessage().contains("Parking Charge Not found"));
    }

    @Test
    void testAddChargeAndAddChargeThrowsWhenNullAmount() {
        // normal add
        ParkingCharge charge = new ParkingCharge();
        charge.setAmount(new Money(100L)); // $1
        Money lotFee = new Money(200L); // $2
        Money updated = tm.addCharge(charge, lotFee);
        assertEquals(300L, updated.getCents());

        // error path: parkingCharge.getAmount() null
        ParkingCharge bad = new ParkingCharge();
        bad.setAmount(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tm.addCharge(bad, lotFee));
        assertTrue(ex.getMessage().contains("Failed to Process Parking Charge"));
    }

    @Test
    void testCalculatePermitBillAndCustomerMonthlyBillAndRemoveCharges() throws Exception {
        UUID ownerId = UUID.randomUUID();

        // create charges for owner: $1.00 and $2.00
        ParkingCharge c1 = new ParkingCharge();
        c1.setPermitId(ownerId);
        c1.setAmount(new Money(100L));

        ParkingCharge c2 = new ParkingCharge();
        c2.setPermitId(ownerId);
        c2.setAmount(new Money(200L));

        @SuppressWarnings("unchecked")
        List<ParkingCharge> charges = (List<ParkingCharge>) getPrivateField(tm, "charges");
        charges.clear();
        charges.add(c1);
        charges.add(c2);

        // non-compact car => total $3.00
        Car car = new Car();
        car.setOwner(ownerId);
        car.setType(CarType.SUV);
        Double total = tm.calculatePermitBill(car);
        assertEquals(3.0, total, 0.0001);

        // compact car => 20% discount => $2.40
        car.setType(CarType.COMPACT);
        Double discounted = tm.calculatePermitBill(car);
        assertEquals(2.4, discounted, 0.0001);

        // calculateCustomerMonthlyBill: prepare customer with cars
        Customer cust = new Customer();
        cust.setCustomerId(ownerId);
        cust.setName("Cust");
        Address a = new Address();
        a.setStreetAddress1("Addr");
        cust.setAddress(a);
        List<Car> cars = new ArrayList<>();
        cars.add(car);
        cust.setCars(cars);

        // ensure charges list has entries -> method should return true and remove matching charges
        boolean res = tm.calculateCustomerMonthlyBill(cust);
        assertTrue(res);
        // after successful billing, charges for owner should be removed
        List<ParkingCharge> remaining = tm.findParkingChargesByOwnerId(ownerId);
        assertTrue(remaining.isEmpty());
    }

    @Test
    void testCalculateCustomerMonthlyBillThrowsOnNullCars() {
        Customer cust = new Customer();
        cust.setCustomerId(UUID.randomUUID());
        cust.setName("X");
        cust.setAddress(new Address());
        cust.setCars(null); // will cause NPE inside method and be wrapped

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                tm.calculateCustomerMonthlyBill(cust)
        );
        assertTrue(ex.getMessage().contains("Failed to Process Customer Monthly Bill"));
    }

    @Test
    void testFindAndRemoveParkingChargesByOwnerId() throws Exception {
        UUID a = UUID.randomUUID();
        ParkingCharge p = new ParkingCharge();
        p.setPermitId(a);
        p.setAmount(new Money(50L));

        @SuppressWarnings("unchecked")
        List<ParkingCharge> charges = (List<ParkingCharge>) getPrivateField(tm, "charges");
        charges.clear();
        charges.add(p);

        List<ParkingCharge> found = tm.findParkingChargesByOwnerId(a);
        assertEquals(1, found.size());

        boolean removed = tm.removeParkingChargesByOwnerId(a);
        assertTrue(removed);
        assertTrue(((List<?>) getPrivateField(tm, "charges")).isEmpty());
    }

    private static Object getPrivateField(Object target, String fieldName) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(target);
    }
}
