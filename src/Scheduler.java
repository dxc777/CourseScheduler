import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The scheduler class is meant to help a student plan the courses they want to take by maintaining 
 * a graph of the classes that have been taken and providing them with information regarding what classes 
 * they can take at the moment. 
 * 
 * 
 * TODO: clean up the user interface
 * TODO: Add documentation to functions that make it easier to understand the implementation
 * TODO: Add handling of concurrent classes
 * TODO: Add support for classes all under 1 topic where user pick x amount of classses from topic
 * TODO: Add support for classes that are only available during certain terms i.e fall or sping
 * TODO: Add tags that can be used to differentiate between general education or major class
 * TODO: Add ability to change max units while planning
 * @author J
 *
 */
public class Scheduler
{
	private ArrayList<Course> courseList;
	
	private ArrayList<Course> sortedCourseList;
	
	private AdjList requiredByXGraph;
	
	private States currentState;
	
	private int[] inDegreeCount;
	
	private int classesTaken;
	
	private int semester;
	
	private int maxUnits;
	
	private static final int ALL_PREREQUISITES_COMPLETE = 0;
	
	private static final int INITIAL_SEMESTER = -1;
	
	public static final int INVALID_INDEX = -1;
	
	public static final int DEFAULT_MAX_UNITS = 15;
	
	public Scheduler(Parser parsedFile) 
	{
		courseList = parsedFile.getCourseList();
		sortedCourseList = new ArrayList<>(courseList);
		requiredByXGraph = parsedFile.getRequiredByXGraph();
		inDegreeCount = parsedFile.getInDegreeCount();
		semester = 0;
		setMaxUnits(DEFAULT_MAX_UNITS);
		
		for(int i = 0; i < inDegreeCount.length; i++) 
		{
			if(inDegreeCount[i] == ALL_PREREQUISITES_COMPLETE) 
			{
				courseList.get(i).setSemesterAvailable(INITIAL_SEMESTER);
			}
		}
	}
	
	
	public boolean addClass(int vertex) 
	{
		int semesterUnits = getSemesterUnits();
		if(semesterUnits > maxUnits) 
		{
			throw new RuntimeException("Semester Units was able to exceed max units");
		}
		else if(semesterUnits + courseList.get(vertex).getUnit() > maxUnits) 
		{
			this.currentState = States.CLASS_EXCEEDS_MAX_UNITS;
			return false;
		}
		else if(getSemesterUnits() == maxUnits) 
		{
			this.currentState = States.MAX_UNITS_REACHED;
			return false;
		}
		
		courseList.get(vertex).setSemesterTaken(semester);
		classesTaken++;
		
		Edge curr = requiredByXGraph.getHeadOfVertex(vertex).next;
		while(curr != null) 
		{
			inDegreeCount[curr.adjVertex]--;
			if(inDegreeCount[curr.adjVertex] == 0) 
			{
				courseList.get(curr.adjVertex).setSemesterAvailable(semester);
			}
			curr = curr.next;
		}
		
		if(semesterUnits + courseList.get(vertex).getUnit() == maxUnits) 
		{
			this.currentState = States.AT_UNIT_MAX;
		}
		else 
		{
			this.currentState = States.CLASS_ADDED;
		}
		return true;
	}
	
	public ArrayList<Integer> removeClass(int vertex)
	{
		//To be removed holds all vertex that are already taken classes and must be removed 
		Queue<Integer> toBeRemoved = new LinkedList<>();
		ArrayList<Integer> removedClasses = new ArrayList<>();
		
		courseList.get(vertex).setSemesterTaken(Course.NOT_TAKEN);
		classesTaken--;
		removedClasses.add(vertex);
		
		Edge curr = requiredByXGraph.getHeadOfVertex(vertex).next;
		while(curr != null) 
		{
			if(courseList.get(curr.adjVertex).getSemesterTaken() != Course.NOT_TAKEN) 
			{
				toBeRemoved.add(curr.adjVertex);
				courseList.get(curr.adjVertex).setSemesterAvailable(Course.NOT_AVAILABLE);
				courseList.get(curr.adjVertex).setSemesterTaken(Course.NOT_TAKEN);
			}
			curr = curr.next;
		}
		
		while(toBeRemoved.isEmpty() == false) 
		{
			int v = toBeRemoved.poll();
			inDegreeCount[v]++;
			classesTaken--;
			
			curr = requiredByXGraph.getHeadOfVertex(v).next;
			while(curr != null) 
			{
				if(courseList.get(curr.adjVertex).getSemesterTaken() != Course.NOT_TAKEN) 
				{
					toBeRemoved.add(curr.adjVertex);
					courseList.get(curr.adjVertex).setSemesterAvailable(Course.NOT_AVAILABLE);
					courseList.get(curr.adjVertex).setSemesterTaken(Course.NOT_TAKEN);
				}
				curr = curr.next;
			}
			removedClasses.add(v);
		}
		
		return removedClasses;
	}
	
	
	public int getSemesterUnits() 
	{
		
		int units = 0;
		for(int i = 0; i < courseList.size(); i++) 
		{
			if(courseList.get(i).getSemesterTaken() ==  semester) 
			{
				units += courseList.get(i).getUnit();
			}
		}
		return units;
	}
	

