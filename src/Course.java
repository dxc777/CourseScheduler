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
	
	public static final int NOT_AVAILABLE = -2;
	
	public static final int NOT_TAKEN = Integer.MAX_VALUE;
	
	public Course(String courseName, float units) 
	{
		this.courseName = courseName;
		this.units = units;
		semesterAvailable = NOT_AVAILABLE;
		semesterTaken = NOT_TAKEN;
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
	
	public String toString() 
	{
		return courseName + " - " + units + 
				(semesterTaken != NOT_TAKEN ? " Completed during semester " + semesterTaken : " Not completed");
	}


	@Override
	public int compareTo(Course o)
	{
		return semesterTaken - o.semesterTaken;
	}
	
}
