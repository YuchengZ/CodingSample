package edu.cmu.cs.cs214.hw4.core;

import java.util.List;

import edu.cmu.cs.cs214.hw4.core.SpecialTile.SpecialTile;

/**
 * Player Class
 * 
 * @author ZhangYC
 *
 */
public class Player {
	// the init score should be 0, but my special tile cost is 10 and player
	// cannot buy it when he do not have enough score. So i set init score to 30
	// which is easy to buy special tail
	private static final int INIT_SCORE = 30;
	private String name;
	private int score = INIT_SCORE;
	private Rack rack = new Rack();
	private GameFlow gf = new GameFlow();

	/**
	 * constructor
	 * 
	 * @param n
	 *            name of player
	 */
	public Player(String n) {
		name = n;
	}

	/**
	 * set GameFlow
	 * 
	 * @param gameFlow
	 *            the movement the player want to make
	 */
	public void setGameFlow(GameFlow gameFlow) {
		gf = gameFlow;
	}

	/**
	 * get the palyer's name
	 * 
	 * @return name String
	 */
	public String getName() {
		return name;
	}

	/**
	 * get the player's core
	 * 
	 * @return score int
	 */
	public int getScore() {
		return score;
	}

	/**
	 * get the player's gameFlow(movement)
	 * 
	 * @return GameFlow gf
	 */
	public GameFlow getGameFlow() {
		return gf;
	}

	/**
	 * get how many normalTile the player have, in order to refill the
	 * normalTile
	 * 
	 * @return number of normalTile
	 */
	public int getNumOfNormalTile() {
		return rack.getNumOfNormalTile();
	}

	/**
	 * get player's normalTile in rack(for testing)
	 * 
	 * @return An ArrayList of normalTIle
	 */
	public List<NormalTile> getNormalTiles() {
		return rack.getNormalTiles();
	}

	/**
	 * get player's specialTile in rack(for testing)
	 * 
	 * @return An ArrayList of SpecialTile
	 */
	public List<SpecialTile> getSpecialTiles() {
		return rack.getSpecialTiles();
	}

	/**
	 * add normalTile for player
	 * 
	 * @param nT
	 *            NormalTile will be added in player's rack
	 */
	public void addNormalTile(NormalTile nT) {
		rack.addNormalTile(nT);
	}

	/**
	 * add specialTile for player
	 * 
	 * @param sT
	 *            specailTile will be added
	 */
	public void addSpecialTile(SpecialTile sT) {
		rack.addSpecialTile(sT);
	}

	/**
	 * play place a normalTile on board
	 * 
	 * @param nT
	 *            NormalTile the player want to play
	 * @param p
	 *            Position the player want to play
	 */
	public void placeNormalTile(NormalTile nT, Position p) {
		if (rack.isOnRack(nT)) {
			gf.placeNormalTile(nT, p);
			rack.deleteNormalTile(nT);
		}
	}

	/**
	 * player place a specialTile on the board
	 * 
	 * @param sT
	 *            SpecialTile the player want to play
	 * @param p
	 *            the position the player want to put
	 */
	public void placeSpecialTile(SpecialTile sT, Position p) {
		if (rack.isOnRack(sT)) {
			gf.placeSpecialTile(sT, p);
			rack.deleteSpecialTile(sT);
		}
	}

	/**
	 * remove the normalTIle form rack,happens when exchange the tiles
	 * 
	 * @param nT
	 *            NormalTile player want to change
	 */
	public void deleteNormalTile(NormalTile nT) {
		if (rack.isOnRack(nT)) {
			rack.deleteNormalTile(nT);
		}
	}

	/**
	 * change the score by adding d
	 * 
	 * @param d
	 *            int how much it want to change the score
	 */
	public void addScore(int d) {
		score += d;
	}

	/**
	 * change the score by sub d(happens want buy specialTIle)
	 * 
	 * @param d
	 *            int the cost of the specailTIle
	 */
	public void subScore(int d) {
		score -= d;
	}

	/**
	 * how many special tile
	 * 
	 * @return int special tile number
	 */
	public int getNumofSpecialTile() {
		return rack.getNumOfSpecialTile();
	}

	/**
	 * retrieve all the movement in this turn
	 */
	public void retrieve() {
		List<NormalTile> normalTiles = gf.getNormalTiles();
		List<SpecialTile> specialTiles = gf.getSpecialTiles();
		for (NormalTile nT : normalTiles) {
			rack.addNormalTile(nT);
		}

		for (SpecialTile sT : specialTiles) {
			rack.addSpecialTile(sT);
		}

		gf = new GameFlow();
	}

}
