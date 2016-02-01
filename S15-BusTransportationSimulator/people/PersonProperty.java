package edu.cmu.cs.cs214.hw3.people;

/**
 * 
 * @author ZhangYC
 *
 */
public abstract class PersonProperty extends Person{
	private final Person personProperty;
	
	/**
	 * constructor
	 * @param pp Person decorator
	 */
	public PersonProperty(Person pp){
		personProperty = pp;
	}
	
	@Override
	public int getDelay(){
		return personProperty.getDelay();
	}
	
	@Override
	public int getPeopleSpace(){
		return personProperty.getPeopleSpace();
	}
	
	@Override
	public int getLuggageSpace(){
		return personProperty.getLuggageSpace();
	}

}
