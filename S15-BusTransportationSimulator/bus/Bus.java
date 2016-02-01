package edu.cmu.cs.cs214.hw3.bus;

import java.util.List;

import javax.swing.ImageIcon;

import edu.cmu.cs.cs214.hw3.Entity;
import edu.cmu.cs.cs214.hw3.Location;
import edu.cmu.cs.cs214.hw3.Simulation;
import edu.cmu.cs.cs214.hw3.routePlanner.BusStop;
/**
 * abstract class bus
 * @author ZhangYC
 *
 */
public abstract class Bus implements Entity {
	// start form -1
	private int stopIndex = -1;
	// delay time for one segment
	private int oneStopDelayTime = 0;
	private String name;
	private Location location;
	private List<BusStop> schedule;
	private int spacePeople;
	private int spaceLuggage;
	private int doors;
	private Simulation s;
	private int lastStopRealTime;

	
	@Override
	public ImageIcon getImage() {
		ImageIcon image = new ImageIcon("src/main/resources/bus.png");
		return image;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	/**
	 * get delay time for two connected stops
	 * @return int
	 */
	public int getDelay() {
		return oneStopDelayTime;
	}

	/**
	 * get schedule for this bus
	 * @return a list of BusStop
	 */
	public List<BusStop> getSchedule() {
		return schedule;
	}

	/**
	 * get available space for people
	 * @return int
	 */
	public int getSpacePeople() {
		return spacePeople;
	}

	/**
	 * get available space for luggage
	 * @return int
	 */
	public int getSpaceLuggage() {
		return spaceLuggage;
	}
	
	@Override
	public void setLocation(Location loc) {
		location = loc;
	}
	
	/**
	 * set simulation for bus
	 * @param simulation Simulation
	 */
	public void setSimulation(Simulation simulation) {
		s = simulation;
	}

	/**
	 * set name for bus
	 * @param string String
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * set door number for bus
	 * @param i int
	 */
	public void setDoor(int i) {
		doors = i;
	}

	/**
	 * return index of current stop
	 * @return int
	 */
	public int currentStopIndex() {
		return stopIndex;
	}

	/**
	 * add board and unboard delay
	 * @param board int
	 * @param unboard int
	 */
	public void addPassagerDelay(int board, int unboard) {
		unboard = unboard / doors;
		int t = board + unboard;
		oneStopDelayTime += t;
	}

	/**
	 * set schedle of a bus
	 * @param stops a list of BusStop
	 */
	public void setSchedule(List<BusStop> stops) {
		schedule = stops;
	}

	/**
	 * set space for people
	 * @param i int
	 */
	public void setSpacePeople(int i) {
		spacePeople = i;
	}

	/**
	 * set space for luggage
	 * @param i int
	 */
	public void setSpaceLuggage(int i) {
		spaceLuggage = i;
	}

	/**
	 * 
	 * @return
	 */
	abstract double driverSpeedUp();

	/**
	 * create a bus
	 * @param busname String
	 * @param stops the schedule
	 * @param pSpace person space
	 * @param lSpace luggage space
	 * @param doorNum door number
	 * @param simulation Simulation
	 */
	public void create(String busname, List<BusStop> stops, int pSpace,
			int lSpace, int doorNum, Simulation simulation) {
		setName(busname);
		setSchedule(stops);
		setLocation(stops.get(0).getLocation());
		setSpacePeople(pSpace);
		setSpaceLuggage(lSpace);
		setDoor(doorNum);
		setSimulation(simulation);
		lastStopRealTime = schedule.get(0).getTime();

	}


	/**
	 * the bus behavior in one step
	 */
	public void step() {
		// get time
		int time = s.getTime();
		
		// if arrived this stop
		while (arrived(time)) {
			// reset the status of this bus
			reset();
			// call arriveAt to inform Person
			s.arriveAt(this, schedule.get(stopIndex));
			// if arrived destination, break, tell simulation
			if (location
					.equals(schedule.get(schedule.size() - 1).getLocation())) {

				int delay = s.getTime()
						- schedule.get(schedule.size() - 1).getTime();
				if (delay>0){
					s.totalDelayForBus();
				}
				
				s.removeBus(this);
				break;
			}
		}
		return;
	}


	private boolean arrived(int time) {
		try {
			/*
			 * calculate time for next stop according to the PDF, real time =
			 * schedule time*0.9 + delay time after that, also consider the
			 * ability to speed up and the weather condition
			 */
			int arriveTime;
			final double rate = 0.9;
			int inter = schedule.get(stopIndex + 1).getTime()
					- schedule.get(stopIndex).getTime();
			
			// consider bad weather or not
			if (s.isBadWeather()) {
				final double rainyDelay = 0.2;	
				arriveTime = (int) (lastStopRealTime + inter*rate * driverSpeedUp()
						* (1 + rainyDelay) + oneStopDelayTime);
			} else {
				arriveTime = (int) (lastStopRealTime + inter *rate* driverSpeedUp() + oneStopDelayTime);
			}
			
			// if consider wait
			boolean wait = s.isWait();
			if (wait) {
				if (time>=arriveTime && time>=schedule.get(stopIndex+1).getTime()) {
					lastStopRealTime = time;
					oneStopDelayTime = 0;
					return true;
				}
			} else {
				// if arrived
				if (arriveTime == time) {
					lastStopRealTime = time;
					oneStopDelayTime = 0;
					return true;
				}
			}
		} catch (Exception e) {
			if (stopIndex == -1) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	private void reset() {
		stopIndex += 1;
		location = schedule.get(stopIndex).getLocation();
	}

}
