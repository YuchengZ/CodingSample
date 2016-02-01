package edu.cmu.cs.cs214.hw3;

import java.util.List;
import java.util.Random;

import edu.cmu.cs.cs214.hw3.people.BasicPerson;
import edu.cmu.cs.cs214.hw3.people.Person;
import edu.cmu.cs.cs214.hw3.people.PersonGroup;
import edu.cmu.cs.cs214.hw3.people.PersonInWheelChair;
import edu.cmu.cs.cs214.hw3.people.PersonWithCash;
import edu.cmu.cs.cs214.hw3.people.PersonWithLuggage;
import edu.cmu.cs.cs214.hw2.DefaultRoutePlannerBuilder;
import edu.cmu.cs.cs214.hw2.Itinerary;
import edu.cmu.cs.cs214.hw2.RoutePlanner;
import edu.cmu.cs.cs214.hw2.Stop;

/**
 * class PersonFactory
 * 
 * @author ZhangYC
 *
 */
public class PersonFactory implements Factory {
	private Simulation simulation;
	private int totalRider;
	private DefaultRoutePlannerBuilder builder = new DefaultRoutePlannerBuilder();
	private RoutePlanner rp;
	private int wheelChairRate;
	private int payInCashRate;
	private int luggageRate;
	private int groupRate;

	/**
	 * constructor
	 * 
	 * @param s
	 *            Simulation
	 * @param i
	 *            totalRiders
	 * @param wheelChair
	 *            how many people in wheelchair in 10 people
	 * @param cash
	 *            how many people use cash in 10 people
	 * @param luggage
	 *            how many people has luggage in 10 people
	 * @param group
	 *            hwo many people in group in 10 people
	 */
	public PersonFactory(Simulation s, int i, int wheelChair, int cash,
			int luggage, int group) {
		final int maxWaitTime = 1200;
		simulation = s;
		totalRider = i;
		wheelChairRate = wheelChair;
		payInCashRate = cash;
		luggageRate = luggage;
		groupRate = group;
		try {
			// I don't know why some itinerary calculate is really slow. if
			// using 1.txt, which only has one bus can make people behavior on
			// the map more smoothly.
			rp = builder.build("src/main/resources/oakland_stop_times.txt",
					maxWaitTime);
		} catch (Exception e) {
			return;
		}
	}

	@Override
	public void step() {
		// TODO Auto-generated method stub
		int t;
		int n;

		t = simulation.getTime();
		n = (int) Util.getRidersPerSecond(t, totalRider);
		// build riders
		buildPerson(t, n);

	}

	private void buildPerson(int t, int n) {
		String s, e;
		Itinerary itinerary = null;
		Person p;
		for (int i = 0; i < n; i++) {
			try {
				s = Util.getRandomStop();
				e = Util.getRandomStop();

				// decide this person has witch property
				boolean wheelChair = isWheelChair();
				boolean payCash = isPayCash();
				boolean luggage = isLuggage();
				boolean group = isGroup();

				// cerate itinerary
				List<Stop> srtStops = rp.findStopsBySubstring(s);
				Stop start = srtStops.get(0);
				List<Stop> endStops = rp.findStopsBySubstring(e);
				Stop end = endStops.get(0);
				itinerary = rp.computeRoute(start, end, t);
				// continue: set location add new person
				if (!itinerary.getInstructions().contains("no")) {
					p = initNewPerson(wheelChair, payCash, luggage, group);
					p = new PersonInWheelChair(new BasicPerson());
					p.create(start.getName(), end.getName(), t, itinerary,
							simulation);
					simulation.insertPerson(p);
				}
			} catch (Exception except) {
				System.out.println("invalid itinerary");
			}
		}
	}

	// helper funciton in buildPerson()
	private Person initNewPerson(boolean wheelChair, boolean payCash,
			boolean luggage, boolean group) {
		Random generator = new Random();
		int luggageNum = generator.nextInt(2) + 1;
		int groupNum = generator.nextInt(2) + 2;
		Person p;
		if (wheelChair) {
			if (payCash) {
				if (luggage) {
					if (group) {
						p = new PersonInWheelChair(new PersonWithCash(
								new PersonWithLuggage(new PersonGroup(
										new BasicPerson(), groupNum),
										luggageNum)));
					} else {
						p = new PersonInWheelChair(new PersonWithCash(
								new PersonWithLuggage(new BasicPerson(),
										luggageNum)));
					}
				} else {
					if (group) {
						p = new PersonInWheelChair(new PersonWithCash(
								new PersonGroup(new BasicPerson(), groupNum)));
					} else {
						p = new PersonInWheelChair(new PersonWithCash(
								new BasicPerson()));
					}
				}
			} else {
				if (luggage) {
					if (group) {
						p = new PersonInWheelChair(new PersonWithLuggage(
								new PersonGroup(new BasicPerson(), groupNum),
								luggageNum));
					} else {
						p = new PersonInWheelChair(new PersonWithLuggage(
								new BasicPerson(), luggageNum));
					}
				} else {
					if (group) {
						p = new PersonInWheelChair(new PersonGroup(
								new BasicPerson(), groupNum));
					} else {
						p = new PersonInWheelChair(new BasicPerson());
					}
				}
			}
		} else {
			if (payCash) {
				if (luggage) {
					if (group) {
						p = new PersonWithCash(new PersonWithLuggage(
								new PersonGroup(new BasicPerson(), groupNum),
								luggageNum));
					} else {
						p = new PersonWithCash(new PersonWithLuggage(
								new BasicPerson(), luggageNum));
					}
				} else {
					if (group) {
						p = new PersonWithCash(new PersonGroup(
								new BasicPerson(), groupNum));
					} else {
						p = new PersonWithCash(new BasicPerson());
					}
				}
			} else {
				if (luggage) {
					if (group) {
						p = new PersonWithLuggage(new PersonGroup(
								new BasicPerson(), groupNum), luggageNum);
					} else {
						p = new PersonWithLuggage(new BasicPerson(), luggageNum);
					}
				} else {
					if (group) {
						p = new PersonGroup(new BasicPerson(), groupNum);
					} else {
						p = new BasicPerson();
					}
				}
			}
		}
		return p;
	}

	// return if this people in wheelchair or ont
	private boolean isWheelChair() {
		final int range = 10;
		Random generator = new Random();
		int i = generator.nextInt(range) + 1;
		if (i <= wheelChairRate) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * return if the person pay with cash or not
	 * 
	 * @return
	 */
	private boolean isPayCash() {
		final int range = 10;
		Random generator = new Random();
		int i = generator.nextInt(range) + 1;
		if (i <= payInCashRate) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * return a boolean of if the person has luggage or not
	 * 
	 * @return
	 */
	private boolean isLuggage() {
		final int range = 10;
		Random generator = new Random();
		int i = generator.nextInt(range) + 1;
		if (i <= luggageRate) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * return if this is one person or a group
	 * 
	 * @return
	 */
	private boolean isGroup() {
		final int range = 10;
		Random generator = new Random();
		int i = generator.nextInt(range) + 1;
		if (i <= groupRate) {
			return true;
		} else {
			return false;
		}
	}
}
