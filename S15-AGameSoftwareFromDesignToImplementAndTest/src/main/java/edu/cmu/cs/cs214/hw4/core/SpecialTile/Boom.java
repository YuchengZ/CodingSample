package edu.cmu.cs.cs214.hw4.core.SpecialTile;

import edu.cmu.cs.cs214.hw4.core.Board;
import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.Player;
import edu.cmu.cs.cs214.hw4.core.Position;

/**
 * Boom specialTile extends form SpecilTile
 * 
 * @author ZhangYC
 *
 */
public class Boom extends SpecialTile {
	private static final int SCALE = 3;

	/**
	 * constructor
	 * 
	 * @param n
	 *            The name of boom
	 * @param d
	 *            The value of boom
	 * @param p
	 *            which player put this one
	 */
	public Boom(String n, int d, Player p) {
		super(n, d, p);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void beforeEffect(Game game, Position p) {
		Board board = game.getBoard();
		int x = p.getX();
		int y = p.getY();

		for (int i = x - SCALE - 1; i < x + SCALE; i++) {
			for (int j = y - SCALE - 1; j < y + SCALE; j++) {
				if (board.isOnBoard(i, j)) {
					board.removeNormalTile(i, j);
				}
			}
		}
	}
}
