package edu.cmu.cs.cs214.hw4.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.cmu.cs.cs214.hw4.core.Game;

/**
 * the game's gui has main method
 * 
 * @author ZhangYC
 *
 */
public class GameGui extends JPanel {

	private static final long serialVersionUID = -1459895943556846039L;
	private static final String GAME = "SCRABBLE";
	private static final int TEXT_LEN = 5;
	private JFrame parentFrame;
	private Game game;

	/**
	 * constructor
	 * 
	 * @param frame
	 *            JFrame of GUI.
	 */
	public GameGui(JFrame frame) {
		parentFrame = frame;

		// Create the components to add to the panel.
		JLabel label = new JLabel("Number of Player: ");

		JTextField text = new JTextField(TEXT_LEN);
		JTextField status = new JTextField();
		status.setText("Please enter a number between 2-4!");
		status.setEditable(false);

		JButton button = new JButton("Start Game");
		JPanel startPanel = new JPanel();
		startPanel.setLayout(new BorderLayout());
		startPanel.add(label, BorderLayout.WEST);
		startPanel.add(text, BorderLayout.CENTER);
		startPanel.add(button, BorderLayout.EAST);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int num;
				try {
					num = Integer.parseInt(text.getText());
					game = new Game(num);
					startGame(num);

				} catch (NumberFormatException e) {
					return;
				}

			}
		});

		setLayout(new BorderLayout());
		add(startPanel, BorderLayout.NORTH);
		add(status, BorderLayout.SOUTH);
		setVisible(true);
	}

	protected void startGame(int num) {
		parentFrame.remove(this);
		GamePanel gp = new GamePanel(game, parentFrame, num);
		parentFrame.add(gp);
		parentFrame.setTitle(GAME);
		parentFrame.setResizable(true);
		parentFrame.pack();
	}

	/**
	 * main method
	 * 
	 * @param args
	 *            argument
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// add frame and set its closing operation
				JFrame frame = new JFrame("Start Scribble");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				// add the beginning dialog of the game
				frame.add(new GameGui(frame));

				// display the JFrame
				frame.pack();
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});
	}

}
