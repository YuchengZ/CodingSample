package edu.cmu.cs.cs214.hw3.simulation;

import javax.swing.SwingUtilities;

import edu.cmu.cs.cs214.hw3.BusFactory;
import edu.cmu.cs.cs214.hw3.PersonFactory;
import edu.cmu.cs.cs214.hw3.Simulation;
import edu.cmu.cs.cs214.hw3.Simulator;
import edu.cmu.cs.cs214.hw3.gui.SimulationUI;
import edu.cmu.cs.cs214.hw3.gui.TextUI;

/**
 * A Main class initializes a simulation and runs it.
 */
public class Simulation2 {
	private Simulation s;
	private PersonFactory pf;
	private BusFactory bf;
	private final int totalRider = 30;
	private static final String ROUTES_FILE_NAME = "src/main/resources/oakland_stop_times.txt";

	/**
	 * Starts up the simulation and the GUI representation
	 *
	 * @param args
	 *            Command line arguments
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Simulation2().createAndShowSimulation();
			}
		});
	}

	/**
	 * Sets up the simulation and starts the GUI
	 */
	public void createAndShowSimulation() {
		// parameters for different kinds of people
		final int wheelChair = 10; // how many people in wheelChair when there is
									// 10 people
		final int payInCash = 10; // how many people pay with cash when there is
									// 10 people
		final int luggage = 10; // how many people has one luggage when there is
								// 10 people
		final int groupPeople = 0; // how many group people to create when
									// create 10 people

		// parameter for different kinds of buses
		final int doubleSizeRate = 10; // how many bus has double size in 10
										// buses.
		final int threeDoorsRate = 10;// how many bus has 3 doors in 10 buses.
		final int betterEngineRate = 0; // how many bus has better engine that
											// could speed up in 10 buses.

		// parameter for environment
		boolean badWeather = false; // badWeather and trafic problem are
									// similar. Just set different delay rate
		boolean wait = true; // if the driver ahead of schedule, wait until the
						// schedule time or not

		s = new Simulator(badWeather, wait);
		pf = new PersonFactory(s, totalRider, wheelChair, payInCash, luggage,
				groupPeople);
		bf = new BusFactory(s, ROUTES_FILE_NAME, doubleSizeRate,
				threeDoorsRate, betterEngineRate);
		s.addBusFactory(bf);
		s.addPersonFactory(pf);

		SimulationUI uis = new SimulationUI(s);
		TextUI uit = new TextUI(s);

		// comment/uncomment these to change SimulationUI to TextUI
		uis.show();
		//uit.simulate();

	}

}
