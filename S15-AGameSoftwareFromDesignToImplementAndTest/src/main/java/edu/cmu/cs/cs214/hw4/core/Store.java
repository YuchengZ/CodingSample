package edu.cmu.cs.cs214.hw4.core;

import edu.cmu.cs.cs214.hw4.core.SpecialTile.Boom;
import edu.cmu.cs.cs214.hw4.core.SpecialTile.MyOwn;
import edu.cmu.cs.cs214.hw4.core.SpecialTile.Negative;
import edu.cmu.cs.cs214.hw4.core.SpecialTile.Reverse;
import edu.cmu.cs.cs214.hw4.core.SpecialTile.SpecialTile;
import edu.cmu.cs.cs214.hw4.core.SpecialTile.Switch;

/**
 * store for buying specialTIle
 * 
 * @author ZhangYC
 *
 */
public class Store {
	private static final int NGTV_COST = 10;
	private static final int RVRS_COST = 10;
	private static final int BOOM_COST = 10;
	private static final int MYWN_COST = 10;
	private static final int SWCH_COST = 10;

	/**
	 * buy special tile
	 * 
	 * @param player
	 *            Player who want to buy the specialTile
	 * @param name
	 *            String the name of the specialTile the person want to buy
	 */
	public void buySpecialTile(Player player, String name) {
		if (name.equals("negative")) {
			buyNegative(player, name);
		} else if (name.equals("reverse")) {
			buyReverse(player, name);
		} else if (name.equals("boom")) {
			buyBoom(player, name);
		} else if (name.equals("myown")) {
			buyMyOwn(player, name);
		} else if (name.equals("switch")) {
			buySwitch(player, name);
		}
	}

	private void buySwitch(Player player, String name) {
		if (player.getScore() >= SWCH_COST){
			SpecialTile sT = new Switch(name, SWCH_COST, player);
			player.subScore(SWCH_COST);
			player.addSpecialTile(sT);
		}
		
	}

	private void buyNegative(Player player, String name) {
		if (player.getScore() >= NGTV_COST) {
			SpecialTile sT = new Negative(name, NGTV_COST, player);
			player.subScore(NGTV_COST);
			player.addSpecialTile(sT);
		}
	}

	private void buyReverse(Player player, String name) {
		if (player.getScore() >= RVRS_COST) {
			SpecialTile sT = new Reverse(name, RVRS_COST, player);
			player.subScore(RVRS_COST);
			player.addSpecialTile(sT);
		}
	}

	private void buyBoom(Player player, String name) {
		if (player.getScore() >= BOOM_COST) {
			SpecialTile sT = new Boom(name, BOOM_COST, player);
			player.subScore(BOOM_COST);
			player.addSpecialTile(sT);
		}
	}

	private void buyMyOwn(Player player, String name) {
		if (player.getScore() >= MYWN_COST) {
			SpecialTile sT = new MyOwn(name, MYWN_COST, player);
			player.subScore(MYWN_COST);
			player.addSpecialTile(sT);
		}
	}

}
