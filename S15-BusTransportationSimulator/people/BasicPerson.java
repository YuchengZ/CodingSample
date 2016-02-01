package edu.cmu.cs.cs214.hw3.people;

/**
 * 
 * @author ZhangYC
 *
 */
public class BasicPerson extends Person{

	@Override
	public int getDelay() {
		return 0;
	}

	@Override
	public int getPeopleSpace() {
		return 1;
	}
	
	@Override
	public int getLuggageSpace() {
		return 0;
	}


}
