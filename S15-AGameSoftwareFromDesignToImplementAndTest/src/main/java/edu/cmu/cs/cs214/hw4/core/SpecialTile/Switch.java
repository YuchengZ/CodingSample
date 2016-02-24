package edu.cmu.cs.cs214.hw4.core.SpecialTile;

import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.Player;
import edu.cmu.cs.cs214.hw4.core.Position;

/**
 * Switch special tile extends from SpecialTile class
 * 
 * @author ZhangYC
 *
 */
public class Switch extends SpecialTile {

	/**
	 * constructor for switch
	 * 
	 * @param n
	 *            the name of the specialTile
	 * @param d
	 *            the cost of this specialTile
	 * @param p
	 *            the player place this tile
	 */
	public Switch(String n, int d, Player p) {
		super(n, d, p);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void finalEffect(Game game, Position p) {
		Player player = super.getPlayer();
		Player currentPlayer = game.getCurrentPlayer();

		int scoreCurrentPlayer = currentPlayer.getScore();
		int scorePlayer = player.getScore();

		// exchange the score
		player.subScore(scorePlayer);
		player.addScore(scoreCurrentPlayer);
		currentPlayer.subScore(scoreCurrentPlayer);
		currentPlayer.addScore(scorePlayer);
	}

}
