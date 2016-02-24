package edu.cmu.cs.cs214.hw4.core;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.cmu.cs.cs214.hw4.core.SpecialTile.Boom;
import edu.cmu.cs.cs214.hw4.core.SpecialTile.MyOwn;
import edu.cmu.cs.cs214.hw4.core.SpecialTile.Negative;
import edu.cmu.cs.cs214.hw4.core.SpecialTile.Reverse;
import edu.cmu.cs.cs214.hw4.core.SpecialTile.SpecialTile;
import edu.cmu.cs.cs214.hw4.core.Square.Square;

public class BoardTest {
	private GameFlow gf0;
	private GameFlow gf1;
	private GameFlow gf2;
	private Board board;
	private Game game;

	@Before
	public void setUp() {
		// first game flow
		NormalTile t11 = new NormalTile("h", 4);
		NormalTile t12 = new NormalTile("o", 1);
		NormalTile t13 = new NormalTile("r", 1);
		NormalTile t14 = new NormalTile("n", 1);

		Position p11 = new Position(7, 5);
		Position p12 = new Position(7, 6);
		Position p13 = new Position(7, 7);
		Position p14 = new Position(7, 8);

		gf0 = new GameFlow();
		gf0.placeNormalTile(t11, p11);
		gf0.placeNormalTile(t13, p13);
		gf0.placeNormalTile(t14, p14);
		gf0.placeNormalTile(t12, p12);

		// second game flow
		NormalTile t21 = new NormalTile("a", 1);
		NormalTile t22 = new NormalTile("f", 4);
		NormalTile t23 = new NormalTile("m", 3);

		Position p21 = new Position(6, 7);
		Position p22 = new Position(5, 7);
		Position p23 = new Position(8, 7);

		gf1 = new GameFlow();
		gf1.placeNormalTile(t21, p21);
		gf1.placeNormalTile(t22, p22);
		gf1.placeNormalTile(t23, p23);

		// third game flow
		NormalTile t31 = new NormalTile("p", 3);
		NormalTile t32 = new NormalTile("a", 1);
		NormalTile t33 = new NormalTile("s", 1);
		NormalTile t34 = new NormalTile("t", 1);
		NormalTile t35 = new NormalTile("e", 1);

		Position p31 = new Position(9, 5);
		Position p32 = new Position(9, 6);
		Position p33 = new Position(9, 7);
		Position p34 = new Position(9, 8);
		Position p35 = new Position(9, 9);

		gf2 = new GameFlow();
		gf2.placeNormalTile(t32, p32);
		gf2.placeNormalTile(t31, p31);
		gf2.placeNormalTile(t33, p33);
		gf2.placeNormalTile(t35, p35);
		gf2.placeNormalTile(t34, p34);

		board = new Board();
	}

	@Test
	public void testBuildBoard() {
		List<List<Square>> s = board.getSquares();
		for (List<Square> l : s) {
			for (Square square : l) {
				System.out.print(square.getName()+" ");
			}
			System.out.print("\n");
		}
	}

	@Test
	public void testIsValidPosition() {
		assertTrue(board.isValid(gf0, 0) == true);
		assertTrue(board.isValid(gf1, 1) == false);
		assertTrue(board.isValid(gf2, 1) == false);
		assertTrue(board.isValid(gf2, 0) == false);

		board.placeNormalTile(gf0);
		assertTrue(board.isValid(gf1, 1) == true);
		assertTrue(board.isValid(gf2, 1) == false);

		board.placeNormalTile(gf1);
		assertTrue(board.isValid(gf2, 1) == true);
	}

	@Test
	public void testGetWordInString() {
		List<String> words = board.getWords(gf0);
		assertTrue(words.size() == 4);
		assertTrue(words.get(0).equals("horn"));

		gf1.sortNormalPosition();
		board.placeNormalTile(gf0);
		words = board.getWords(gf1);
		assertTrue(words.size() == 3);
		assertTrue(words.get(2).equals("farm"));

		gf2.sortNormalPosition();
		board.placeNormalTile(gf1);
		words = board.getWords(gf2);
		assertTrue(words.size() == 6);
		assertTrue(words.get(0).equals("paste"));
		assertTrue(words.get(3).equals("farms"));
	}

	@Test
	public void testGetWordInWord() {
		gf0.sortNormalPosition();
		board.placeNormalTile(gf0);
		List<Word> words = board.findNewWordsOnBoard(gf0);
		assertTrue(words.get(0).toString().equals("horn"));
		assertTrue(words.size() == 1);

		gf1.sortNormalPosition();
		board.placeNormalTile(gf1);
		words = board.findNewWordsOnBoard(gf1);
		assertTrue(words.size() == 1);
		assertTrue(words.get(0).toString().equals("farm"));
		
		gf2.sortNormalPosition();
		board.placeNormalTile(gf2);
		words = board.findNewWordsOnBoard(gf2);
		assertTrue(words.size() == 2);
		assertTrue(words.get(0).toString().equals("paste"));
		assertTrue(words.get(1).toString().equals("farms"));

	}
	
