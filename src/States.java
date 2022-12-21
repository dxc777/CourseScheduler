
public enum States
{
	MAX_UNITS_REACHED("The max units for this semester have already been reached either remove"
			+ "a class or move this class to the next semester",StateType.ERROR), 
	CLASS_EXCEEDS_MAX_UNITS("Adding this class will exceed the set max units",StateType.ERROR), 
	AT_UNIT_MAX("This class has been added and the max units have now been reached",StateType.SUCCESS), 
	CLASS_ADDED("This class has been added and the max units have not been reached",StateType.SUCCESS),
	NEGATIVE_SEMESTER("You cannot move to a negative semester",StateType.ERROR), 
	SEMESTER_CHANGED("The semester has been changed",StateType.SUCCESS),
	INSUFFICIENT_DATA("Line does have minimum amount of data needed to parse:",StateType.ERROR),
	NAN("The units field cannot be parsed",StateType.ERROR),
	REUSED_IDENTIFIER("Class identifiers cannot be reused",StateType.ERROR),
	FILE_NOT_FOUND("The entered file cannot be found",StateType.ERROR),
	INVALID_INDEX("The list number entered is not valid enter again",StateType.ERROR),
	UNDECLARED_IDENTIFIER("This identifier has not been declared and is being used",StateType.ERROR),
	NO_AVAILABLE_CLASSES("There are no classes available for this semester",StateType.ERROR),
	NO_STATE("This state is meant to be a clear the current state variable",StateType.NEUTRAL),
	INVALID_UNITS("Units cannot be less than or equal to zero",StateType.ERROR),
	UNITS_CHANGED("Units changed succesfully",StateType.SUCCESS);
	
	//I placed the seperator here as this was the most fitting place
	static String seperator = "==================================";
		
	private final String MESSAGE;
	
	private final StateType stateType;
	
	private States(String MESSAGE, StateType stateType) 
	{
		this.MESSAGE = MESSAGE;
		this.stateType = stateType;
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
