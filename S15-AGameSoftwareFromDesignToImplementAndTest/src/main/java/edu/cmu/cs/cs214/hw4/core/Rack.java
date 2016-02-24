package edu.cmu.cs.cs214.hw4.core;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.cs214.hw4.core.SpecialTile.SpecialTile;

/**
 * Rack, storing the normalTIle adn specialTile a palyer has
 * 
 * @author ZhangYC
 *
 */
public class Rack {
	private List<NormalTile> normalTiles = new ArrayList<NormalTile>();
	private List<SpecialTile> specialTiles = new ArrayList<SpecialTile>();

	/**
	 * add normal tile to rack
	 * 
	 * @param nT
	 *            NormalTile
	 */
	public void addNormalTile(NormalTile nT) {
		normalTiles.add(nT);
	}

	/**
	 * add special tile to rack
	 * 
	 * @param sT
	 *            SpecialTile
	 */
	public void addSpecialTile(SpecialTile sT) {
		specialTiles.add(sT);
	}

	/**
	 * remove the noramlTile form rack
	 * 
	 * @param nT
	 *            NormaLTile needed to be removed
	 */
	public void deleteNormalTile(NormalTile nT) {
		normalTiles.remove(nT);
	}

	/**
	 * remove the speicialTile from rack
	 * 
	 * @param sT
	 *            specialTile
	 */
	public void deleteSpecialTile(SpecialTile sT) {
		specialTiles.remove(sT);
	}

	/**
	 * check if a noramlTile is on rack or not
	 * 
	 * @param nT
	 *            NormalTile needed to check
	 * @return true if this tile on rack
	 */
	public boolean isOnRack(NormalTile nT) {
		return normalTiles.contains(nT);
	}

	/**
	 * check if a specialTile on rack or not
	 * 
	 * @param sT
	 *            SpecialTile needed to check
	 * @return true if this tile on rack
	 */
	public boolean isOnRack(SpecialTile sT) {
		return specialTiles.contains(sT);
	}

	/**
	 * get the number of normalTile on rack
	 * 
	 * @return int, number of normalTile
	 */
	public int getNumOfNormalTile() {
		return normalTiles.size();
	}

	/**
	 * get the normalTile(for testing)
	 * 
	 * @return an ArrayList of normalTiles
	 */
	public List<NormalTile> getNormalTiles() {
		return normalTiles;
	}

	/**
	 * get the SpecialTiles(for testing)
	 * 
	 * @return an ArrayList of spcialTile
	 */
	public List<SpecialTile> getSpecialTiles() {
		return specialTiles;
	}

	/**
	 * get number of specialTile
	 * 
	 * @return int the number of specialTIle
	 */
	public int getNumOfSpecialTile() {
		// TODO Auto-generated method stub
		return specialTiles.size();
	}

}
