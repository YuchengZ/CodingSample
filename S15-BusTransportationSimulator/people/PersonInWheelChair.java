package edu.cmu.cs.cs214.hw3.people;

/**
 * 
 * @author ZhangYC
 *
 */
public class PersonInWheelChair extends PersonProperty{
	private final int space = 1; // wheelchair will need 1 more person space.
	private final int delayTime = 5; // wheelchair will cause 5s delay.

	/**
	 * constructor
	 * @param personProperty Person decorator
	 */
	public PersonInWheelChair(Person personProperty){
		super(personProperty);
	}


	@Override
	public int getDelay(){
		return super.getDelay()+delayTime;
	}
	

	@Override
	public int getPeopleSpace(){
		return super.getPeopleSpace()+space;
	}

}
