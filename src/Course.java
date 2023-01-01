import java.util.Comparator;

/**
 * Last edit made 11/9/22
 * Created entire class only getter and setters
 * @author J
 *
 */
public class Course implements Comparable<Course>
{
	private String courseName;
	
	private float units;
	
	private int semesterAvailable;
	
	private int semesterTaken;
	
	//This boolean value indicates whether or not the class has co requisites present and is assumed to be false
	private boolean coreqsPresent;
	
	public static final int NOT_AVAILABLE = -2;
	
	public static final int NOT_TAKEN = Integer.MAX_VALUE;
	
	public Course(String courseName, float units) 
	{
		this.courseName = courseName;
		this.units = units;
		semesterAvailable = NOT_AVAILABLE;
		semesterTaken = NOT_TAKEN;
		coreqsPresent = false;
	}

	public String getCourseName()
	{
		return courseName;
	}

	public void setCourseName(String courseName)
	{
		this.courseName = courseName;
	}

	public float getUnit()
	{
		return units;
	}

	public void setUnit(int unit)
	{
		this.units = unit;
	}

	public int getSemesterTaken()
	{
		return semesterTaken;
	}

	public void setSemesterTaken(int semesterCompleted)
	{
		this.semesterTaken = semesterCompleted;
	}
	
	public int getSemesterAvailable() 
	{
		return semesterAvailable;
	}
	
	public void setSemesterAvailable(int semesterAvailable) 
	{
		this.semesterAvailable = semesterAvailable;
	}
	
	public boolean coreqsPresent() 
	{
		return coreqsPresent;
	}
	
	public void setCoreqsPresent(boolean hasCoreqs) 
	{
		coreqsPresent = hasCoreqs;
	}
	
	public String toString() 
	{
		return courseName + " - " + units + " units";
	}


	@Override
	public int compareTo(Course o)
	{
		return semesterTaken - o.semesterTaken;
	}
	
}
