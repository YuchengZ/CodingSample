package edu.cmu.cs.cs214.hw4.core.Square;


import edu.cmu.cs.cs214.hw4.core.GameFlow;
import edu.cmu.cs.cs214.hw4.core.Position;


/**
 * NormalSquare extends form Square
 * @author ZhangYC
 *
 */
public class NormalSquare extends Square{

	/**
	 * constructor
	 * 
	 * @param sqaureName
	 *            String the square's name
	 * @param p
	 *            the position of this square
	 * 
	 */
	public NormalSquare(String sqaureName, Position p) {
		super(sqaureName, p);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int[] active(int[] modifier, int scoreIndex, int dwIndex, int twIndex, GameFlow gf){
		modifier[scoreIndex]+=super.getNormalTileValue();
		
		return modifier;	
	}

}
