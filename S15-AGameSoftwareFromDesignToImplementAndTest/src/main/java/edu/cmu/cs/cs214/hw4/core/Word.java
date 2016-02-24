package edu.cmu.cs.cs214.hw4.core;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.cs214.hw4.core.Square.Square;

/**
 * word class can calculate its own score
 * 
 * @author ZhangYC
 *
 */
public class Word {
	private List<Square> squares = new ArrayList<Square>();
	private static final int TREE = 3;

	/**
	 * add back part of the word
	 * 
	 * @param s
	 *            Square which build the word
	 */
	public void addBack(Square s) {
		squares.add(s);
	}

	/**
	 * add front part of the word
	 * 
	 * @param s
	 *            Square, which build the word
	 */
	public void addFront(Square s) {
		squares.add(0, s);
	}

	/**
	 * calculate the score of its own
	 * 
	 * @param gameFlow
	 *            The movement of this turn(it need to see if the special
	 *            squares(DL, DW, TL TW) on the word are in this turn or not)
	 * @return int score of the words
	 */
	public int getScore(GameFlow gameFlow) {
		/*
		 * modifier[0] means the current word score modifier[1] means the
		 * current double word multiple modifier[2] means the current triple
		 * word multiple pass this array in square.active and let square change
		 * the value according to different square
		 */

		int[] modifier = { 0, 0, 0 };
		for (Square s : squares) {
			modifier = s.active(modifier, 0, 1, 2, gameFlow);
		}
		int score = modifier[0];
		int dw = modifier[1];
		int tw = modifier[2];
		// multiply score
		score = (int) (score * Math.pow(2, dw) * Math.pow(TREE, tw));
		return score;
	}

	/**
	 * get size of the word(it was used when exclude one letter word)
	 * 
	 * @return int how many square the word has
	 */
	public int getSize() {
		return squares.size();
	}

	/**
	 * are two words same or not(combined with same square)
	 * 
	 * @param word
	 *            Word, compared with
	 * @return boolean, true if two words same
	 */
	public boolean isSameWith(Word word) {
		if (squares.size() == word.squares.size()) {
			for (int i = 0; i < squares.size(); i++) {
				if (!squares.get(i).equals(word.squares.get(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Square s : squares) {
			sb.append(s.getNormalTileName());
		}
		return sb.toString();
	}

}
