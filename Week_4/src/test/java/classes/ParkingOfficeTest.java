// File: ICT_4305/Week_4/src/test/java/classes/ParkingOfficeTest.java
package classes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ParkingOfficeTest {

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
    void testConstructorAndGetters() {
        assertEquals("University Parking", parkingOffice.getName());
        assertNotNull(parkingOffice.getAddress());
        assertEquals("Springfield", parkingOffice.getAddress().getCity());
        assertNotNull(parkingOffice.getCustomers());
        assertNotNull(parkingOffice.getLots());
        assertTrue(parkingOffice.getCustomers().isEmpty());
        assertTrue(parkingOffice.getLots().isEmpty());
    }

    @Test
    void testSetNameAndAddress() {
        parkingOffice.setName("New Name");
        Address a = new Address();
        a.setCity("X");
        parkingOffice.setAddress(a);
        assertEquals("New Name", parkingOffice.getName());
        assertEquals("X", parkingOffice.getAddress().getCity());
    }

    @Test
    void testGetCustomerIdsWhenEmpty() {
        List<UUID> ids = parkingOffice.getCustomerIds();
        assertNotNull(ids);
        assertTrue(ids.isEmpty());
    }

    @Test
    void testGetPermitIdsWhenNoCars() {
        List<UUID> permits = parkingOffice.getPermitIds();
        assertNotNull(permits);
        assertTrue(permits.isEmpty());
    }

    @Test
    void testGetPermitIdsForNullCustomerReturnsEmpty() {
        assertNotNull(parkingOffice.getPermitIds((Customer) null));
        assertTrue(parkingOffice.getPermitIds((Customer) null).isEmpty());
    }

    @Test
    void testGetCustomerWhenNotFound() {
        UUID randomId = UUID.randomUUID();
        assertNull(parkingOffice.getCustomer(randomId));
    }

    @Test
    void testRegisterCustomerDelegatesButDoesNotAddToList() {
        Customer c = new Customer();
        c.setName("Alice");
        c.setAddress(createTestAddress());
        c.setPhoneNumber("555-1234");

        // Current production implementation calls permitManager.register(...) but does not add to the office's customers list.
        parkingOffice.register(c);
        assertTrue(parkingOffice.getCustomers().isEmpty());
    }

    @Test
    void testSetAndGetCustomersAndLotsCollections() {
        // ensure setters work
        parkingOffice.setCustomers(List.of());
        parkingOffice.setLots(List.of());
        assertNotNull(parkingOffice.getCustomers());
        assertNotNull(parkingOffice.getLots());
    }
}
