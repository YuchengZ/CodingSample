package edu.cmu.cs.cs214.hw4.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Bag contains all the normalTile
 * 
 * @author ZhangYC
 *
 */
public class Bag {
	private List<NormalTile> normalTiles = new ArrayList<NormalTile>();

	private String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z" };
	// CHECKSTYLE:OFF
	private int[] counters = { 9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2,
			1, 6, 4, 6, 4, 2, 2, 1, 2, 1 };
	private int[] values = { 1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3,
			10, 1, 1, 1, 1, 4, 4, 8, 4, 10 };

	// CHECKSTYLE:ON

	/**
	 * the constructor of bag. all the tiles were build in it
	 */
	public Bag() {
		NormalTile nT;
		for (int i = 0; i < letters.length; i++) {
			String letter = letters[i];
			int count = counters[i];
			int value = values[i];
			for (int j = 0; j < count; j++) {
				nT = new NormalTile(letter, value);
				normalTiles.add(nT);
			}
		}

	}

	/**
	 * exchange normal tiles
	 * 
	 * @param nT
	 *            NormalTile the tiles that need to be change
	 * @return return a NormalTile to change which will replace the old tile
	 */
	public NormalTile exchange(NormalTile nT) {

		normalTiles.add(nT);
		NormalTile t = getRandomTile();
		// exchange the tile with same value
		while (t.getValue() != nT.getValue()) {
			t = getRandomTile();
		}
		normalTiles.remove(t);
		return t;
	}

	/**
	 * get a random tile form bag
	 * 
	 * @return A NormalTile randomly get form bag
	 */
	public NormalTile getRandomTile() {
		if (normalTiles.size() > 0) {
			Random rand = new Random();
			int index = rand.nextInt(normalTiles.size());
			return normalTiles.get(index);
		}
		return null;
	}

	/**
	 * return the size of normalTile inside the bag now
	 * 
	 * @return int the size of normalTile inside the bag now
	 */
	public int getNormalTileSize() {
		return normalTiles.size();
	}

}
