package classes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address();
    }

    @Test
    void testStreetAddress1GetterAndSetter() {
        address.setStreetAddress1("123 Main St");
        assertEquals("123 Main St", address.getStreetAddress1());
    }

    @Test
    void testStreetAddress2GetterAndSetter() {
        address.setStreetAddress2("Apt 4B");
        assertEquals("Apt 4B", address.getStreetAddress2());
    }

    @Test
    void testCityGetterAndSetter() {
        address.setCity("Springfield");
        assertEquals("Springfield", address.getCity());
    }

    @Test
    void testStateGetterAndSetter() {
        address.setState("IL");
        assertEquals("IL", address.getState());
    }

    @Test
    void testZipCodeGetterAndSetter() {
        address.setZipCode("62704");
        assertEquals("62704", address.getZipCode());
    }

    @Test
    void testGetAddressInfo_AllFieldsSet() {
        address.setStreetAddress1("123 Main St");
        address.setStreetAddress2("Apt 4B");
        address.setCity("Springfield");
        address.setState("IL");
        address.setZipCode("62704");

        String expected = "123 Main St Apt 4B, Springfield, IL 62704";
        assertEquals(expected, address.getAddressInfo());
    }

    @Test
    void testGetAddressInfo_EmptyFields() {
        String expected = "null null, null, null null";
        assertEquals(expected, address.getAddressInfo());
    }

    @Test
    void testGetAddressInfo_PartialFieldsSet() {
        address.setStreetAddress1("456 Elm St");
        address.setCity("Chicago");
        address.setZipCode("60601");

        // StreetAddress2, State are null
        String expected = "456 Elm St null, Chicago, null 60601";
        assertEquals(expected, address.getAddressInfo());
    }
}