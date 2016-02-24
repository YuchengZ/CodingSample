package edu.cmu.cs.cs214.hw4.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.core.GameChangeListener;
import edu.cmu.cs.cs214.hw4.gui.ActionLisener.BuySpecialTileListener;
import edu.cmu.cs.cs214.hw4.gui.ActionLisener.ExchangeListener;
import edu.cmu.cs.cs214.hw4.gui.ActionLisener.PassListener;
import edu.cmu.cs.cs214.hw4.gui.ActionLisener.PlayListener;
import edu.cmu.cs.cs214.hw4.gui.ActionLisener.RetrieveListener;
import edu.cmu.cs.cs214.hw4.gui.Panel.BoardPanel;
import edu.cmu.cs.cs214.hw4.gui.Panel.PlayerPanel;
import edu.cmu.cs.cs214.hw4.gui.Panel.RackPanel;

/**
 * GamePanel
 * 
 * @author ZhangYC
 *
 */
public class GamePanel extends JPanel implements GameChangeListener {

	private static final long serialVersionUID = -1051558484364880544L;
	private static final int GRID_LEN = 12;
	private static final int HELP_SIZE = 10;
	private int numOfPlayer;
	private JFrame parentFrame;
	private Game game;
	private PlayerPanel playerPanel;
	private RackPanel rackPanel;
	private BoardPanel boardPanel;
	private JPanel boardPlayerPanel;
	private JPanel movementPanel;
	private JTextArea help;

	private JButton restartButton;
	private JButton playButton;
	private JButton exchangeButton;
	private JButton passButton;
	private JButton retrieveButton;
	private JButton buyBoom;
	private JButton buyMyOwn;
	private JButton buyNegative;
	private JButton buyReverse;
	private JButton buySwitch;
	private JTextField status;

	/**
	 * constructor
	 * 
	 * @param g
	 *            Game
	 * @param frame
	 *            JFrame
	 * @param num
	 *            num of player
	 */
	public GamePanel(Game g, JFrame frame, int num) {
		game = g;
		parentFrame = frame;
		numOfPlayer = num;
		initElements();
		initLayout();
		game.addGameChangeListener(this);
		game.startGame();
	}

	private void initLayout() {

		playerPanel.add(help);

		// overall layout
		movementPanel.setLayout(new GridLayout(GRID_LEN, 1));
		movementPanel.add(playButton);
		movementPanel.add(exchangeButton);
		movementPanel.add(passButton);
		movementPanel.add(retrieveButton);
		movementPanel.add(new JLabel("Store:"));
		movementPanel.add(buyBoom);
		movementPanel.add(buyMyOwn);
		movementPanel.add(buyNegative);
		movementPanel.add(buyReverse);
		movementPanel.add(buySwitch);
		movementPanel.add(new JLabel(" "));
		movementPanel.add(restartButton);

		boardPlayerPanel.setLayout(new BorderLayout());
		boardPlayerPanel.add(playerPanel, BorderLayout.WEST);
		boardPlayerPanel.add(boardPanel, BorderLayout.CENTER);
		boardPlayerPanel.add(movementPanel, BorderLayout.EAST);

		setLayout(new BorderLayout());
		add(status, BorderLayout.NORTH);
		add(boardPlayerPanel, BorderLayout.CENTER);
		add(rackPanel, BorderLayout.SOUTH);

	}

