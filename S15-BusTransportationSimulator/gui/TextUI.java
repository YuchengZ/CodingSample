package edu.cmu.cs.cs214.hw3.gui;

import edu.cmu.cs.cs214.hw3.Simulation;
import edu.cmu.cs.cs214.hw3.Util;

/**
 * This interface runs the simulation automatically and prints the end result.
 */
public class TextUI {


    private final Simulation simulation;

    /**
     * Creates a simulation.
     *
     * @param asimulation the Simulation drawn by the GUI
     */
    public TextUI(Simulation asimulation) {
        this.simulation = asimulation;
    }

    /**
     * Runs the entire simulation
     */
    public void simulate() {
        while (simulation.getTime() < Util.SECONDS_PER_DAY) {
            simulation.step();
            if (simulation.getTime() % Util.SECONDS_PER_HOUR == 0)
                System.out.println("Time: " + Util.formatTime(simulation.getTime()));
        }
        System.out.println("Analysis result:\n" + simulation.getAnalysisResult());
    }

}
