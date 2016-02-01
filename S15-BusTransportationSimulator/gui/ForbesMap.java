package edu.cmu.cs.cs214.hw3.gui;

import javax.swing.ImageIcon;

import edu.cmu.cs.cs214.hw3.Util;

/**
 * A map with the coordinates that surround the Forbes Ave corridor in Oakland
 *
 */
public class ForbesMap extends CityMap {

	private static final long serialVersionUID = -2974399220660259140L;
	private static final double START_LAT = 40.4531;
	private static final double START_LNG = -79.9633;
	private static final double END_LAT = 40.4361;
	private static final double END_LNG = -79.9367;

	private static final ImageIcon IMAGE = Util.loadImage("forbes.png");
	private static final int WIDTH = IMAGE.getIconWidth() / 2;
	private static final int HEIGHT = IMAGE.getIconHeight() / 2;

	/**
	 * Default constructor for the PittsburghMap class
	 */
	public ForbesMap() {
		super(WIDTH, HEIGHT, START_LAT, START_LNG, END_LAT, END_LNG);
	}

	@Override
	public ImageIcon getBackgroundImage() {
		return IMAGE;
	}


	@Override
	public String getName() {
		return "Forbes";
	}

}
