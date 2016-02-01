package edu.cmu.cs.cs214.hw3.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.Border;

import edu.cmu.cs.cs214.hw3.Entity;
import edu.cmu.cs.cs214.hw3.Simulation;
import edu.cmu.cs.cs214.hw3.Util;

/**
 * The main panel of the Simulation UI responsible for drawing its entities and
 * managing its steps.
 */
public class SimulationPanel extends JPanel {
	private static final long serialVersionUID = 4927764458527709056L;

	private static final int IMAGE_DEFAULT_SIZE = 6; // Pixels
	private static final int RUN_SPEED = 100; // Milliseconds
	private static final int DEFAULT_STEPS = 10;
	private static final int MAX_STEPS = 120;
	private static final int INCREMENT = 10;

	private final Simulation simulation;
	private CityMap currentMap;
	private final CityMap[] maps = { new PittsburghMap(), new OaklandMap(),
			new ForbesMap() };
	private final Timer timer;
	private final JLabel stats;
	private final JSlider slider;
	private int steps;
	private boolean renderLabels = false;

	/**
	 * Creates a panel drawn with the contents of the specified Simulation.
	 *
	 * @param pSimulation
	 *            the {@link} Simulation drawn by this panel
	 * @param pStats
	 *            The {@link JLabel} on which to draw the simulation analysis
	 */
	public SimulationPanel(Simulation pSimulation, JLabel pStats) {
		simulation = pSimulation;
		stats = pStats;
		steps = DEFAULT_STEPS;
		// Calculate the preferred width and height of the panel.
		currentMap = maps[0];
		slider = new JSlider(0, MAX_STEPS, DEFAULT_STEPS);
		initialize();
		timer = new Timer(RUN_SPEED, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				step();
			}
		});
	}

	/*
	 * Initializes the slider and the layout for the SimulationPanel
	 */
	private void initialize() {

		// Setup the panel and layout
		int panelWidth = currentMap.getWidth();
		int panelHeight = currentMap.getHeight();
		Dimension preferredSize = new Dimension(panelWidth, panelHeight);
		setPreferredSize(preferredSize);
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());

		// Add slider to the bottom
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new BorderLayout());
		slider.setLabelTable(slider.createStandardLabels(INCREMENT));
		slider.setPaintLabels(true);
		sliderPanel.add(new JLabel("Seconds per step: "), BorderLayout.WEST);
		sliderPanel.add(createRadioButtonGrouping(maps, "Zoom Level"),
				BorderLayout.EAST);
		sliderPanel.add(slider, BorderLayout.CENTER);
		bottomPanel.add(sliderPanel, BorderLayout.CENTER);
		bottomPanel.add(createLabelCheckbox(), BorderLayout.EAST);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		double imgWidth = (this.getWidth() * 1.0) / currentMap.getWidth();
		double imgHeight = (this.getHeight() * 1.0) / currentMap.getHeight();

		ImageIcon backgroundImg = currentMap.getBackgroundImage();
		graphics.drawImage(backgroundImg.getImage(), 0, 0, this.getWidth(),
				this.getHeight(), this);

		// Iterates through all of the entities and redraws them.
		for (Entity entity : simulation.getEntities()) {
			ImageIcon img = entity.getImage();
			if (img != null) {
				int x = (int) (currentMap.getXLocation(entity.getLocation()) * imgWidth);
				int y = (int) (currentMap.getYLocation(entity.getLocation()) * imgHeight);
				if (x >= 0 && y >= 0 && x <= this.getWidth()
						&& y <= this.getHeight()) {
					graphics.drawImage(img.getImage(), x, y,
							IMAGE_DEFAULT_SIZE, IMAGE_DEFAULT_SIZE, this);
					if (renderLabels)
						graphics.drawString(entity.getName(), x, y);
				}
			}
		}
	}

	private Component createRadioButtonGrouping(CityMap[] elements, String title) {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		// If title set, create titled border
		if (title != null) {
			Border border = BorderFactory.createTitledBorder(title);
			panel.setBorder(border);
		}
		// Create group
		ButtonGroup group = new ButtonGroup();
		JRadioButton aRadioButton;
		// For each String passed in:
		// Create button, add to panel, and add to group
		for (int i = 0, n = elements.length; i < n; i++) {
			final CityMap element = elements[i];
			aRadioButton = new JRadioButton(elements[i].getName());
			aRadioButton.setSelected(elements[i] == currentMap);
			panel.add(aRadioButton);
			group.add(aRadioButton);
			aRadioButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					currentMap = element;
					SimulationPanel.this.repaint();
				}
			});
		}
		return panel;
	}

	private Component createLabelCheckbox() {
		final JCheckBox checkbox = new JCheckBox("Show labels");
		checkbox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				renderLabels = checkbox.isSelected();
				SimulationPanel.this.repaint();
			}
		});
		return checkbox;
	}

	/**
	 * Runs the simulation. Continuously advances the simulation step-by-step
	 * until {@link SimulationPanel#stop()} is called.
	 */
	public void start() {
		timer.start();
	}

	/**
	 * Stops the simulation. If the simulation is running (because
	 * {@link SimulationPanel#start()} was called) then this will stop the
	 * continuous stepping.
	 */
	public void stop() {
		timer.stop();
	}

	/**
	 * Advances the simulation a variable number of steps and repaints the UI.
	 *
	 * @see Simulation#step()
	 */
	public void step() {
		steps = slider.getValue();
		for (int i = 0; i < steps; i++) {
			simulation.step();
		}
		stats.setText(Util.formatTime(simulation.getTime()) + ": "
				+ simulation.getAnalysisResult());
		repaint();
	}

}
