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

public class GameTest {
	private GameFlow gf0;
	private GameFlow gf1;
	private GameFlow gf2;
	private GameFlow gf3;
	private GameFlow gfInValid;
	private Board board;
	private Game game;
	private Player p;

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

		NormalTile t41 = new NormalTile("a", 3);
		NormalTile t42 = new NormalTile("a", 1);
		NormalTile t43 = new NormalTile("a", 1);
		NormalTile t44 = new NormalTile("a", 1);

		gfInValid = new GameFlow();
		gfInValid.placeNormalTile(t42, p12);
		gfInValid.placeNormalTile(t41, p11);
		gfInValid.placeNormalTile(t43, p13);
		gfInValid.placeNormalTile(t44, p14);

		Position p41 = new Position(0, 0);
		Position p42 = new Position(0, 1);
		Position p43 = new Position(0, 2);
		Position p44 = new Position(0, 3);
		Position p45 = new Position(0, 4);

		gf3 = new GameFlow();
		gf3.placeNormalTile(t32, p42);
		gf3.placeNormalTile(t31, p41);
		gf3.placeNormalTile(t33, p43);
		gf3.placeNormalTile(t35, p45);
		gf3.placeNormalTile(t34, p44);

		board = new Board();
	}

	@Test
	public void testIsValid() {
		game = new Game(1);
		// in valid word
		assertTrue(game.isValid(gfInValid) == false);
		// in valid first round position
		assertTrue(game.isValid(gf2) == false);
		assertTrue(game.isValid(gf0) == true);
		game.move(gf0);

		// in valid connection
		assertTrue(game.isValid(gf2) == false);
		assertTrue(game.isValid(gf1));
		game.move(gf1);
		p = game.getCurrentPlayer();
		assertTrue(p.getScore() == 53);
		assertTrue(game.isValid(gf2));
		game.move(gf2);

		assertTrue(p.getScore() == 78);
	}

	@Test
	public void testMove() {
		game = new Game(1);
		p = game.getCurrentPlayer();
		game.move(gf0);
		assertTrue(p.getScore() == 44);
		game.move(gf1);
		assertTrue(p.getScore() == 53);
		game.move(gf2);
		assertTrue(p.getScore() == 78);
	}

	@Test
	public void testMoveToNextPlayer() {
		game = new Game(3);
		game.setBoard(board);

		p = game.getCurrentPlayer();
		assertTrue(p.getName().equals("0"));
		game.move(gf0);
		assertTrue(p.getScore() == 44);

		p = game.getCurrentPlayer();
		assertTrue(p.getName().equals("1"));
		game.move(gf1);
		assertTrue(p.getScore() == 39);

		p = game.getCurrentPlayer();
		assertTrue(p.getName().equals("2"));
		game.move(gf2);
		assertTrue(p.getScore() == 55);

		p = game.getCurrentPlayer();
		assertTrue(p.getName().equals("0"));
	}

	@Test
	public void testReverseEffect() {
		game = new Game(4);
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
	public void testBoomEffect() {
		game = new Game(1);
		game.setBoard(board);

		Player p = game.getPlayers().get(0);
		SpecialTile bomb = new Boom("boom", 10, p);
		Position position = new Position(9, 6);
		gf0.placeSpecialTile(bomb, position);

		game.move(gf0);
		game.move(gf1);
		game.move(gf2);

		assertTrue(p.getScore() == 56);
	}

	@Test
	public void testNectiveEffect() {
		game = new Game(1);
		game.setBoard(board);

		Player p = game.getPlayers().get(0);
		SpecialTile negative = new Negative("negtive", 6, p);
		Position position = new Position(6, 7);
		gf0.placeSpecialTile(negative, position);

		game.move(gf0);
		game.move(gf1);
		assertTrue(p.getScore() == 35);
	}

	@Test
	public void testMultipleSpecialEffect() {
		game = new Game(3);
		game.setBoard(board);

		Player p = game.getCurrentPlayer();
		SpecialTile negative = new Negative("negtive", 6, p);
		Position position = new Position(9, 5);
		gf0.placeSpecialTile(negative, position);
		game.move(gf0);

		p = game.getCurrentPlayer();
		SpecialTile bomb = new Boom("boom", 10, p);
		position = new Position(9, 6);
		gf1.placeSpecialTile(bomb, position);
		game.move(gf1);

		p = game.getCurrentPlayer();
		game.move(gf2);
		assertTrue(p.getScore() == 27);
	}

	@Test
	public void testMultipleSpecialEffectInSamePlace() {
		game = new Game(3);
		game.setBoard(board);

		Player p = game.getCurrentPlayer();
		SpecialTile negative = new Negative("negtive", 6, p);
		Position position = new Position(9, 6);
		SpecialTile myOwn = new MyOwn("myown", 10, p);
		gf0.placeSpecialTile(negative, position);
		gf0.placeSpecialTile(myOwn, position);
		game.move(gf0);

		assertTrue(game.isSpecialTileValid(gf0) == true);

		p = game.getCurrentPlayer();
		SpecialTile bomb = new Boom("boom", 10, p);
		position = new Position(9, 6);
		gf1.placeSpecialTile(bomb, position);
		game.move(gf1);

		p = game.getCurrentPlayer();
		game.move(gf2);
		assertTrue(p.getScore() == 30);
	}

	@Test
	public void testGameBegin() {
		game = new Game(3);
		game.startGame();
		Player p = game.getCurrentPlayer();
		assertTrue(p.getNumOfNormalTile() == 7);
	}

	@Test
	public void testExchange() {
		game = new Game(1);
		game.startGame();

		List<NormalTile> tiles1 = game.getCurrentPlayer().getNormalTiles();
		List<NormalTile> change = new ArrayList<NormalTile>();
		change.add(tiles1.get(0));
		change.add(tiles1.get(1));
		change.add(tiles1.get(5));
		game.exchange(change);
		assertTrue(tiles1.size() == 7);
	}

	@Test
	public void testBuySpecialTile() {
		game = new Game(1);
		p = game.getCurrentPlayer();
		game.buySpecialTile("boom");
		assertTrue(p.getSpecialTiles().size() == 1);
		game.move(gf0);
		game.buySpecialTile("boom");
		assertTrue(p.getSpecialTiles().size() == 2);
		
		assertTrue(p.getScore() == 24);

		game.buySpecialTile("reverse");
		assertTrue(p.getSpecialTiles().size() == 3);
		game.move(gf1);
		game.buySpecialTile("reverse");
		assertTrue(p.getSpecialTiles().size() == 4);

	}

	@Test
	public void testSpecialSquare() {
		game = new Game(1);
		p = game.getCurrentPlayer();

		game.move(gf3);
		assertTrue(p.getScore() == 54);
	}

	@Test
	public void testPlayer() {
		game = new Game(1);
		Player p = game.getCurrentPlayer();
		game.startGame();
		List<NormalTile> oldT = p.getNormalTiles();

		assertTrue(oldT.size() == 7);
		p.placeNormalTile(oldT.get(0), new Position(0, 0));
		p.placeNormalTile(oldT.get(0), new Position(0, 1));

		assertTrue(oldT.size() == 5);
		GameFlow gf = p.getGameFlow();
		assertTrue(gf.getNormalTiles().size() == 2);
		game.move(gf);
		assertTrue(oldT.size() == 7);
		gf = p.getGameFlow();
		assertTrue(gf.getNormalTiles().size() == 0);
		assertTrue(p.getNormalTiles().size() == 7);
	}
}
