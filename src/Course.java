import java.util.Comparator;

/**
 * The course class represents a course that has a name and units. The fields "semesterAvailable"
 * and "semesterTaken" are used in the scheduler class. Semester available represents the semester
 * the class' prerequisites were all completed (semesters are represented by the numbers 0 - Integer.MAX_VALUE).
 * Semester taken is the semester when the user actually decides to take the class. Note that the semester a 
 * class becomes available is not the same semester it could be taken so that is why they need two separate variables.
 * The boolean coreqsPresent is meant to indicate whether the class has prerequisites that can 
 * be taken concurrently or not. A prerequisite is class that must be completed before you can take
 * another class.  A prerequisite that can be taken concurrently means that you can take a class 
 * and its prerequisites together in the same semester instead of completing all the prerequisites in one semester 
 * and then taking the class that required those prerequisites in another semester. For this project I used this definition
 * of a class that can be taken concurrently: A class can be taken concurrently with its prerequisites as long as
 * all its normal prerequisites are completed and at least one class taken during the current semester
 * is a concurrent prerequisite and the rest of the classes in the semester are not a prerequisite to the class at all.
 * The field "NOT_AVAILABLE" is meant to be used in conjunction with "semesterAvailable" and is given the value -2 as that value is
 * invalid in the scheduler class. Note that -1 is a valid value for "semesterAvailable" and is explained in the scheduler class. Not taken is
 * meant to be used in conjuction with the field "semesterTaken" and is given the max value of an int for sorting purposes.
 */
public class Course implements Comparable<Course>
{
	private String courseName;
	
	private float units;
	
	private int semesterAvailable;
	
	private int semesterTaken;
	
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


	/**
	 * A list of courses is used in the scheduler class. It has to be sorted and it sorted in increasing order.
	 * This function return whether or not the current course was taken at an earlier semester or later semester
	 * in comparison to the course variable "o" which is the first letter of the word object.
	 */
	public int compareTo(Course o)
	{
		return semesterTaken - o.semesterTaken;
	}
	
}
