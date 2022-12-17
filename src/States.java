
public enum States
{
	MAX_UNITS_REACHED(0,"The max units for this semester have already been reached either remove"
			+ "a class or move this class to the next semester",StateType.ERROR), 
	CLASS_EXCEEDS_MAX_UNITS(1,"Adding this class will exceed the set max units",StateType.ERROR), 
	AT_UNIT_MAX(2,"This class has been added and the max units have now been reached",StateType.SUCCESS), 
	CLASS_ADDED(3,"This class has been added and the max units have not been reached",StateType.SUCCESS),
	NEGATIVE_SEMESTER(4,"You cannot move to a negative semester",StateType.ERROR), 
	SEMESTER_CHANGED(5,"The semester has been changed",StateType.SUCCESS),
	INSUFFICIENT_DATA(6,"Line does have minimum amount of data needed to parse:",StateType.ERROR),
	NAN(7,"The units field cannot be parsed",StateType.ERROR),
	REUSED_IDENTIFIER(8,"Class identifiers cannot be reused",StateType.ERROR),
	FILE_NOT_FOUND(9,"The entered file cannot be found",StateType.ERROR),
	INVALID_INDEX(10,"The list number entered is not valid enter again",StateType.ERROR),
	UNDECLARED_IDENTIFIER(11,"This identifier has not been declared and is being used",StateType.ERROR),
	NO_AVAILABLE_CLASSES(12,"There are no classes available for this semester",StateType.ERROR),
	NO_STATE(13,"This state is meant to be a clear the current state variable",StateType.NEUTRAL);
	
	private final int STATE_CODE;
	
	private final String MESSAGE;
	
	private final StateType stateType;
	
	private States(int STATE_CODE,String MESSAGE, StateType stateType) 
	{
		this.STATE_CODE = STATE_CODE;
		this.MESSAGE = MESSAGE;
		this.stateType = stateType;
	}
	
	public int getStateCode() 
	{
		return STATE_CODE;
	}
	
	public String getMessage() 
	{
		return MESSAGE;
	}
	
	public StateType getType() 
	{
		return stateType;
	}
	

	

}
