package edu.cmu.cs.cs214.hw4.core;

/**
 * NormalTile
 * 
 * @author ZhangYC
 *
 */
public class NormalTile {
	private String name;
	private int value;

	/**
	 * constructor
	 * 
	 * @param s
	 *            String, name of normalTile
	 * @param b
	 *            int, valid of normalTile
	 */
	public NormalTile(String s, int b) {
		name = s;
		value = b;
	}

	/**
	 * get the name if normal tile
	 * 
	 * @return String the name of normal tile
	 */
	public String getName() {
		return name;
	}

	/**
	 * get the value of normal tile
	 * 
	 * @return int the value of normal tile
	 */
	public int getValue() {
		return value;
	}
}
