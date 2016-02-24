package edu.cmu.cs.cs214.hw4.core.Square;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.GameFlow;
import edu.cmu.cs.cs214.hw4.core.NormalTile;
import edu.cmu.cs.cs214.hw4.core.Player;
import edu.cmu.cs.cs214.hw4.core.Position;
import edu.cmu.cs.cs214.hw4.core.SpecialTile.SpecialTile;

/**
 * the square on the board
 * 
 * @author ZhangYC
 *
 */
public class Square {
	private NormalTile normalTile;
	private List<SpecialTile> specialTiles;
	private String name;
	private Position position;

	/**
	 * constructor
	 * 
	 * @param sqaureName
	 *            String the square's name
	 * @param p
	 *            the position of this square
	 * 
	 */
	public Square(String sqaureName, Position p) {
		name = sqaureName;
		normalTile = null;
		specialTiles = new ArrayList<SpecialTile>();
		position = p;
	}

	/**
	 * put a normalTile into the square
	 * 
	 * @param tile
	 *            NormalTile puts on the square
	 */
	public void setNormalTile(NormalTile tile) {
		normalTile = tile;
	}

	/**
	 * put a specialTile into the sqaure
	 * 
	 * @param tile
	 *            SpecialTile
	 */
	public void addSpecialTile(SpecialTile tile) {
		specialTiles.add(tile);
	}

	/**
	 * get normalTile's name in this square
	 * 
	 * @return String name of normalTIle
	 */
	public String getNormalTileName() {
		return normalTile.getName();
	}

	/**
	 * get normalTile's valid
	 * 
	 * @return int normalTile's value
	 */
	public int getNormalTileValue() {
		return normalTile.getValue();
	}

	/**
	 * get noramlTile (used for its sub class)
	 * 
	 * @return normalTile in this square
	 */
	public NormalTile getNormalTile() {
		return normalTile;
	}

	/**
	 * if this square has normalTile or not
	 * 
	 * @return boolean true is has
	 */
	public boolean hasNormalTile() {
		if (normalTile == null) {
			return false;
		}
		return true;
	}

	/**
	 * get specialTile lists
	 * 
	 * @return A list of specailTile in this square
	 */
	public List<SpecialTile> getSpecialTiles() {
		return specialTiles;
	}

	/**
	 * get name of square( used to print the board to check)
	 * 
	 * @return name of the square
	 */
	public String getName() {
		return name;
	}

	/**
	 * remove normalTilr form this square
	 */
	public void removeNormalTile() {
		normalTile = null;
	}

	/**
	 * remove special tile form this square
	 */
	public void removeSpecialTile() {
		specialTiles = new ArrayList<SpecialTile>();
	}

	/**
	 * active the square function when calculate the score
	 * 
	 * @param modifier
	 *            an array of int(score, dw modifier and tw modifer)
	 * @param scoreIndex
	 *            int the index of score in modifier.
	 * @param dwIndex
	 *            int the index of dw in modifier
	 * @param twIndex
	 *            int the index of tw in modifier.
	 * @param gameFlow
	 *            movement made by player in this turn
	 * @return modifier an array of int which has been changed by the square
	 */
	public int[] active(int[] modifier, int scoreIndex, int dwIndex,
			int twIndex, GameFlow gameFlow) {
		return modifier;
	}

	/**
	 * active specialTile's before calculate effect
	 * 
	 * @param game
	 *            Game, the game players are playing
	 */
	public void activeBeforeEffect(Game game) {
		for (SpecialTile s : specialTiles) {
			s.beforeEffect(game, position);
		}
	}

	/**
	 * after calculate score, active specialTile's afterEffect
	 * 
	 * @param game
	 *            Game, the game players are playing
	 * @param score
	 *            int, score before active afterEffect
	 * @return the score after active specialTile's afterEffect
	 */
	public int activeAfterEffect(Game game, int score) {
		for (SpecialTile s : specialTiles) {
			score = s.afterEffect(game, position, score);
		}
		return score;
	}

	/**
	 * if the square has special tile of a player
	 * 
	 * @param p
	 *            the Player
	 * @return boolean true of player has this specialTile, false otherwise
	 */
	public boolean hasSpecialTileOfPlayer(Player p) {
		for (SpecialTile t : specialTiles) {
			if (t.getPlayer() == p) {
				return true;
			}
		}
		return false;
	}

	/**
	 * active specialTile's after update scores
	 * 
	 * @param game
	 *            Game, the game players are playing
	 */
	public void activeFinalEffect(Game game) {
		for (SpecialTile s : specialTiles) {
			s.finalEffect(game, position);
		}

	}

}
