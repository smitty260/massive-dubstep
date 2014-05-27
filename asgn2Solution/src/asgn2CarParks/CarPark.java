/**
 * 
 * This file is part of the CarParkSimulator Project, written as 
 * part of the assessment for INB370, semester 1, 2014. 
 *
 * CarParkSimulator
 * asgn2CarParks 
 * 21/04/2014
 * 
 */
package asgn2CarParks;

import java.util.ArrayList;
import java.util.Iterator;

import asgn2Exceptions.SimulationException;
import asgn2Exceptions.VehicleException;
import asgn2Simulators.Constants;
import asgn2Simulators.Simulator;
import asgn2Vehicles.Car;
import asgn2Vehicles.MotorCycle;
import asgn2Vehicles.Vehicle;

/**
 * The CarPark class provides a range of facilities for working with a car park in support 
 * of the simulator. In particular, it maintains a collection of currently parked vehicles, 
 * a queue of vehicles wishing to enter the car park, and an historical list of vehicles which 
 * have left or were never able to gain entry. 
 * 
 * The class maintains a wide variety of constraints on small cars, normal cars and motorcycles 
 * and their access to the car park. See the method javadoc for details. 
 * 
 * The class relies heavily on the asgn2.Vehicle hierarchy, and provides a series of reports 
 * used by the logger. 
 * 
 * @author Jeremy Smith(n864207) / Salman Shahid(n8303606)
 *
 */
public class CarPark {

	private int maxCarSpaces;
	private int maxRegCarSpaces;
	private int maxSmallCarSpaces;
	private int maxMotorCycleSpaces;
	private int maxQueueSize;
	private int totalSpaces;
	private ArrayList<Vehicle> queue = new ArrayList<Vehicle>();
	private int count;
	private ArrayList<Vehicle> spaces = new ArrayList<Vehicle>();
	private int numCars;
	private int numRegCars;
	private int numSmallCars;
	private int numMotorCycles;
	private int numDissatisfied;
	private ArrayList<Vehicle> past = new ArrayList<Vehicle>();
	private String status = "";
	private ArrayList<Vehicle> temp = new ArrayList<Vehicle>();
	
	/**
	 * CarPark constructor sets the basic size parameters. 
	 * Uses default parameters
	 */
	public CarPark() {
		this(Constants.DEFAULT_MAX_CAR_SPACES,Constants.DEFAULT_MAX_SMALL_CAR_SPACES,
				Constants.DEFAULT_MAX_MOTORCYCLE_SPACES,Constants.DEFAULT_MAX_QUEUE_SIZE);
	}
	
	/**
	 * CarPark constructor sets the basic size parameters. 
	 * @param maxCarSpaces maximum number of spaces allocated to cars in the car park 
	 * @param maxSmallCarSpaces maximum number of spaces (a component of maxCarSpaces) 
	 * 						 restricted to small cars
	 * @param maxMotorCycleSpaces maximum number of spaces allocated to MotorCycles
	 * @param maxQueueSize maximum number of vehicles allowed to queue
	 */
	public CarPark(int maxCarSpaces,int maxSmallCarSpaces, int maxMotorCycleSpaces, int maxQueueSize) {
		//sets the variables
		this.maxCarSpaces = maxCarSpaces - maxSmallCarSpaces;
		this.maxRegCarSpaces = maxCarSpaces - maxSmallCarSpaces;
		this.maxSmallCarSpaces = maxSmallCarSpaces;
		this.maxMotorCycleSpaces = maxMotorCycleSpaces;
		this.maxQueueSize = maxQueueSize;
		totalSpaces = maxCarSpaces + maxMotorCycleSpaces;
	}

