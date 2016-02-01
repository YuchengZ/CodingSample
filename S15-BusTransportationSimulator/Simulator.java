package edu.cmu.cs.cs214.hw3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.cmu.cs.cs214.hw3.bus.Bus;
import edu.cmu.cs.cs214.hw3.people.Person;
import edu.cmu.cs.cs214.hw3.routePlanner.BusStop;

/**
 * 
 * @author ZhangYC
 *
 */
public class Simulator implements Simulation {
	private final int timeBegin = 4;
	private final int seconds = 3600;;
	private List<Person> riders = new LinkedList<Person>();
	private List<Bus> buses = new LinkedList<Bus>();
	private int counter = timeBegin * seconds;
	private PersonFactory pf;
	private BusFactory bf;
	private List<Entity> entities = new ArrayList<Entity>();
	private int peopleDelay = 0;
	private int busDelay = 0;
	private boolean badWeather;
	private boolean wait;

	/**
	 * constructor
	 * @param b boolean, if bad weather or not
	 * @param w boolean, if driver wait or not when he ahead of schedule
	 */
	public Simulator(boolean b, boolean w) {
		badWeather = b;
		wait = w;
	}

	@Override
	public void step() {
		pf.step();
		bf.step();
		for (Bus bus : buses) {
			if (bus != null) {
				bus.step();
			}
		}
		counter++;
	}

	@Override
	public int getTime() {
		return counter;
	}

	@Override
	public Iterable<Entity> getEntities() {
		// TODO Auto-generated method stub
		return entities;
	}

	@Override
	public String getAnalysisResult() {
		double pDelay = (riders.size()-peopleDelay * 1.0) / riders.size();
		double bDelay = (buses.size() - busDelay * 1.0 )/ buses.size();
		String s = String
				.format("rider on time: %1$f | bus on time is: %2$f",
						pDelay, bDelay);
		return s;
	}

	@Override
	public void insertPerson(Person p) {
		// TODO Auto-generated method stub
		riders.add(p);
		entities.add(p);

	}

	@Override
	public void insertBus(Bus b) {
		// TODO Auto-generated method stub
		buses.add(b);
		entities.add(b);

	}

	@Override
	public void addPersonFactory(PersonFactory p) {
		pf = p;

	}

	@Override
	public void addBusFactory(BusFactory b) {
		bf = b;

	}

	@Override
	public void arriveAt(Bus bus, BusStop stop) {
		for (Person p : riders) {
			if (p != null) {
				p.busArrived(bus, stop);
			}
		}
	}

	@Override
	public void removePeople(Person p) {
		entities.remove(p);

	}

	@Override
	public void removeBus(Bus b) {
		entities.remove(b);

	}

	@Override
	public void totalDelayForPeople() {
		peopleDelay += 1;

	}

	@Override
	public boolean isBadWeather() {
		// TODO Auto-generated method stub
		return badWeather;
	}

	@Override
	public void totalDelayForBus() {
		busDelay += 1;

	}

	@Override
	public boolean isWait() {
		// TODO Auto-generated method stub
		return wait;
	}
}