	/**
	 * Return the list of classes that are available for the current semester by checking if
	 * the class is available, can be taken within the current semester, and has not been taken yet
	 * @return
	 */
	public ArrayList<Integer> getAvailableClasses() 
	{
		ArrayList<Integer> availableCourses = new ArrayList<>();
		//Vertex doubles as the vertex of the class in the graph and the 
		//index in the coureslist array
		for(int vertex = 0; vertex < courseList.size(); vertex++) 
		{
			Course currCourse = courseList.get(vertex);
			
			//IF this is true then the prerequisites for the class have not been met and thus
			//the class cannot be taken
			if(currCourse.getSemesterAvailable() == Course.NOT_AVAILABLE) continue;
			//since the prerequisites have been met we need to make sure 
			//we are in a semester where we can take it and the course have not been taken yet
			if(this.semester > currCourse.getSemesterAvailable() && 
					currCourse.getSemesterTaken() == Course.NOT_TAKEN) 
			{
				availableCourses.add(vertex);
			}
		}
		
		if(availableCourses.size() == 0) 
		{
			currentState = States.NO_AVAILABLE_CLASSES;
			return null;
		}
		else 
		{
			return availableCourses;
		}
	}
	
	/**
	 * getScheduleStr() works by operating on the sortedCourseList ArrayList.
	 * As the name implies that ArrayList is sorted as that makes printing the classes 
	 * easier. The outer loop ensures that after we exit from the inner while loop we 
	 * are still in a valid index of the array and have not reached the section of the array
	 * that contains all the courses that have not been taken yet. Before we enter the inner loop
	 * the header is added to the built string and in the inner loop we add the courses that belong to 
	 * that specific semester. This design avoids the need to have an awkward if statement that checks
	 * to see if we are still in the correct semester and need to change the header if we aren't.
	 * @return A string representation of the classes that have been picked
	 */
	public String getScheduleStr() 
	{
		Collections.sort(sortedCourseList);
		StringBuilder s = new StringBuilder();
		int i = 0;
		s.append("========The schedule that has been planned so far========\n");
		while(i < sortedCourseList.size() 
				&& sortedCourseList.get(i).getSemesterTaken() != Course.NOT_TAKEN) 
		{
			int semester = sortedCourseList.get(i).getSemesterTaken();
			s.append("----Semester #");
			s.append(semester == INITIAL_SEMESTER ? 1 : semester + 1);
			s.append("----\n");
			while(i < sortedCourseList.size() && 
					sortedCourseList.get(i).getSemesterTaken() == semester) 
			{
				s.append('\t');
				s.append(sortedCourseList.get(i).toString());
				s.append('\n');
				i++;
			}
		}
		s.append("==========================================================");
		return s.toString();
	}
	

	/**
	 * Returns a list of all the classes that have been taken during the current semester
	 * @return null if no classes taken during semester or the list of classes
	 */
	public ArrayList<Integer> getSemesterCourses() 
	{
		//These are courses that were taken within the semester
		ArrayList<Integer> courses = new ArrayList<>();
		//the vertex variable is also the variable used to iterate through the course list
		//this can be done since the index in the courselist is the same as the vertex in the graph
		for(int vertex = 0; vertex < courseList.size(); vertex++) 
		{
			Course currCourse = courseList.get(vertex);
			if(currCourse.getSemesterTaken() == this.semester) 
			{
				courses.add(vertex);
			}
		}
		
		//If the list is empty then no classes have been taken for this semster
		if(courses.size() == 0) 
		{
			currentState = States.NO_AVAILABLE_CLASSES;
			return null;
		}
		else 
		{
			return courses;
		}
	}

	public int getMaxUnits()
	{
		return maxUnits;
	}

	public void setMaxUnits(int maxUnits)
	{
		this.maxUnits = maxUnits;
	}
	
	
	public boolean changeSemester(int semester) 
	{
		if(semester < 0) 
		{
			this.currentState = States.NEGATIVE_SEMESTER;
			return false;
		}
		this.semester = semester;
		this.currentState = States.SEMESTER_CHANGED;
		return true;
	}
		
	public boolean donePlanning() 
	{
		return classesTaken == courseList.size();
	}
	
	public States getState() 
	{
		States saveState = currentState;
		currentState = States.NO_STATE;
		return saveState;
	}
	
	public int currSemester() 
	{
		return semester;
	}
	
	public ArrayList<Course> getCourseList()
	{
		return courseList;
	}
}
