package edu.cmu.cs.cs214.hw3.people;

/**
 * 
 * @author ZhangYC
 *
 */
public class PersonWithLuggage extends PersonProperty{
	private int space = 1;
	private int numOfLuggage;
	
	/**
	 * constructor
	 * @param personProperty Person
	 * @param num int how many luggage this person carry
	 */
	public PersonWithLuggage(Person personProperty, int num){
		super(personProperty);
		numOfLuggage = num;
	}
	
	@Override
	public int getLuggageSpace(){
		return super.getLuggageSpace()+space*numOfLuggage;
	}
}
