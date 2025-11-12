package classes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * This is the class that manages all the parking transactions.
 */
public class TransactionManager {
    private List<ParkingCharge> charges;

    private HashMap<String, List<ParkingCharge>> chargesPerCar;
    
    /**
     * This method will create a parking transaction and will add it to the transactions list.
     */
    public ParkingCharge park(Calendar date, ParkingPermit permit, ParkingLot lot) {
    	
    }

    public Money getParkingCharges(ParkingPermit permit) {
    	
    }

    public Money getParkingCharges(String licensePlate) {
    	
    }
}
