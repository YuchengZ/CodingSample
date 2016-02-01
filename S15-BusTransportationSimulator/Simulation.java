package edu.cmu.cs.cs214.hw3;

import edu.cmu.cs.cs214.hw3.bus.Bus;
import edu.cmu.cs.cs214.hw3.people.Person;
import edu.cmu.cs.cs214.hw3.routePlanner.BusStop;


/**
 * The city transportation simulation. The simulation interface is meant to be a
 * subset of the methods that will be needed for a correct implementation. A
 * single run of the simulation should represent a full day of a bus schedule
 * starting at approximately 3:30am (03:30 hrs) and ending at 12:00am (00:00
 * hrs) the following day.
 */
public interface Simulation {

	/**
	 * Performs a single step in the simulation. Each step should represent one
	 * second in the day's simulation.
	 */
	void step();

	/**
	 * Returns to the caller the seconds that have elapsed since midnight.
	 * 
	 * @return The seconds since midnight
	 */
	int getTime();

	/**
	 * Returns the collection of {@link Entity}s in this Simulation. The
	 * <code>Iterable</code> interface enables this collection to be used in a
	 * "for each" loop:
	 *
	 * <pre>
	 *   e.g. <code> for(Entity entity : simulation.getEntities()) {...}
	 * </pre>
	 *
	 * @return a collection of all {@link Entity}s in this Simulation
	 */
	Iterable<Entity> getEntities();

	/**
	 * A method that a user of the simulation will use to display an analysis of
	 * the current (or final) state of the simulation. The returned string
	 * should be a summary of important statistics of the simulation. An example
	 * return value could be:
	 * 
	 * "Busses on time: 67%  | People on time: 45%"
	 * 
	 * @return a string with an analysis of the current state of the simulation
	 */
	String getAnalysisResult();

	/**
	 * add personFactory
	 * @param pf PersonFactory
	 */
	void addPersonFactory(PersonFactory pf);

	/**
	 * add BusFactory
	 * @param bf BusFactory
	 */
	void addBusFactory(BusFactory bf);

	/**
	 * insert person
	 * @param p Person
	 */
	void insertPerson(Person p);

	/**
	 * insert bus
	 * @param b Bus
	 */
	void insertBus(Bus b);

	/**
	 * when bus arrives stop, do something
	 * @param bus Bus
	 * @param stop BusStop
	 */
	void arriveAt(Bus bus, BusStop stop);

	/**
	 * remove people
	 * @param p People
	 */
	void removePeople(Person p);

	/**
	 * remove bus
	 * @param b Bus
	 */
	void removeBus(Bus b);

	/**
	 * add delay person
	 */
	void totalDelayForPeople();
	/**
	 * add delay bus
	 */
	void totalDelayForBus();

	/**
	 * is now bad weather or not
	 * @return Boolean
	 */
	boolean isBadWeather();
	
	/**
	 * does driver wait or not when ahead of schedule
	 * @return boolean
	 */
	boolean isWait();
	

}
