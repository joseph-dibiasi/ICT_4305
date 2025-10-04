package classes;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ParkingFeeTest {

    @Test
    void testConstructorWithoutEntryTime() {
        UUID lotId = UUID.randomUUID();
        ParkingFee fee = new ParkingFee(5, true, 10.0, lotId);

        assertEquals(5, fee.getRate());
        assertTrue(fee.getDailyRate());
        assertEquals(10.0, fee.getLotFees(), 0.0001);
        assertEquals(lotId, fee.getLotId());
        assertNull(fee.getEntryTime());
        assertNull(fee.getTotalFee());
    }

    @Test
    void testConstructorWithEntryTime() {
        UUID lotId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        ParkingFee fee = new ParkingFee(3, false, 7.5, lotId, now);

        assertEquals(3, fee.getRate());
        assertFalse(fee.getDailyRate());
        assertEquals(7.5, fee.getLotFees(), 0.0001);
        assertEquals(lotId, fee.getLotId());
        assertEquals(now, fee.getEntryTime());
        assertNull(fee.getTotalFee());
    }

    @Test
    void testSettersAndGetters() {
        UUID lotId = UUID.randomUUID();
        LocalDateTime entryTime = LocalDateTime.now();
        ParkingFee fee = new ParkingFee(0, false, 0.0, lotId);

        fee.setRate(8);
        fee.setDailyRate(true);
        fee.setLotFees(12.34);
        fee.setLotId(lotId);
        fee.setEntryTime(entryTime);
        fee.setTotalFee(99.99);

        assertEquals(8, fee.getRate());
        assertTrue(fee.getDailyRate());
        assertEquals(12.34, fee.getLotFees(), 0.0001);
        assertEquals(lotId, fee.getLotId());
        assertEquals(entryTime, fee.getEntryTime());
        assertEquals(99.99, fee.getTotalFee(), 0.0001);
    }

    @Test
    void testTotalFeeCanBeSetAndRetrieved() {
        ParkingFee fee = new ParkingFee(1, true, 10.0, UUID.randomUUID());
        fee.setTotalFee(45.67);

        assertEquals(45.67, fee.getTotalFee(), 0.0001);
    }
}