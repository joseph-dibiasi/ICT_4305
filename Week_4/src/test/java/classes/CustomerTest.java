package classes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setup() {
        customer = new Customer();
        customer.setCustomerId(UUID.randomUUID());
        customer.setName("John Doe");
        customer.setCars(new java.util.HashMap<>());
    }

    @Test
    void testRegisterAddsCar() {
        String license = "ABC-123";
        CarType type = CarType.SUV;

        Car registeredCar = customer.register(license, type);

        assertNotNull(registeredCar);
        assertEquals(license, registeredCar.getLicense());
        assertEquals(type, registeredCar.getType());
        assertEquals(customer.getCustomerId(), registeredCar.getOwner());
        assertTrue(customer.getCars().containsKey(license));
        assertEquals(registeredCar, customer.getCars().get(license));
        assertEquals(customer.getName(), registeredCar.getPermit());  // permit = customer name in constructor
        assertTrue(registeredCar.getPermitExpiration().isAfter(LocalDate.now()));
    }

    @Test
    void testCalculateCustomerMonthlyBill_SumsAllCarBills() {
        // Add two cars with mocked parking fees
        Car car1 = new Car("Permit1", LocalDate.now().plusDays(30), "L1", CarType.SUV, customer.getCustomerId());
        Car car2 = new Car("Permit2", LocalDate.now().plusDays(30), "L2", CarType.COMPACT, customer.getCustomerId());

        // Setup parking fees for car1
        ParkingFee fee1 = new ParkingFee(10, true, 5.0, UUID.randomUUID());
        fee1.setTotalFee(50.0);
        car1.getParkingFees().put(fee1.getLotId(), fee1);

        // Setup parking fees for car2
        ParkingFee fee2 = new ParkingFee(5, true, 5.0, UUID.randomUUID());
        fee2.setTotalFee(20.0);
        car2.getParkingFees().put(fee2.getLotId(), fee2);

        customer.getCars().put(car1.getLicense(), car1);
        customer.getCars().put(car2.getLicense(), car2);

        double expectedTotal = car1.calculatePermitBill() + car2.calculatePermitBill();
        double actualTotal = customer.calculateCustomerMonthlyBill();

        assertEquals(expectedTotal, actualTotal);
    }

    @Test
    void testGettersAndSetters() {
        UUID id = UUID.randomUUID();
        Address address = new Address();
        String phone = "123-456-7890";

        customer.setCustomerId(id);
        customer.setAddress(address);
        customer.setPhoneNumber(phone);
        customer.setName("Jane");

        assertEquals(id, customer.getCustomerId());
        assertEquals(address, customer.getAddress());
        assertEquals(phone, customer.getPhoneNumber());
        assertEquals("Jane", customer.getName());
    }

    @Test
    void testGetCarsReturnsEmptyMapIfNull() {
        customer.setCars(null);
        assertNotNull(customer.getCars());
        assertTrue(customer.getCars().isEmpty());
    }

    @Test
    void testToStringContainsExpectedData() {
        customer.setCustomerId(UUID.randomUUID());
        customer.setName("Alice");
        customer.setAddress(new Address());
        customer.setPhoneNumber("555-555-5555");

        String str = customer.toString();

        assertTrue(str.contains("Customer"));
        assertTrue(str.contains("Alice"));
        assertTrue(str.contains("555-555-5555"));
    }
}