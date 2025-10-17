package classes;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void testSetAndGetCustomerId() {
        Customer customer = new Customer();
        UUID id = UUID.randomUUID();
        customer.setCustomerId(id);
        assertEquals(id, customer.getCustomerId());
    }

    @Test
    void testSetAndGetName() {
        Customer customer = new Customer();
        customer.setName("Alice");
        assertEquals("Alice", customer.getName());
    }

    @Test
    void testSetAndGetAddress() {
        Customer customer = new Customer();
        Address address = new Address(); // Assuming Address is a basic POJO
        customer.setAddress(address);
        assertEquals(address, customer.getAddress());
    }

    @Test
    void testSetAndGetPhoneNumber() {
        Customer customer = new Customer();
        customer.setPhoneNumber("123-456-7890");
        assertEquals("123-456-7890", customer.getPhoneNumber());
    }

    @Test
    void testSetAndGetCars() {
        Customer customer = new Customer();
        HashMap<String, Car> cars = new HashMap<>();
        Car car = new Car();
        car.setLicense("XYZ-123");
        cars.put("XYZ-123", car);

        customer.setCars(cars);
        assertEquals(cars, customer.getCars());
        assertEquals(car, customer.getCars().get("XYZ-123"));
    }

    @Test
    void testGetCarsInitializesIfNull() {
        Customer customer = new Customer();
        // Initially cars is null
        assertNotNull(customer.getCars());
        assertTrue(customer.getCars().isEmpty());
    }

    @Test
    void testRegisterAddsCarWithCorrectFields() {
        Customer customer = new Customer();
        customer.setName("Bob");
        UUID customerId = UUID.randomUUID();
        customer.setCustomerId(customerId);

        Car registeredCar = customer.register("ABC-999", CarType.SUV);

        assertNotNull(registeredCar);
        assertEquals("Bob", registeredCar.getPermit());
        assertEquals("ABC-999", registeredCar.getLicense());
        assertEquals(CarType.SUV, registeredCar.getType());
        assertEquals(customerId, registeredCar.getOwner());

        // Check expiration is one year from today
        assertEquals(LocalDate.now().plusYears(1), registeredCar.getPermitExpiration());

        // Confirm it's added to the customer's car list
        assertEquals(registeredCar, customer.getCars().get("ABC-999"));
    }

    @Test
    void testToString() {
        Customer customer = new Customer();
        customer.setCustomerId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));
        customer.setName("Jane Doe");

        Address address = new Address();
        address.setStreetAddress1("789 Oak Ave");
        address.setStreetAddress2("");
        address.setCity("Gotham");
        address.setState("NJ");
        address.setZipCode("07097");

        customer.setAddress(address);
        customer.setPhoneNumber("999-999-9999");

        String toStringResult = customer.toString();

        assertTrue(toStringResult.contains("Jane Doe"));
        assertTrue(toStringResult.contains("Gotham")); // indirect check for address inclusion
        assertTrue(toStringResult.contains("999-999-9999"));
    }
    
}