	@Test
	public void testIsValidSpecial(){
		game = new Game(1);

		Player p = game.getPlayers().get(0);
		
		SpecialTile bomb = new Boom("boom", 10, p);
		Position position = new Position(9, 6);
		gf0.placeSpecialTile(bomb, position);
		
		// first move the board is empty
		assertTrue(board.isSpecialTileValid(gf0));
		
		SpecialTile reverse = new Boom("reverse", 10, p);
		position = new Position(7, 7);
		gf1.placeSpecialTile(reverse, position);
		
		board.placeNormalTile(gf0);
		assertTrue(board.isSpecialTileValid(gf1) == false);
	}

	@Test
	public void testCalculateScore() {
		gf0.sortNormalPosition();
		board.placeNormalTile(gf0);
		List<Word> words = board.findNewWordsOnBoard(gf0);
		assertTrue(words.get(0).getScore(gf0) == 14);
		assertTrue (board.calculateScore(gf0) == 14);
		
		gf1.sortNormalPosition();
		board.placeNormalTile(gf1);
		words = board.findNewWordsOnBoard(gf1);
		assertTrue(words.get(0).getScore(gf1) == 9);
		assertTrue(board.calculateScore(gf1) == 9);
		
		gf2.sortNormalPosition();
		board.placeNormalTile(gf2);
		words = board.findNewWordsOnBoard(gf2);
		assertTrue(board.calculateScore(gf2) == 25);
	}
	
	@Test
	public void testBoomEffect(){
		game = new Game(1);
		game.setBoard(board);
		
		Player p = game.getPlayers().get(0);
		SpecialTile bomb = new Boom("boom", 10, p);
		Position position = new Position(9, 6);
		gf0.placeSpecialTile(bomb, position);
		
		assertTrue(board.isSpecialTileValid(gf0));
		
		board.placeNormalTile(gf0);
		board.placeSpecialTile(gf0);
		board.placeNormalTile(gf1);
		board.placeNormalTile(gf2);
		
		assertTrue(board.hasNormalTile(9, 6) == true);
		assertTrue(board.hasNormalTile(9, 5) == true);
		assertTrue(board.hasNormalTile(9, 7) == true);
		assertTrue(board.hasNormalTile(9, 8) == true);
		assertTrue(board.hasNormalTile(8, 7) == true);
		assertTrue(board.hasNormalTile(7, 7) == true);
		assertTrue(board.hasNormalTile(7, 6) == true);
		bomb.beforeEffect(game, position);
		assertTrue(board.hasNormalTile(9, 6) == false);
		assertTrue(board.hasNormalTile(9, 5) == false);
		assertTrue(board.hasNormalTile(9, 7) == false);
		assertTrue(board.hasNormalTile(9, 8) == false);
		assertTrue(board.hasNormalTile(8, 7) == false);
		assertTrue(board.hasNormalTile(7, 7) == false);
		assertTrue(board.hasNormalTile(7, 6) == false);
	}
	
	@Test
	public void testBoomEffectByBoard(){
		game = new Game(1);
		game.setBoard(board);
		
		Player p = game.getPlayers().get(0);
		SpecialTile bomb = new Boom("boom", 10, p);
		Position position = new Position(9, 6);
		gf0.placeSpecialTile(bomb, position);
		
		assertTrue(board.isSpecialTileValid(gf0));
		
		board.placeNormalTile(gf0);
		board.placeSpecialTile(gf0);
		board.placeNormalTile(gf1);
		board.placeNormalTile(gf2);
		
		board.activeBeforeEffect(game, gf2);
		assertTrue(board.calculateScore(gf2)==3);	
	}
	
	@Test
	public void testNegtiveEffect(){
		game = new Game(1);
		game.setBoard(board);
		
		Player p = game.getPlayers().get(0);
		SpecialTile negative = new Negative("negtive", 6, p);
		Position position = new Position(6, 7);
		gf0.placeSpecialTile(negative, position);
		
		board.placeNormalTile(gf0);
		board.placeSpecialTile(gf0);
		
		board.placeNormalTile(gf1);
		board.activeBeforeEffect(game, gf1);
		int score = board.calculateScore(gf1);
		score = board.activeAfterEffect(game, gf1, score);
		assertTrue(score == -9);
	}
	
	@Test
	public void testReverseEffect(){
		game =  new Game(4);
		game.setBoard(board);
		
		Player p = game.getPlayers().get(0);
		SpecialTile reverse = new Reverse("reverse", 6, p);
		Position position = new Position(6, 7);
		gf0.placeSpecialTile(reverse, position);
		
		game.move(gf0);
		board.placeNormalTile(gf1);
		board.activeBeforeEffect(game, gf1);
		List<Player> ps = game.getPlayers();
		assertTrue(ps.get(3).getName().equals("0"));
		assertTrue(ps.get(2).getName().equals("1"));
		assertTrue(ps.get(1).getName().equals("2"));
		assertTrue(ps.get(0).getName().equals("3"));
	}
	
	@Test
	public void testMyOwnEffect(){
		game = new Game(1);
		game.setBoard(board);
		
		Player p = game.getPlayers().get(0);
		SpecialTile myOwn = new MyOwn("myOwn", 6, p);
		Position position = new Position(6, 7);
		gf0.placeSpecialTile(myOwn, position);
		
		board.placeNormalTile(gf0);
		board.placeSpecialTile(gf0);
		
		board.placeNormalTile(gf1);
		board.activeBeforeEffect(game, gf1);
		int score = board.calculateScore(gf1);
		score = board.activeAfterEffect(game, gf1, score);
		assertTrue(score == 0);
	}
	

}
