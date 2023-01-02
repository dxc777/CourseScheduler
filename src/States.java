
/**
 * States are messages used to tell the user how an action went. These are used in the 
 * Scheduler and Parser class. They are made in such a way that if a function modifies
 * the "curentState" variable then it will have at least 1 error state and at least 1 success state
 * except for states in the parser class. All the states are also unique. When a function that modifies the state if called the
 * getState() function must be called to clear the state and report what happens to the user. 
 * If not then an exception is raised the next time a function that modifies that "currentState"
 * variable is called. Since each state is unique and associated with 1 function then it is easy to find out 
 * which function state was not cleared. 
 * @author J
 *
 */
public enum States
{
	//addClass states (in scheduler class)
	CLASS_EXCEEDS_MAX_UNITS("Adding this class will exceed the set max units",StateType.ERROR), 

	MAX_UNITS_REACHED("The max units for this semester have already been reached either remove"
			+ "a class or move this class to the next semester",StateType.ERROR), 
	AT_UNIT_MAX("This class has been added and the max units have now been reached",StateType.SUCCESS), 
	CLASS_ADDED("This class has been added and the max units have not been reached",StateType.SUCCESS),
	//addClass
	
	//changeSemester state (in scheduler class)
	NEGATIVE_SEMESTER("You cannot move to a negative semester",StateType.ERROR), 
	SEMESTER_CHANGED("The semester has been changed",StateType.SUCCESS),
	//changeSemester
	
	//setUnits states
	INVALID_UNITS("Units cannot be less than or equal to zero",StateType.ERROR),
	UNITS_CHANGED("Units changed succesfully",StateType.SUCCESS),
	//setUnits
	
	//Get semester courses
	NO_SEMESTER_COURSES("There are no courses taken this semester",StateType.ERROR),
	SEMESTER_RETURNED("The clases this semester were returned",StateType.SUCCESS),
	//Get semester 
	
	//Get available courses states
	NO_AVAILABLE_COURSE("There are no available courses for this semester",StateType.ERROR),
	COURSES_AVAILABLE("There are courses available for this semester",StateType.SUCCESS),
	//get available courses
	
	
	
	//Parser class states
	INSUFFICIENT_DATA("Line does have minimum amount of data needed to parse:",StateType.ERROR),
	NAN("The units field cannot be parsed",StateType.ERROR),
	UNDECLARED_IDENTIFIER("This identifier has not been declared and is being used",StateType.ERROR),
	//Parser class
	
	//openFile states
	FILE_NOT_FOUND("The entered file cannot be found",StateType.ERROR),
	//openFile
	
	//
	INVALID_INDEX("The list number entered is not valid enter again",StateType.ERROR),
	
	
	//Default state used after "currentState" variable has been cleared
	NO_STATE("This state is meant to be a clear the current state variable",StateType.NEUTRAL);
	
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
