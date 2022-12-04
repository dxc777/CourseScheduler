
public enum States
{
	MAX_UNITS_REACHED(0,"The max units for this semester have already been reached either remove"
			+ "a class or move this class to the next semester"), 
	CLASS_EXCEEDS_MAX_UNITS(1,"Adding this class will exceed the set max units"), 
	AT_UNIT_MAX(2,"This class has been added and the max units have now been reached"), 
	CLASS_ADDED(3,"This class has been added and the max units have not been reached"),
	NEGATIVE_SEMESTER(4,"You cannot move to a negative semester"), 
	SEMESTER_CHANGED(5,"The semester has been changed"),
	INSUFFICIENT_DATA(6,"Line does have minimum amount of data needed to parse:"),
	NAN(7,"The units field cannot be parsed"),
	REUSED_IDENTIFIER(8,"Class identifiers cannot be reused"),
	FILE_NOT_FOUND(9,"The entered file cannot be found"),
	INVALID_INDEX(10,"The list number entered is not valid enter again");
	
	private final int STATE_CODE;
	
	private final String MESSAGE;
	
	private States(int STATE_CODE,String MESSAGE) 
	{
		this.STATE_CODE = STATE_CODE;
		this.MESSAGE = MESSAGE;
	}
	
	public int getStateCode() 
	{
		return STATE_CODE;
	}
	
	public String getMessage() 
	{
		return MESSAGE;
	}
	

	

}