	/**
	 * Archives vehicles exiting the car park after a successful stay. Includes transition via 
	 * Vehicle.exitParkedState(). 
	 * @param time int holding time at which vehicle leaves
	 * @param force boolean forcing departure to clear car park 
	 * @throws VehicleException if vehicle to be archived is not in the correct state 
	 * @throws SimulationException if one or more departing vehicles are not in the car park when operation applied
	 */
	public void archiveDepartingVehicles(int time,boolean force) throws VehicleException, SimulationException {
		// clear the list
		temp.clear();
		
		// check if car park is forced to clear and move all vehicles if true
		if (force) {
			for (Vehicle v : spaces) {
				if (!v.isParked()) {
					throw new VehicleException("The vehicle is not in the correct state.");
				}
				temp.add(v);
			}
		} else {
			// check each vehicle for if they have overstayed their duration or not
			for (Vehicle v : spaces) {
				if (time >= v.getDepartureTime()) {
					temp.add(v);
				}
			}
		}
		
		for (Vehicle v : temp) {
			unparkVehicle(v, v.getDepartureTime());
			status += setVehicleMsg(v, "P", "A");
		}
	}
		
	/**
	 * Method to archive new vehicles that don't get parked or queued and are turned 
	 * away
	 * @param v Vehicle to be archived
	 * @throws SimulationException if vehicle is currently queued or parked
	 */
	public void archiveNewVehicle(Vehicle v) throws SimulationException {
		// check for an exception otherwise archive vehicle
		if (v.isQueued() || v.isParked()) {
			throw new SimulationException("The vehicle is already parked or queued.");
		} else {
			past.add(v);
			status += setVehicleMsg(v, "N", "A");
			numDissatisfied++;
		}
	}
	
	/**
	 * Archive vehicles which have stayed in the queue too long 
	 * @param time int holding current simulation time 
	 * @throws VehicleException if one or more vehicles not in the correct state or if timing constraints are violated
	 * @throws SimulationException 
	 */
	public void archiveQueueFailures(int time) throws VehicleException, SimulationException {
		// check each vehicle in the queue and archive if applicable
		for (Vehicle v : queue) {
			if (!v.isQueued()) {
				throw new VehicleException("Vehicle is not in the correct state.");
			} else {
				if (time == v.getArrivalTime() + Constants.MAXIMUM_QUEUE_TIME) {
					past.add(v);
					status += setVehicleMsg(v, "Q", "A");
					exitQueue(v, time);
					numDissatisfied++;
				}
			}
		}
	}
	
