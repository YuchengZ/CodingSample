package edu.cmu.cs.cs214.hw3.people;

import javax.swing.ImageIcon;

import edu.cmu.cs.cs214.hw2.Itinerary;
import edu.cmu.cs.cs214.hw3.Entity;
import edu.cmu.cs.cs214.hw3.Location;
import edu.cmu.cs.cs214.hw3.Simulation;
import edu.cmu.cs.cs214.hw3.bus.Bus;
import edu.cmu.cs.cs214.hw3.routePlanner.BusStop;

/**
 * 
 * @author ZhangYC
 *
 */
public abstract class Person implements Entity {
	private Itinerary itinerary;
	private String name = "p";
	private Location location;
	private String strStop;
	private String endStop;
	private int strTime;
	private Bus onBoard = null;
	private int currentIndex = 0;
	private Simulation s;

	@Override
	public ImageIcon getImage() {
		return new ImageIcon("src/main/resources/people.png");
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
	 * return start stop
	 * 
	 * @return String of stop name
	 */
	public String getStartStop() {
		return strStop;
	}

	/**
	 * return end Stop
	 * 
	 * @return String of stop name
	 */
	public String getEndtStop() {
		return endStop;
	}

	/**
	 * get itinerary of the person
	 * 
	 * @return Itinerary
	 */
	public Itinerary getItinerary() {
		return itinerary;
	}

	/**
	 * get start time
	 * 
	 * @return int
	 */
	public int getStrTime() {
		return strTime;
	}

	@Override
	public void setLocation(Location loc) {
		location = loc;
	}

	/**
	 * set start stop
	 * 
	 * @param string
	 *            String of stop name
	 */
	public void setStartStop(String string) {
		strStop = string;
	}

	/**
	 * set end stop
	 * 
	 * @param string
	 *            String
	 */
	public void setEndStop(String string) {
		endStop = string;
	}

	/**
	 * set itinerary
	 * 
	 * @param i
	 *            Itinerary
	 */
	public void setItinerary(Itinerary i) {
		itinerary = i;
	}

	/**
	 * set start time
	 * 
	 * @param time
	 *            int
	 */
	public void setStrTime(int time) {
		strTime = time;
	}

	/**
	 * set what bus does this person in
	 * 
	 * @param bus
	 *            Bus
	 */
	public void setOnBoard(Bus bus) {
		onBoard = bus;
	}

	/**
	 * set simulation
	 * 
	 * @param simulation
	 *            Simulation
	 */
	public void setSimulation(Simulation simulation) {
		s = simulation;
	}

	/**
	 * create a person
	 * 
	 * @param start
	 *            String of strstop
	 * @param end
	 *            String of endstop
	 * @param time
	 *            int of start time
	 * @param i
	 *            Itinerary
	 * @param simulation
	 *            Simulation
	 */
	public void create(String start, String end, int time, Itinerary i,
			Simulation simulation) {
		setSimulation(simulation);
		setStartStop(start);
		setEndStop(end);
		setStrTime(time);
		setItinerary(i);
		Location loc = new Location(i.getStartLocation().getLatitude(), i
				.getStartLocation().getLongitude());
		setLocation(loc);
	}

	/**
	 * the person behavior when bus arrived
	 * 
	 * @param bus
	 *            Bus
	 * @param stop
	 *            BusStop bus arrived
	 */
	public void busArrived(Bus bus, BusStop stop) {
		int getOffDelay = 0;
		int getOnDelay = 0;

		// people get off
		// when people in this bus
		if (onBoard == bus) {
			Location loc = new Location(itinerary.getSegments()
					.get(currentIndex).getEndLocation().getLatitude(),
					itinerary.getSegments().get(currentIndex).getEndLocation()
							.getLongitude());
			// when this stop is the end stop of this segment
			if (loc.equals(stop.getLocation())) {
				// reset some value
				getOff(stop.getLocation());
				// add get off delay to bus
				getOffDelay += getDelay();
				// add space to bus
				bus.setSpacePeople(bus.getSpacePeople() + getPeopleSpace());
				bus.setSpaceLuggage(bus.getSpaceLuggage() + getLuggageSpace());

				// arrived destination
				if (stop.getName().equals(endStop)
						&& currentIndex == itinerary.getSegments().size()) {

					// remove people form simulation
					s.removePeople(this);
					// see if he delay or not
					int delay = s.getTime() - itinerary.getEndTime();
					if (delay > 0) {
						s.totalDelayForPeople();
					}
				}
			}
		}

		// people get on
		// when there is enough space on bus
		else if (s.getTime() >= itinerary.getStartTime() && onBoard == null
				&& bus.getSpacePeople() >= getPeopleSpace()
				&& bus.getSpaceLuggage() >= getLuggageSpace()) {
			// this is the bus for this segment
			if (couldGetOn(bus, stop)) {
				getOn(bus);
				// add get on delay;
				getOnDelay += getDelay();
				// Consume space
				bus.setSpacePeople(bus.getSpacePeople() - getPeopleSpace());
				bus.setSpaceLuggage(bus.getSpaceLuggage() - getLuggageSpace());
			}
		}
		// add delay for bus
		bus.addPassagerDelay(getOnDelay, getOffDelay);
	}

	private void getOff(Location loc) {
		onBoard = null;
		location = loc;
		currentIndex += 1;
	}

	/**
	 * this method set to public is only for test
	 * @param bus Bus
	 * @param stop Stop
	 * @return boolean
	 */
	public boolean couldGetOn(Bus bus, BusStop stop) {
		try {
			// location of start stop of this segment
			Location pl = new Location(itinerary.getSegments()
					.get(currentIndex).getStartLocation().getLatitude(),
					itinerary.getSegments().get(currentIndex)
							.getStartLocation().getLongitude());

			// bus name of this segment
			String busname = itinerary.getSegments().get(currentIndex)
					.getInstruction().split(" ")[2];

			if (pl.equals(stop.getLocation()) && busname.equals(bus.getName())
					&& bus.getSpacePeople() >= getPeopleSpace()
					&& bus.getSpaceLuggage() >= getLuggageSpace()) {
				return true;
			}
		} catch (Exception e) {

			return false;
		}
		return false;
	}

	private void getOn(Bus bus) {
		onBoard = bus;

	}

	/**
	 * get delay of this person when board and unboard
	 * 
	 * @return int
	 */
	public abstract int getDelay();

	/**
	 * get how many people space this person need
	 * 
	 * @return int
	 */
	public abstract int getPeopleSpace();

	/**
	 * get hwo many luggage space this person need
	 * 
	 * @return int
	 */
	public abstract int getLuggageSpace();

}
