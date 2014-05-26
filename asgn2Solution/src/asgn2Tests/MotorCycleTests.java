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
import asgn2Vehicles.MotorCycle;
import asgn2Vehicles.Vehicle;

/**
 * @author Jeremy Smith(n8642087)
 *
 */
public class MotorCycleTests {
	
	MotorCycle mc;
	Vehicle vehicle;
	
	private String GENERIC_VEHID = "MC1";
	private int GENERIC_ARRIVALTIME = 10;

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
	 * Test method for {@link asgn2Vehicles.MotorCycle#MotorCycle(java.lang.String, int)}.
	 * @throws VehicleException 
	 */
	@Test
	public void testMotorCycle() throws VehicleException {
		mc = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#Vehicle(java.lang.String, int)}.
	 * @throws VehicleException 
	 */
	@Test
	public void testVehicle() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#getVehID()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testGetVehID() throws VehicleException {
		vehicle = new MotorCycle("MC12", GENERIC_ARRIVALTIME);
		assertEquals("MC12", vehicle.getVehID());
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#getArrivalTime()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testGetArrivalTime() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, 52);
		assertEquals(52, vehicle.getArrivalTime());
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#enterQueuedState()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testEnterQueuedState() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		assertEquals(true, vehicle.isQueued());
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#exitQueuedState(int)}.
	 * @throws VehicleException 
	 */
	@Test
	public void testExitQueuedState() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		vehicle.exitQueuedState(52);
		assertEquals(false, vehicle.isQueued());
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#enterParkedState(int, int)}.
	 * @throws VehicleException 
	 */
	@Test
	public void testEnterParkedState() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		vehicle.exitQueuedState(12);
		vehicle.enterParkedState(12, 42);
		assertEquals(true, vehicle.isParked());
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#exitParkedState(int)}.
	 * @throws VehicleException 
	 */
	@Test (expected = VehicleException.class)
	public void testExitParkedStateInt() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		vehicle.exitQueuedState(12);
		vehicle.enterParkedState(12, 42);
		vehicle.exitParkedState(8);
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#exitParkedState()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testExitParkedState() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		vehicle.exitQueuedState(12);
		vehicle.enterParkedState(12, 42);
		vehicle.exitParkedState(54);
		assertEquals(false, vehicle.isParked());
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#isParked()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testIsParked() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		vehicle.exitQueuedState(12);
		vehicle.enterParkedState(12, 42);
		assertEquals(true, vehicle.isParked());
	}
	
	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#isParked()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testIsNotParked() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		assertEquals(false, vehicle.isParked());
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#isQueued()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testIsQueued() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		assertEquals(true, vehicle.isQueued());
	}
	
	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#isQueued()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testIsNotQueued() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		vehicle.exitQueuedState(12);
		vehicle.enterParkedState(12, 42);
		assertEquals(false, vehicle.isQueued());
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#getParkingTime()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testGetParkingTime() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		vehicle.exitQueuedState(12);
		vehicle.enterParkedState(12, 42);
		assertEquals(12, vehicle.getParkingTime());
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#getDepartureTime()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testGetDepartureTime() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		vehicle.exitQueuedState(12);
		vehicle.enterParkedState(12, 42);
		vehicle.exitParkedState(104);
		assertEquals(104, vehicle.getDepartureTime());
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#wasQueued()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testWasQueued() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		vehicle.exitQueuedState(12);
		vehicle.enterParkedState(12, 42);
		assertEquals(true, vehicle.wasQueued());
	}
	
	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#wasQueued()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testWasNotQueued() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		assertEquals(false, vehicle.wasQueued());
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#wasParked()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testWasParked() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		vehicle.exitQueuedState(12);
		vehicle.enterParkedState(12, 42);
		vehicle.exitParkedState(104);
		assertEquals(true, vehicle.wasParked());
	}
	
	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#wasParked()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testWasNotParked() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		vehicle.exitQueuedState(12);
		assertEquals(false, vehicle.wasParked());
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#isSatisfied()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testIsSatisfied() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		vehicle.exitQueuedState(12);
		vehicle.enterParkedState(12, 42);
		vehicle.exitParkedState(104);
		assertEquals(true, vehicle.isSatisfied());
	}
	
	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#isSatisfied()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testIsNotSatisfied() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		vehicle.exitQueuedState(12);
		assertEquals(false, vehicle.isSatisfied());
	}

	/**
	 * Test method for {@link asgn2Vehicles.Vehicle#toString()}.
	 * @throws VehicleException 
	 */
	@Test
	public void testToString() throws VehicleException {
		vehicle = new MotorCycle(GENERIC_VEHID, GENERIC_ARRIVALTIME);
		vehicle.enterQueuedState();
		vehicle.exitQueuedState(12);
		vehicle.enterParkedState(12, 42);
		vehicle.exitParkedState(104);
		assertEquals("Vehicle vehID: MC1\n"
				+ "Arrival Time: 10\n"
				+ "Exit from Queue: 12\n"
				+ "Queuing Time: 2\n"
				+ "Entry to Car Park: 12\n"
				+ "Exit from Car Park: 104\n"
				+ "Parking Time: 92\n"
				+ "Customer was satisfied\n",
				vehicle.toString());
	}

}
