package edu.cmu.cs.cs214.hw4.core;

/**
 * the postion on board
 * 
 * @author ZhangYC
 *
 */
public class Position {
	private int x;
	private int y;

	/**
	 * constructor
	 * 
	 * @param pX
	 *            row index of the board
	 * @param pY
	 *            col index of the board
	 */
	public Position(int pX, int pY) {
		x = pX;
		y = pY;
	}

	/**
	 * get row index
	 * 
	 * @return int row index
	 */
	public int getX() {
		return x;
	}

	/**
	 * get col index
	 * 
	 * @return itn col index
	 */
	public int getY() {
		return y;
	}

	/**
	 * check if it is the same location
	 * 
	 * @param pX
	 *            row index int
	 * @param pY
	 *            col index int
	 * @return boolean true if the x and y are equal
	 */
	public boolean isSame(int pX, int pY) {
		return (pX == x && pY == y);
	}

	/**
	 * overload, check if two positions are same or not
	 * 
	 * @param p
	 *            Position
	 * @return boolean true if two position are same
	 */
	public boolean isSame(Position p) {
		return x == p.x && y == p.y;
	}
}
