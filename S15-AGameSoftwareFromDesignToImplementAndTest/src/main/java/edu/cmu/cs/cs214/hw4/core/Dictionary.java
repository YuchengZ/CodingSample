package edu.cmu.cs.cs214.hw4.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Dictionary, contain all the valid word in string
 * 
 * @author ZhangYC
 *
 */
public class Dictionary {
	private List<String> words = new ArrayList<String>();
	private String filename = "src/main/resources/words.txt";

	/**
	 * constructor, read files form words.txt
	 */
	public Dictionary() {

		// read words from file
		BufferedReader br = null;
		try {
			String line;
			br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) {
				words.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * check if the given word are valid or not
	 * 
	 * @param w
	 *            String represent the word that needed to check
	 * @return boolean true if the word in dictionary
	 */
	public boolean isValidWord(String w) {
		for (String word : words) {
			if (word.equals(w)) {
				return true;
			}
		}
		return false;
	}

}
