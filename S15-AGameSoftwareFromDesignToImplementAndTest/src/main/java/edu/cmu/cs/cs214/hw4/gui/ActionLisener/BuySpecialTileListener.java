package edu.cmu.cs.cs214.hw4.gui.ActionLisener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import edu.cmu.cs.cs214.hw4.core.Game;

/**
 * buy special tile listeners
 * 
 * @author ZhangYC
 *
 */
public class BuySpecialTileListener implements ActionListener {
	private String name;
	private Game game;
	private JTextField status;

	/**
	 * constructor
	 * 
	 * @param s
	 *            String, the name of specialTile
	 * @param g
	 *            s Game
	 * @param j
	 *            JTextField show information
	 */
	public BuySpecialTileListener(String s, Game g, JTextField j) {
		status = j;
		name = s;
		game = g;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		status.setText(" ");
		game.buySpecialTile(name);

	}

}
