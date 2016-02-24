package edu.cmu.cs.cs214.hw4.core.Square;

import java.util.List;

import edu.cmu.cs.cs214.hw4.core.GameFlow;
import edu.cmu.cs.cs214.hw4.core.NormalTile;
import edu.cmu.cs.cs214.hw4.core.Position;


/**
 * DWSquare extends form Square, double word
 * @author ZhangYC
 *
 */
public class DWSquare extends Square{

	/**
	 * constructor
	 * 
	 * @param sqaureName
	 *            String the square's name
	 * @param p
	 *            the position of this square
	 * 
	 */
	public DWSquare(String sqaureName, Position p) {
		super(sqaureName, p);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int[] active(int[] modifier, int scoreIndex, int dwIndex, int twIndex, GameFlow gf){		
		List<NormalTile> tiles = gf.getNormalTiles();
		if (tiles.contains(super.getNormalTile())){
			modifier[scoreIndex] += super.getNormalTileValue();
			modifier[dwIndex] += 1;
		}else{
			modifier[scoreIndex] += super.getNormalTileValue();		
		}
		return modifier;	
	}

}
