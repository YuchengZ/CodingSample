package edu.cmu.cs.cs214.hw5.framework.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * The panel of analysis plugin title, result and panel.
 * 
 * @author Tao
 *
 */
public class AnalysisResultPanel extends JPanel {

	private static final long serialVersionUID = 5574942497648331900L;
	private static final int HEIGHT = 6;
	private static final int WEIGHT = 10;

	private JLabel title = new JLabel();
	private JTextArea description = new JTextArea(HEIGHT, WEIGHT);
	private JPanel result = new JPanel();

	/**
	 * The constructor. Initialize the empty panel.
	 */
	public AnalysisResultPanel() {
		description.setLineWrap(true);
		description.setWrapStyleWord(true);

		setLayout(new BorderLayout());
		add(title, BorderLayout.NORTH);
		add(result, BorderLayout.CENTER);
		add(description, BorderLayout.SOUTH);
	}

	/**
	 * Call this method when title changed.
	 * 
	 * @param text
	 *                The new title.
	 */
	public void onTitleChanged(String text) {
		title.setText(text);
		add(title, BorderLayout.NORTH);
		revalidate();
		repaint();
	}

	/**
	 * Call this method when description changed.
	 * 
	 * @param s
	 *                The new description.
	 */
	public void onDescriptionChanged(String s) {
		description.setText(s);
		revalidate();
		repaint();
	}

	/**
	 * Call this method when result changed.
	 * 
	 * @param panel
	 *                The new panel.
	 */
	public void onResultChanged(JPanel panel) {
		
		remove(result);
		result = panel;
		add(result, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

}
