package edu.cmu.cs.cs214.hw3.people;

/**
 * 
 * @author ZhangYC
 *
 */
public class PersonGroup extends PersonProperty{
	private int space; // a group of people will need more person space
	
	/**
	 * constructor
	 * @param personProperty Person decorator
	 * @param numOfPeople int how many people in this group
	 */
	public PersonGroup(Person personProperty,int numOfPeople){
		super(personProperty);
		// basicPeople already occupy one space;
		space = numOfPeople-1;
		
	}
	
	@Override
	public int getPeopleSpace(){
		return super.getPeopleSpace()+space;
	}
	
	

}
