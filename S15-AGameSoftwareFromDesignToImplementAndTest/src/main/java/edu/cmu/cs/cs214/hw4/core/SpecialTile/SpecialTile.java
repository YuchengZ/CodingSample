package edu.cmu.cs.cs214.hw4.core.SpecialTile;

import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.Player;
import edu.cmu.cs.cs214.hw4.core.Position;

/**
 * specialTile
 * 
 * @author ZhangYC
 *
 */
public class SpecialTile {
	private int value;
	private String name;
	private Player player;

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
	public SpecialTile(String n, int d, Player p) {
		name = n;
		value = d;
		player = p;
	}

	/**
	 * get the cost
	 * 
	 * @return int value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * get player who placed special tile
	 * 
	 * @return Player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * the effect of specialTile before calculate the score
	 * 
	 * @param game
	 *            The Game players are playing
	 * @param p
	 *            position of this specailTile
	 */
	public void beforeEffect(Game game, Position p) {
		return;
	}

	/**
	 * the effect of specailTile after calcualte the score
	 * 
	 * @param game
	 *            The Game players are playing
	 * @param p
	 *            position of this specailTile
	 * @param score
	 *            int score before active this effect
	 * @return score int the score after this effect
	 */
	public int afterEffect(Game game, Position p, int score) {
		return score;
	}

	/**
	 * get specialTile's name
	 * 
	 * @return String specialTile's names
	 */
	public String getName() {
		return name;
	}

	/**
	 * the effect of specialTile before calculate the score
	 * 
	 * @param game
	 *            The Game players are playing
	 * @param position
	 *            position of this specailTile
	 */
	public void finalEffect(Game game, Position position) {
		return;
	}

}
