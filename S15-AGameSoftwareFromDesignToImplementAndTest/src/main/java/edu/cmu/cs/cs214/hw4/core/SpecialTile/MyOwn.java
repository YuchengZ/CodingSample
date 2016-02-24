package edu.cmu.cs.cs214.hw4.core.SpecialTile;

import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.Player;
import edu.cmu.cs.cs214.hw4.core.Position;

/**
 * NyOwb specialTile extends form SpecailTile class
 * 
 * @author ZhangYC
 *
 */
public class MyOwn extends SpecialTile {

	/**
	 * constructor
	 * 
	 * @param n
	 *            String The name
	 * @param d
	 *            int The value, cost if one want's to buy it
	 * @param p
	 *            which player put this one
	 */
	public MyOwn(String n, int d, Player p) {
		super(n, d, p);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int afterEffect(Game game, Position p, int score) {
		return 0;
	}

}
