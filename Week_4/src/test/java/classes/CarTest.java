package classes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarTest {

    private Car car;
    private UUID ownerId;
    private UUID lotId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        lotId = UUID.randomUUID();
        car = new Car("PERMIT-001", LocalDate.now().plusMonths(6), "TEST-123", CarType.SUV, ownerId);
    }

    @Test
    void testUpdateParkingFees_NewFee() {
        ParkingFee fee = new ParkingFee(10, true, 1.0, lotId);
        fee.setTotalFee(10.0);

        car.updateParkingFees(fee, true);

        assertTrue(car.getParkingFees().containsKey(lotId));
        assertEquals(10.0, car.getParkingFees().get(lotId).getTotalFee());
    }

    @Test
    void testUpdateParkingFees_UpdateExistingFee_DailyRateTrue() {
        ParkingFee existingFee = new ParkingFee(3, true, 15.0, lotId);
        existingFee.setTotalFee(45.0);
        car.getParkingFees().put(lotId, existingFee);

        ParkingFee newFee = new ParkingFee(2, true, 15.0, lotId);

        car.updateParkingFees(newFee, true);

        ParkingFee updatedFee = car.getParkingFees().get(lotId);
        // rate should be sum of old and new
        assertEquals(5, updatedFee.getRate());

        // totalFee = rate * lotFees (5 * 15.0)
        assertEquals(75.0, updatedFee.getTotalFee(), 0.0001);
    }


    @Test
    void testUpdateParkingFees_UpdateExisting_DailyRateFalse_NoEntryTime() {
        ParkingFee initialFee = new ParkingFee(5, false, 2.0, lotId);
        initialFee.setTotalFee(0.0); // Will be updated
        car.setParkingFees(new HashMap<UUID, ParkingFee>() {{
            put(lotId, initialFee);
        }});

        ParkingFee newFee = new ParkingFee(5, false, 2.0, lotId); // Not used directly
        newFee.setTotalFee(0.0);

        car.updateParkingFees(newFee, false);

        ParkingFee updatedFee = car.getParkingFees().get(lotId);
        assertEquals(10.0, updatedFee.getTotalFee()); // 5 * 2.0
    }

    @Test
    void testCalculatePermitBill_SingleFee_SUV() {
        ParkingFee fee = new ParkingFee(10, true, 1.0, lotId);
        fee.setTotalFee(100.0);

        car.setParkingFees(new HashMap<UUID, ParkingFee>() {{
            put(lotId, fee);
        }});

        double total = car.calculatePermitBill();
        assertEquals(100.0, total);
    }

    @Test
    void testCalculatePermitBill_SingleFee_Compact_DiscountApplied() {
        car.setType(CarType.COMPACT);

        ParkingFee fee = new ParkingFee(10, true, 1.0, lotId);
        fee.setTotalFee(100.0);

        car.setParkingFees(new HashMap<UUID, ParkingFee>() {{
            put(lotId, fee);
        }});

        double total = car.calculatePermitBill();
        assertEquals(80.0, total); // 20% discount
    }

    @Test
    void testCalculatePermitBill_MultipleFees_Mixed() {
        ParkingFee fee1 = new ParkingFee(10, true, 1.0, UUID.randomUUID());
        fee1.setTotalFee(50.0);
        ParkingFee fee2 = new ParkingFee(10, true, 1.0, UUID.randomUUID());
        fee2.setTotalFee(30.0);

        car.setParkingFees(new HashMap<UUID, ParkingFee>() {{
            put(fee1.getLotId(), fee1);
            put(fee2.getLotId(), fee2);
        }});

        assertEquals(80.0, car.calculatePermitBill());
    }

    @Test
    void testCalculatePermitBill_NoFees() {
        car.setParkingFees(new HashMap<>());
        assertEquals(0.0, car.calculatePermitBill());
    }

    @Test
    void testGettersAndSetters() {
        car.setLicense("NEW-LICENSE");
        car.setPermit("NEW-PERMIT");
        car.setPermitExpiration(LocalDate.of(2026, 1, 1));
        car.setType(CarType.COMPACT);
        UUID newOwner = UUID.randomUUID();
        car.setOwner(newOwner);

        assertEquals("NEW-LICENSE", car.getLicense());
        assertEquals("NEW-PERMIT", car.getPermit());
        assertEquals(LocalDate.of(2026, 1, 1), car.getPermitExpiration());
        assertEquals(CarType.COMPACT, car.getType());
        assertEquals(newOwner, car.getOwner());
    }

    @Test
    void testToStringContainsKeyInfo() {
        String str = car.toString();
        assertTrue(str.contains(car.getLicense()));
        assertTrue(str.contains(car.getPermit()));
        assertTrue(str.contains(car.getPermitExpiration().toString()));
        assertTrue(str.contains(car.getType().toString()));
    }
}