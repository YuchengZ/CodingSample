package edu.cmu.cs.cs214.hw4.core.SpecialTile;

import java.util.Collections;
import java.util.List;

import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.Player;
import edu.cmu.cs.cs214.hw4.core.Position;

/**
 * Reverse specialTile extends form SpecialTile class
 * 
 * @author ZhangYC
 *
 */
public class Reverse extends SpecialTile {

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
	public Reverse(String n, int d, Player p) {
		super(n, d, p);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void beforeEffect(Game game, Position p) {
		List<Player> players = game.getPlayers();
		Collections.reverse(players);
	}

}
