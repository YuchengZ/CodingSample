package edu.cmu.cs.cs214.hw4.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import edu.cmu.cs.cs214.hw4.core.SpecialTile.SpecialTile;

/**
 * GameFlow, represent a movement made by currentPlayer
 * 
 * @author ZhangYC
 *
 */
public class GameFlow {
	private List<NormalTile> normalTiles;
	private List<Position> normalPositions;
	private List<SpecialTile> specialTiles;
	private List<Position> specialPositions;
	/*
	 * because we may change the position order later, so we use map to build a
	 * strong relationship between tiles and positions.
	 */
	private HashMap<Position, NormalTile> normalMap = new HashMap<Position, NormalTile>();
	private HashMap<Position, SpecialTile> specialMap = new HashMap<Position, SpecialTile>();

	/**
	 * constructor,create for lists to store the movement
	 */
	public GameFlow() {
		normalTiles = new ArrayList<NormalTile>();
		normalPositions = new ArrayList<Position>();
		specialTiles = new ArrayList<SpecialTile>();
		specialPositions = new ArrayList<Position>();
	}

	/**
	 * player place a normal tile on board
	 * 
	 * @param tile
	 *            NormalTile the tile that player want to put
	 * @param p
	 *            Position, the position the player want this tile to be
	 */
	public void placeNormalTile(NormalTile tile, Position p) {
		normalTiles.add(tile);
		normalPositions.add(p);
		normalMap.put(p, tile);
	}

	/**
	 * when player move back the normal tile form the board(maybe he didn't want
	 * to use this normal tile now)
	 * 
	 * @param tile
	 *            NormalTile the tile player want to put back
	 * @param p
	 *            Position, the place of this tile on board
	 */
	public void deleteNormalTile(NormalTile tile, Position p) {
		normalTiles.remove(tile);
		normalPositions.remove(p);
		normalMap.remove(p);
	}

	/**
	 * player place a special tile on board
	 * 
	 * @param tile
	 *            SpecialTile the specialTile the player want to play
	 * @param p
	 *            Position, the position the player want to put
	 */
	public void placeSpecialTile(SpecialTile tile, Position p) {
		specialTiles.add(tile);
		specialPositions.add(p);
		specialMap.put(p, tile);
	}

	/**
	 * when player move back the special tile form the board(maybe he didn't
	 * want to use this special tile now)
	 * 
	 * @param tile
	 *            SpecialTile the tile player want to put back
	 * @param p
	 *            Position, the place of this tile on board
	 */
	public void deleteSpecialTile(SpecialTile tile, Position p) {
		specialTiles.remove(tile);
		specialPositions.remove(p);
		specialMap.remove(p);
	}

	/**
	 * get normalTiles
	 * 
	 * @return a Arraylist of NormalTile
	 */
	public List<NormalTile> getNormalTiles() {
		return normalTiles;
	}

	/**
	 * get specailTiles
	 * 
	 * @return an ArrayList of SpecialTiles
	 */
	public List<SpecialTile> getSpecialTiles() {
		return specialTiles;
	}

	/**
	 * get normalTiles'positions
	 * 
	 * @return an ArrayList of position
	 */
	public List<Position> getNormalPosition() {
		return normalPositions;
	}

	/**
	 * get specialTiles'positions
	 * 
	 * @return an ArrayList of positions
	 */
	public List<Position> getSpecialPosition() {
		return specialPositions;
	}

	/**
	 * get hashMap of normalTile and normalPosition
	 * 
	 * @return A HashMap with position as key
	 */
	public HashMap<Position, NormalTile> getNormalMap() {
		return normalMap;
	}

	/**
	 * get hashMap of specialTile and specialPosition
	 * 
	 * @return A HashMap with position as key
	 */
	public HashMap<Position, SpecialTile> getSpecialMap() {
		return specialMap;
	}

	/**
	 * get how many noramlTiels the play want to play
	 * 
	 * @return int, how many noramlTiels the play want to play
	 */
	public int getNormalSize() {
		return normalPositions.size();
	}

	/**
	 * check if there is same location in normal tiles'positions
	 * 
	 * @return boolean, true if there is no same position, false otherwise
	 */
	public boolean notSamePosition() {
		for (int i = 0; i < normalPositions.size(); i++) {
			for (int j = i + 1; j < normalPositions.size(); j++) {
				if (normalPositions.get(i).isSame(normalPositions.get(j))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * check if the positions of normalTiles are in same row or col
	 * 
	 * @return boolean, true if they are in same row or col
	 */
	public boolean sameRowOrCol() {
		// player must place normalTile if he press play button.
		if (normalPositions.size() == 0){
			return false;
		}
		int x = normalPositions.get(0).getX();
		int y = normalPositions.get(0).getY();
		boolean sameRow = true;
		boolean sameCol = true;
		// check same row
		for (Position p : normalPositions) {
			if (p.getX() != x) {
				sameRow = false;
				break;
			}
		}
		// check same col
		for (Position p : normalPositions) {
			if (p.getY() != y) {
				sameCol = false;
				break;
			}
		}
		return sameRow || sameCol;
	}

	/**
	 * sort the position by x and y.
	 */
	public void sortNormalPosition() {
		Collections.sort(normalPositions, new Comparator<Position>() {
			public int compare(Position p1, Position p2) {
				if (p1.getX() != p2.getX()) {
					return p1.getX() - p2.getX();
				}
				return p1.getY() - p2.getY();
			}
		});
	}

	/**
	 * check if the player only place one normalTile(It is for first round
	 * check)
	 * 
	 * @return boolean true if there if only one normalTile
	 */
	public boolean isOneNormalTile() {
		return normalPositions.size() == 1;
	}

}