	/**
	 * Simple status showing whether carPark is empty
	 * @return true if car park empty, false otherwise
	 */
	public boolean carParkEmpty() {
		if (spaces.size() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Simple status showing whether carPark is full
	 * @return true if car park full, false otherwise
	 */
	public boolean carParkFull() {
		if (spaces.size() >= totalSpaces) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Method to add vehicle successfully to the queue
	 * Precondition is a test that spaces are available
	 * Includes transition through Vehicle.enterQueuedState 
	 * @param v Vehicle to be added 
	 * @throws SimulationException if queue is full  
	 * @throws VehicleException if vehicle not in the correct state 
	 */
	public void enterQueue(Vehicle v) throws SimulationException, VehicleException {
		// checks for an exception
		if (queueFull()) {
			throw new SimulationException("Queue is full.");
		} else if (v.isQueued() || v.isParked()) {
			throw new VehicleException("The vehicle is in the wrong state.");
		} else {
			// add to queue
			queue.add(v);
			v.enterQueuedState();
		}
	}
	
	
	/**
	 * Method to remove vehicle from the queue after which it will be parked or 
	 * removed altogether. Includes transition through Vehicle.exitQueuedState.  
	 * @param v Vehicle to be removed from the queue 
	 * @param exitTime int time at which vehicle exits queue
	 * @throws SimulationException if vehicle is not in queue 
	 * @throws VehicleException if the vehicle is in an incorrect state or timing 
	 * constraints are violated
	 */
	public void exitQueue(Vehicle v,int exitTime) throws SimulationException, VehicleException {
		// checks for an exception
		if (!queue.contains(v)) {
			throw new SimulationException("Vehicle is not in queue.");
		} else if (!v.isParked()) {
			throw new VehicleException("Vehicle is not in the correct state.");
		} else {
			// remove the vehicle from the queue
			queue.remove(v);
			v.exitQueuedState(exitTime);
		}
	}
	
	/**
	 * State dump intended for use in logging the final state of the carpark
	 * All spaces and queue positions should be empty and so we dump the archive
	 * @return String containing dump of final carpark state 
	 */
	public String finalState() {
		String str = "Vehicles Processed: count:" + 
				this.count + ", logged: " + this.past.size() 
				+ "\nVehicle Record: \n";
		for (Vehicle v : this.past) {
			str += v.toString() + "\n\n";
		}
		return str + "\n";
	}
	
	/**
	 * Simple getter for number of cars in the car park 
	 * @return number of cars in car park, including small cars
	 */
	public int getNumCars() {
		// counts the number of Car instances in the car park and returns that number
		int count = 0;
		for (Vehicle v : spaces) {
			if (v instanceof Car) {
				count += 1;
			}
		}
		return count;
	}
	
	/**
	 * Simple getter for number of motorcycles in the car park 
	 * @return number of MotorCycles in car park, including those occupying 
	 * 			a small car space
	 */
	public int getNumMotorCycles() {
		// counts the number of MotorCycles instances in the car park and returns that number
		int count = 0;
		for (Vehicle v : spaces) {
			if (v instanceof MotorCycle) {
				count += 1;
			}
		}
		return count;
	}
	
	/**
	 * Simple getter for number of small cars in the car park 
	 * @return number of small cars in car park, including those 
	 * 		   not occupying a small car space. 
	 */
	public int getNumSmallCars() {
		// counts the number of Car instances in the car park and returns that number
		int count = 0;
		for (Vehicle v : spaces) {
			if (v instanceof Car) {
				if (((Car) v).isSmall()) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * Method used to provide the current status of the car park. 
	 * Uses private status String set whenever a transition occurs. 
	 * Example follows (using high probability for car creation). At time 262, 
	 * we have 276 vehicles existing, 91 in car park (P), 84 cars in car park (C), 
	 * of which 14 are small (S), 7 MotorCycles in car park (M), 48 dissatisfied (D),
	 * 176 archived (A), queue of size 9 (CCCCCCCCC), and on this iteration we have 
	 * seen: car C go from Parked (P) to Archived (A), C go from queued (Q) to Parked (P),
	 * and small car S arrive (new N) and go straight into the car park<br>
	 * 262::276::P:91::C:84::S:14::M:7::D:48::A:176::Q:9CCCCCCCCC|C:P>A||C:Q>P||S:N>P|
	 * @return String containing current state 
	 */
	public String getStatus(int time) {
		numCars = getNumCars();
		numSmallCars = getNumSmallCars();
		numMotorCycles = getNumMotorCycles();
		
		String str = time +"::"
		+ this.count + "::" 
		+ "P:" + this.spaces.size() + "::"
		+ "C:" + this.numCars + "::S:" + this.numSmallCars 
		+ "::M:" + this.numMotorCycles 
		+ "::D:" + this.numDissatisfied 
		+ "::A:" + this.past.size()  
		+ "::Q:" + this.queue.size(); 
		for (Vehicle v : this.queue) {
			if (v instanceof Car) {
				if (((Car)v).isSmall()) {
					str += "S";
				} else {
					str += "C";
				}
			} else {
				str += "M";
			}
		}
		str += this.status;
		this.status="";
		return str+"\n";
	}
	

	/**
	 * State dump intended for use in logging the initial state of the carpark.
	 * Mainly concerned with parameters. 
	 * @return String containing dump of initial carpark state 
	 */
	public String initialState() {
		return "CarPark [maxCarSpaces: " + this.maxCarSpaces
				+ " maxSmallCarSpaces: " + this.maxSmallCarSpaces 
				+ " maxMotorCycleSpaces: " + this.maxMotorCycleSpaces 
				+ " maxQueueSize: " + this.maxQueueSize + "]";
	}

	/**
	 * Simple status showing number of vehicles in the queue 
	 * @return number of vehicles in the queue
	 */
	public int numVehiclesInQueue() {
		return queue.size();
	}
	
	/**
	 * Method to add vehicle successfully to the car park store. 
	 * Precondition is a test that spaces are available. 
	 * Includes transition via Vehicle.enterParkedState.
	 * @param v Vehicle to be added 
	 * @param time int holding current simulation time
	 * @param intendedDuration int holding intended duration of stay 
	 * @throws SimulationException if no suitable spaces are available for parking 
	 * @throws VehicleException if vehicle not in the correct state or timing constraints are violated
	 */
	public void parkVehicle(Vehicle v, int time, int intendedDuration) throws SimulationException, VehicleException {
		// checks for an exception
		if (!spacesAvailable(v)) {
			throw new SimulationException("No suitable spaces are available for parking.");
		} else {
			// park the vehicle
			spaces.add(v);
			v.enterParkedState(time, intendedDuration);
		}
	}

	/**
	 * Silently process elements in the queue, whether empty or not. If possible, add them to the car park. 
	 * Includes transition via exitQueuedState where appropriate
	 * Block when we reach the first element that can't be parked. 
	 * @param time int holding current simulation time 
	 * @throws SimulationException if no suitable spaces available when parking attempted
	 * @throws VehicleException if state is incorrect, or timing constraints are violated
	 */
	public void processQueue(int time, Simulator sim) throws VehicleException, SimulationException {
		Iterator<Vehicle> queuedVehicleCopy = new ArrayList<Vehicle>(queue).iterator();
		while (queuedVehicleCopy.hasNext() == true) {
			Vehicle v = queuedVehicleCopy.next();
			if (v.isQueued() == false) {
				throw new VehicleException("State is incorrect, or timing constraints are violated");
			}
			if (spacesAvailable(v) == true) {
				exitQueue(v, time);
				parkVehicle(v, time, sim.setDuration());
				status = status + setVehicleMsg(v, "Q", "P");
			}
			else {
				throw new SimulationException("No suitable spaces available when parking attempted");
			}
		}
	}

	/**
	 * Simple status showing whether queue is empty
	 * @return true if queue empty, false otherwise
	 */
	public boolean queueEmpty() {
		if (queue.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Simple status showing whether queue is full
	 * @return true if queue full, false otherwise
	 */
	public boolean queueFull() {
		if (queue.size() >= maxQueueSize) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Method determines, given a vehicle of a particular type, whether there are spaces available for that 
	 * type in the car park under the parking policy in the class header.  
	 * @param v Vehicle to be stored. 
	 * @return true if space available for v, false otherwise 
	 */
	public boolean spacesAvailable(Vehicle v) {
		if (carParkFull()) {
			return false;
			
		}
		else if (v instanceof Car) {
			if(getNumCars() < maxCarSpaces) {
				return true;
			}
		}
		else if(v instanceof Car) {
			if(((Car)v).isSmall()) {
				if((getNumSmallCars() < maxSmallCarSpaces) || (getNumCars() < maxCarSpaces)) {
					return true;
				}
				else {
					return false;
				}
			}
		}
		else if (v instanceof MotorCycle) {
			if((getNumSmallCars() < maxSmallCarSpaces) || (getNumMotorCycles() < maxMotorCycleSpaces)) {
			return true;
			}
			else {
			return false;
			}
		}
		else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return status;
	}

	/**
	 * Method to try to create new vehicles (one trial per vehicle type per time point) 
	 * and to then try to park or queue (or archive) any vehicles that are created 
	 * @param sim Simulation object controlling vehicle creation 
	 * @throws SimulationException if no suitable spaces available when operation attempted 
	 * @throws VehicleException if vehicle creation violates constraints 
	 */
	public void tryProcessNewVehicles(int time,Simulator sim) throws VehicleException, SimulationException {
		// method variables
		boolean car = false;
		boolean smallCar = false;
		boolean mc = false;
		Vehicle v;
		String vehID;
		int intendedDuration;
		
		// trial to determine type of vehicle
		car = sim.newCarTrial();
		smallCar = sim.smallCarTrial();
		mc = sim.motorCycleTrial();
		
		// check for a car
		if (car) {
			vehID = "C" + Integer.toString(count + 1);
			count++;
			intendedDuration = sim.setDuration();
			
			// create a small car and park, queue or archive
			if (smallCar) {
				v = new Car(vehID, time, true);
				if (spacesAvailable(v)) {
					parkVehicle(v, time, intendedDuration);
					status += setVehicleMsg(v, "N", "P");
				} else if (queueFull()) {
					archiveNewVehicle(v);
					status += setVehicleMsg(v, "N", "A");
				} else {
					enterQueue(v);
					status += setVehicleMsg(v, "N", "Q");
				}
			} else {
				//create a general car and park, queue or archive
				v = new Car(vehID, time, false);
				if (spacesAvailable(v)) {
					parkVehicle(v, time, intendedDuration);
					status += setVehicleMsg(v, "N", "P");
				} else if (queueFull()) {
					archiveNewVehicle(v);
					status += setVehicleMsg(v, "N", "A");
				} else {
					enterQueue(v);
					status += setVehicleMsg(v, "N", "Q");
				}
			}
		}
		
		// check for a motorcycle
		if (mc) {
			// create a motorcycle and park, queue or archive
			vehID = "MC" + Integer.toString(count + 1);
			count++;
			intendedDuration = sim.setDuration();
			v = new MotorCycle(vehID, time);
			
			if (spacesAvailable(v)) {
				parkVehicle (v, time, intendedDuration);
				status += setVehicleMsg(v, "N", "P");
			} else if (queueFull()) {
				archiveNewVehicle(v);
				status += setVehicleMsg(v, "N", "A");
			} else {
				enterQueue(v);
				status += setVehicleMsg(v, "N", "Q");
			}
		}
		
	}
	/**
	 * Method to remove vehicle from the carpark. 
	 * For symmetry with parkVehicle, include transition via Vehicle.exitParkedState.  
	 * So vehicle should be in parked state prior to entry to this method. 
	 * @param v Vehicle to be removed from the car park 
	 * @throws VehicleException if Vehicle is not parked, is in a queue, or violates timing constraints 
	 * @throws SimulationException if vehicle is not in car park
	 */
	public void unparkVehicle(Vehicle v,int departureTime) throws VehicleException, SimulationException {
		// check for an exception
		if (!v.isParked() || v.isQueued()) {
			throw new VehicleException("Vehicle is in the incorrect state.");
		} else if (!spaces.contains(v)) {
			throw new SimulationException("Vehicle is not in the car park.");
		} else {
			// unpark the vehicle
			spaces.remove(v);
			past.add(v);
			v.exitParkedState(departureTime);
		}
	}
	
	/**
	 * Helper to set vehicle message for transitions 
	 * @param v Vehicle making a transition (uses S,C,M)
	 * @param source String holding starting state of vehicle (N,Q,P,A) 
	 * @param target String holding finishing state of vehicle (Q,P,A) 
	 * @return String containing transition in the form: |(S|C|M):(N|Q|P|A)>(Q|P|A)| 
	 */
	private String setVehicleMsg(Vehicle v,String source, String target) {
		String str="";
		if (v instanceof Car) {
			if (((Car)v).isSmall()) {
				str+="S";
			} else {
				str+="C";
			}
		} else {
			str += "M";
		}
		return "|"+str+":"+source+">"+target+"|";
	}
}
