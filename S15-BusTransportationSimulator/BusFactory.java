package edu.cmu.cs.cs214.hw3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.cmu.cs.cs214.hw3.bus.BasicBus;
import edu.cmu.cs.cs214.hw3.bus.Bus;
import edu.cmu.cs.cs214.hw3.bus.BusWithBetterEngine;
import edu.cmu.cs.cs214.hw3.routePlanner.BusStop;
import edu.cmu.cs.cs214.hw3.routePlanner.VertexStop;

/**
 * Class BusFactory implements interface Factory
 * 
 * @author ZhangYC
 *
 */
public class BusFactory implements Factory {
	private List<Bus> buses = new LinkedList<Bus>();
	private List<String[]> storeBus = new ArrayList<String[]>();
	private List<BusStop> storeStops = new ArrayList<BusStop>();
	private Simulation simulation;
	private String filename;
	private int doubleSizeRate; // how many bus has double size in 10 buses.
	private int threeDoorsRate;// how many bus has 3 doors in 10 buses.
	private int betterEngineRate; // how many bus has better engine that could
									// speed up in 10 buses.
	private final int onePeopleSpace = 50; // double size bus is 100
	private final int oneLuggageSpace = 5; // double size bus is 10
	private int normalDoorNum = 2;

	/**
	 * constructor
	 * 
	 * @param s
	 *            type Simulation
	 * @param name
	 *            String
	 * @param doubleSize
	 *            int
	 * @param threeDoors
	 *            int
	 * @param betterEngine
	 *            int
	 */
	public BusFactory(Simulation s, String name, int doubleSize,
			int threeDoors, int betterEngine) {
		simulation = s;
		filename = name;
		doubleSizeRate = doubleSize;
		threeDoorsRate = threeDoors;
		betterEngineRate = betterEngine;
		// ride file and create buses.
		createAllBuses();
	}

	/**
	 * reading form file and create all the buses
	 */
	private void createAllBuses() {
		// read form file;
		// The test don't need cover the the following line because it is for
		// file reader. We are not responsible for IO test.
		BufferedReader br = null;
		try {
			String line;
			br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) {
				buildStore(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		buildBuses();

	}

	/**
	 * change one line form file into busName list and busStop list.
	 * 
	 * @param line
	 *            String form one line of file
	 */
	private void buildStore(String line) {
		Location loc;
		String[] splitLine = line.split(",");
		if (splitLine.length == 2) {
			storeBus.add(splitLine);
		} else {
			BusStop stop = new VertexStop();
			stop.setName(splitLine[0]);
			loc = new Location(Double.parseDouble(splitLine[1]),
					Double.parseDouble(splitLine[2]));
			stop.setLocation(loc);
			stop.setTime(Integer.parseInt(splitLine[splitLine.length - 1]));
			storeStops.add(stop);

		}
	}

	/**
	 * create buses with different type
	 */
	private void buildBuses() {
		String busName = new String();
		int stopNum;
		int lenth = 0;
		Bus bus;
		List<BusStop> stops;

		// build all the buses
		for (int i = 0; i < storeBus.size(); i++) {
			stops = new LinkedList<BusStop>();
			busName = storeBus.get(i)[0];
			stopNum = Integer.parseInt(storeBus.get(i)[1]);
			// add all stops into bus schedule
			for (int j = lenth; j < lenth + stopNum; j++) {
				stops.add(storeStops.get(j));
			}
			
			// calculate bus parameter and decide the bus property
			int peopleSpace = getRandomPeopleSpace();
			int luggageSpace = getRandomLuggageSpace();
			int doorNum = getRandomThreeDoors();
			boolean betterEngine = isBetterEngine();
			
			// create a bus
			if (betterEngine) {
				bus = new BusWithBetterEngine(new BasicBus());
			} else {
				bus = new BasicBus();
			}
			// set some value for a new bus
			bus.create(busName, stops, peopleSpace, luggageSpace, doorNum, simulation);
			buses.add(bus);
			lenth += stopNum;
		}
	}

	/**
	 * get the people space according to parameter: peopleSpaceRate
	 * @return int, size of people space
	 */
	private int getRandomPeopleSpace() {
		final int range = 10;
		Random generator = new Random();
		int i = generator.nextInt(range) + 1;
		if (i <= doubleSizeRate) {
			return onePeopleSpace * 2;
		} else {
			return onePeopleSpace;
		}
	}

	/**
	 * get luggage space according to parameter: luggageSpaceRate
	 * @return int, size of luggage space
	 */
	private int getRandomLuggageSpace() {
		final int range = 10;
		Random generator = new Random();
		int i = generator.nextInt(range) + 1;
		if (i <= doubleSizeRate) {
			return oneLuggageSpace * 2;
		} else {
			return oneLuggageSpace;
		}
	}

	/**
	 * get door number.
	 * @return int
	 */
	private int getRandomThreeDoors() {
		final int range = 10;
		Random generator = new Random();
		int i = generator.nextInt(range) + 1;
		if (i <= threeDoorsRate) {
			return normalDoorNum + 1;
		} else {
			return normalDoorNum;
		}
	}

	/**
	 * decide has better endine or not
	 * @return boolean
	 */
	private boolean isBetterEngine() {
		final int range = 10;
		Random generator = new Random();
		int i = generator.nextInt(range) + 1;
		if (i <= betterEngineRate) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void step() {
		int t = simulation.getTime();
		for (Bus bus : buses) {
			if (bus.getSchedule().get(0).getTime() == t) {
				// System.out.print(bus.getSchedule().get(0).getTime());
				// System.out.print(t);
				// System.out.println(bus.getName());
				simulation.insertBus(bus);
			}
		}
	}

}
