package edu.cmu.cs.cs214.hw4.core;

/**
 * GameChangeListener
 * 
 * @author ZhangYC
 *
 */
public interface GameChangeListener {

	/**
	 * change the GUI when player make move
	 */
	void makeMoveChanged();

	/**
	 * change the GUI when player place a tiles
	 */
	void gameFlowChanged();

	/**
	 * change the GUI when player buy a specialTiles
	 */
	void buySpecialTileChanged();

	/**
	 * change the GUI when current player changed
	 */
	void currentPlayerChanged();

	/**
	 * change the GUI when game is overs
	 */
	void gameOverChanged();

	/**
	 * change the GUI when player retrieve the movement
	 */
	void retrieveChanged();

}