	private void initElements() {
		// init help text
		initHelp();
		// init status textField
		status = new JTextField();
		status.setEditable(false);

		// init each panel
		playerPanel = new PlayerPanel(game, numOfPlayer, status);
		rackPanel = new RackPanel(game, status);
		boardPanel = new BoardPanel(game, status, rackPanel.getClickNormal(),
				rackPanel.getClickSpecial());
		movementPanel = new JPanel();
		boardPlayerPanel = new JPanel();

		// init restartButton
		restartButton = new JButton();
		restartButton.setText("restart");
		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						restart();
					}
				});
			}
		});

		// init playButton
		playButton = new JButton();
		playButton.setText("play");
		playButton.addActionListener(new PlayListener(game, status));

		// init exchangeButton
		exchangeButton = new JButton();
		exchangeButton.setText("exchange");
		exchangeButton.addActionListener(new ExchangeListener(game, status,
				rackPanel.getClickNormal()));

		// init passButton
		passButton = new JButton();
		passButton.setText("pass");
		passButton.addActionListener(new PassListener(game, status));

		// init retrieveButton
		retrieveButton = new JButton();
		retrieveButton.setText("retrieve");
		retrieveButton.addActionListener(new RetrieveListener(game, status));

		// init buy specialTile button
		buyBoom = new JButton();
		buyBoom.setText("buy boom");
		buyBoom.addActionListener(new BuySpecialTileListener("boom", game,
				status));

		buyMyOwn = new JButton();
		buyMyOwn.setText("buy my own");
		buyMyOwn.addActionListener(new BuySpecialTileListener("myown", game,
				status));

		buyReverse = new JButton();
		buyReverse.setText("buy reverse");
		buyReverse.addActionListener(new BuySpecialTileListener("reverse",
				game, status));

		buyNegative = new JButton();
		buyNegative.setText("buy negative");
		buyNegative.addActionListener(new BuySpecialTileListener("negative",
				game, status));

		buySwitch = new JButton();
		buySwitch.setText("buy switch");
		buySwitch.addActionListener(new BuySpecialTileListener("switch", game,
				status));
	}

	private void initHelp() {

		help = new JTextArea();
		help.setEditable(false);
		help.setFont(new Font(Font.MONOSPACED, Font.PLAIN, HELP_SIZE));
		help.setText("Readme:\n"
				+ "1.One place alow multiple kinds of special\n" + "  tile.\n"
				+ "2.If you want to play, you cannot only put\n"
				+ "  specialTile or they will consider your\n"
				+ "  movement invalid.\n"
				+ "3.If there is no tiles on the board, and you\n"
				+ "  want to play,you have to put a tile in the\n"
				+ "  center square no matter which round\n"
				+ "4.If you play and the movement is invalid,\n"
				+ "  they will retrieve all your movement,\n"
				+ "  you need to place again\n"
				+ "5.If you want to exchange and pass, you are\n"
				+ "  not allowed to put specialTile.\n"
				+ "  If you try to put specialTile on board,\n"
				+ "  they will force to retrieve your specialTile\n"
				+ "  after you click exchange and pass\n"
				+ "6.If you want to exchange the tiles,\n"
				+ "  click all the tiles you want to change,\n"
				+ "  then click exchange button\n"
				+ "7.If you click the normal tile, the game can\n"
				+ "  remember which normal tile your have clicked\n"
				+ "  but if you click special tile, the game will\n"
				+ "  go to record the special tile your clicked.\n"
				+ "  It won't remember what normal tile you clicked\n"
				+ "  before. Same thing wich special tile.\n"
				+ "8.Game ends when bag has no tial or every body\n"
				+ "  click pass\n"
				+ "9.Each specialTile cost 10 points, if player\n"
				+"   don't have enough points, he can't buy it.");

	}

	protected void restart() {
		// add frame and set its closing operation
		parentFrame.setVisible(false);
		JFrame frame = new JFrame("Start Scribble");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// add the beginning dialog of the game
		frame.add(new GameGui(frame));

		// display the JFrame
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);

	}

	@Override
	public void makeMoveChanged() {
		boardPanel.makeMoveChanged();
		playerPanel.makeMoveChanged();

	}

	@Override
	public void gameFlowChanged() {
		boardPanel.gameFlowChanged();
		rackPanel.gameFlowChanged();

	}

	@Override
	public void buySpecialTileChanged() {
		playerPanel.buySpecialTileChanged();
		rackPanel.buySpecialTileChanged();

	}

	@Override
	public void currentPlayerChanged() {
		boardPanel.currentPlayerChanged();
		playerPanel.currentPlayerChanged();
		rackPanel.currentPlayerChanged();

	}

	@Override
	public void gameOverChanged() {
		boardPanel.gameOverChanged();
		rackPanel.gameOverChanged();
		playerPanel.gameOverChanged();
		playButton.setEnabled(false);
		exchangeButton.setEnabled(false);
		passButton.setEnabled(false);
		retrieveButton.setEnabled(false);
		buyBoom.setEnabled(false);
		buyMyOwn.setEnabled(false);
		buyNegative.setEnabled(false);
		buyReverse.setEnabled(false);
		buySwitch.setEnabled(false);
	}

	@Override
	public void retrieveChanged() {
		boardPanel.retrieveChanged();
		rackPanel.retrieveChanged();
	}

}
