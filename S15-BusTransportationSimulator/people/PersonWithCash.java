package edu.cmu.cs.cs214.hw3.people;

/**
 * 
 * @author ZhangYC
 *
 */
public class PersonWithCash extends PersonProperty{
	private final int delayTime = 3; // person pay by cash will delay 3s
	
	/**
	 * constructor
	 * @param personProperty Person 
	 */
	public PersonWithCash(Person personProperty){
		super(personProperty);
	}
	
	@Override
	public int getDelay(){
		return super.getDelay()+delayTime;
	}

}
