package edu.cmu.cs.cs214.hw3.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import edu.cmu.cs.cs214.hw3.Simulation;

/**
 * The setup of the {@link Simulation}'s UI, including the main panel, the
 * start/stop button, and the step button.
 */
public class SimulationUI {

	private static final String TITLE = "15-214 Simulation";
	private static final String START_BUTTON = "Start";
	private static final String STOP_BUTTON = "Stop";
	private static final String STEP_BUTTON = "Step";

	private final JFrame frame;
	private final JLabel stats;
	private final SimulationPanel simulationPanel;
	private final JButton stepButton;
	private final JButton runButton;

	/**
	 * Creates a GUI with the specified World.
	 *
	 * @param simulation
	 *            the Simulation drawn by the GUI
	 */
	public SimulationUI(Simulation simulation) {
		stats = new JLabel("Waiting for analysis", SwingConstants.CENTER);
		simulationPanel = new SimulationPanel(simulation, stats);
		runButton = new JButton(START_BUTTON);
		stepButton = new JButton(STEP_BUTTON);

		// Create the bottom panel and add buttons to it.
		JPanel bottom = new JPanel(new BorderLayout());
		bottom.add(runButton, BorderLayout.WEST);
		bottom.add(stats, BorderLayout.CENTER);
		bottom.add(stepButton, BorderLayout.EAST);

		stepButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				simulationPanel.step();
			}
		});

		runButton.addActionListener(new ActionListener() {
			private boolean toggle = true;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (toggle) {
					simulationPanel.start();
					stepButton.setEnabled(false);
					runButton.setText(STOP_BUTTON);
				} else {
					simulationPanel.stop();
					stepButton.setEnabled(true);
					runButton.setText(START_BUTTON);
				}
				toggle = !toggle;
			}
		});

		// Add the elements.
		frame = new JFrame(TITLE);
		Container pane = frame.getContentPane();
		pane.add(simulationPanel, BorderLayout.CENTER);
		pane.add(bottom, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
	}

	/**
	 * Sets the window to be visible if it wasn't before.
	 */
	public void show() {
		frame.setVisible(true);
	}

}
