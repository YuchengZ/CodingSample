package edu.cmu.cs.cs214.hw4.core;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class GameFlowTest {
	private GameFlow gf0;
	private GameFlow gfInValid;
	
	@Before
	public void setUp() {
		// first game flow
		NormalTile t11 = new NormalTile("h", 8);
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
	}
	
	@Test 
	public void testIsValidPositionGF(){
		NormalTile t1 = new NormalTile("a", 1);
		NormalTile t2 = new NormalTile("a", 1);
		NormalTile t3 = new NormalTile("a", 1);
		NormalTile t4 = new NormalTile("a", 1);
		
		Position p1 = new Position(1, 1);
		Position p2 = new Position(2, 1);
		Position p3 = new Position(1, 1);
		Position p4 = new Position(2, 3);
		
		gfInValid = new GameFlow();
		gfInValid.placeNormalTile(t1, p1);
		gfInValid.placeNormalTile(t2, p3);
		gfInValid.placeNormalTile(t3, p2);
		gfInValid.placeNormalTile(t4, p4);
		
		assertTrue(gf0.notSamePosition() == true);		
		assertTrue(gf0.sameRowOrCol() == true);
		assertTrue(gfInValid.notSamePosition()== false);
		assertTrue(gfInValid.sameRowOrCol()== false);	
	}

}
