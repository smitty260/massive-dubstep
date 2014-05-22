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
		newCar = new Car("testcar", 10, false);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link asgn2Vehicles.Car#toString()}.
	 */
	@Test
	public void testToString() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link asgn2Vehicles.Car#Car(java.lang.String, int, boolean)}.
	 * @throws VehicleException 
	 */
	@Test
	public void testCar() throws VehicleException {
		newCar = new Car("test", 3, false);
	}

	/**
	 * Test method for {@link asgn2Vehicles.Car#isSmall()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testIsSmall() throws VehicleException {
		newCar = new Car("hello", 4, true);
		assertEquals(true, newCar.isSmall());
	}

}
