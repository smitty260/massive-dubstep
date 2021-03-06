/**
 * 
 * This file is part of the CarParkSimulator Project, written as 
 * part of the assessment for INB370, semester 1, 2014. 
 *
 * CarParkSimulator
 * asgn2Tests 
 * 22/04/2014
 * 
 */
package asgn2Tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import asgn2Exceptions.VehicleException;
import asgn2Vehicles.Car;

/**
 * @author Jeremy Smith (n8642087)
 *
 */
public class CarTests {

	private Car newCar;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link asgn2Vehicles.Car#toString()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testToString() throws VehicleException {
		newCar = new Car("C1", 3, true);
		newCar.enterQueuedState();
		newCar.exitQueuedState(22);
		newCar.enterParkedState(22, 78);
		newCar.exitParkedState(100);
		assertEquals("Vehicle vehID: C1\n"
				+ "Arrival Time: 3\n"
				+ "Exit from Queue: 22\n"
				+ "Queuing Time: 19\n"
				+ "Entry to Car Park: 22\n"
				+ "Exit from Car Park: 100\n"
				+ "Parking Time: 78\n"
				+ "Customer was satisfied\n"
				+ "Car can use small car parking space\n", newCar.toString());
	}

	/**
	 * Test method for {@link asgn2Vehicles.Car#Car(java.lang.String, int, boolean)}.
	 * @throws VehicleException 
	 */
	@Test
	public void testCar() throws VehicleException {
		newCar = new Car("C4", 3, false);
	}

	/**
	 * Test method for {@link asgn2Vehicles.Car#isSmall()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testIsSmall() throws VehicleException {
		newCar = new Car("C6", 4, true);
		assertEquals(true, newCar.isSmall());
	}
	
	@Test
	public void testIsNotSmall() throws VehicleException {
		newCar = new Car("C12", 3, false);
		assertEquals(false, newCar.isSmall());
	}
	
	@Test (expected = VehicleException.class)
	public void testNegativeArrivalTime() throws VehicleException {
		newCar = new Car("C1", -11, true);
	}
	
	@Test (expected = VehicleException.class)
	public void testBoundaryArrivalTime() throws VehicleException {
		newCar = new Car("C6", 0, false);
	}

}
