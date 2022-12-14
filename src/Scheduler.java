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
	
	/**
	 * After the user has seen the available classes for the semester they will pick the index of the 
	 * class they want to pick. This method will return the actual vertex of the item on the list
	 * @param index
	 * @return
	 */
	public int getVertexFromAddList(int index) 
	{
		if(index < 0) 
		{
			this.currentState = States.INVALID_INDEX;
			return INVALID_INDEX;
		}
		int vertex = 0;
		while(vertex < courseList.size() && index > 0) 
		{
			Course course = courseList.get(vertex);
			if(course.getSemesterTaken() == Course.NOT_TAKEN) 
			{
				if(semester > course.getSemesterAvailable() 
					&& course.getSemesterAvailable() != Course.NOT_AVAILABLE) 
				{
					index--;
				}
			}
			if(index > 0)vertex++;
		}
		if(index > 0 && vertex >= courseList.size()) 
		{
			this.currentState = States.INVALID_INDEX;
			return INVALID_INDEX;
		}
		return vertex;
	}
	
	public int getVertexFromRemoveList(int listIndex) 
	{
		if(listIndex < 0) 
		{
			this.currentState = States.INVALID_INDEX;
			return INVALID_INDEX;
		}
		int i = 0; 
		while(i < sortedCourseList.size() 
				&& sortedCourseList.get(i).getSemesterTaken() != semester) 
		{
			i++;
		}
		
		while(i < sortedCourseList.size() && listIndex > 0
				&& sortedCourseList.get(i).getSemesterTaken() == semester) 
		{
			listIndex--;
			if(listIndex > 0) i++;
		}
		if(i >= sortedCourseList.size() || listIndex != 0) 
		{
			this.currentState = States.INVALID_INDEX;
			return INVALID_INDEX;
		}
		return i;
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
	
	
	public String getAvailableClasses() 
	{
		StringBuilder s = new StringBuilder();
		s.append("Available Courses:\n");
		int index = 1;
		for(int i = 0; i < courseList.size(); i++) 
		{
			Course course = courseList.get(i);
			if(course.getSemesterTaken() != Course.NOT_TAKEN) continue;
			if(semester > course.getSemesterAvailable()
					&& course.getSemesterAvailable() != Course.NOT_AVAILABLE) 
			{
				s.append(index + ")");
				s.append(course.getCourseName() + " Units: " + course.getUnit() + "\n");
				index++;
			}
		}
		if(index == 1) currentState = States.NO_AVAILABLE_CLASSES;
		return s.toString();
	}
	
	public String getScheduleStr() 
	{
		Collections.sort(sortedCourseList);
		StringBuilder s = new StringBuilder();
		int i = 0;
		s.append("========The schedule that has been planned so far========\n");
		int currSemester = -1;
		while(i < sortedCourseList.size() 
				&& sortedCourseList.get(i).getSemesterTaken() != Course.NOT_TAKEN) 
		{
			Course course = sortedCourseList.get(i);
			if(currSemester != course.getSemesterTaken()) 
			{
				s.append("----Semester #");
				s.append(course.getSemesterTaken() + 1);
				s.append("----");
				s.append('\n');
				currSemester = course.getSemesterTaken();
			}
			s.append('\t');
			s.append(course.getCourseName());
			s.append('-');
			s.append(course.getUnit());
			s.append(" units\n");
			i++;
		}
		s.append("==========================================================");
		return s.toString();
	}
	
	public String getSemesterStr() 
	{
		Collections.sort(sortedCourseList);
		StringBuilder s = new StringBuilder();
		int i = 0; 
		s.append("========The schedule that has been planned for semester #" + (semester) +"========\n");
		while(i < sortedCourseList.size() 
				&& sortedCourseList.get(i).getSemesterTaken() != semester) 
		{
			i++;
		}
		
		int label = 1;
		while(i < sortedCourseList.size() 
				&& sortedCourseList.get(i).getSemesterTaken() == semester) 
		{
			Course course = sortedCourseList.get(i);
			s.append(label);
			s.append(')');
			s.append(course.getCourseName());
			s.append(' ');
			s.append(course.getUnit());
			s.append('\n');
			i++;
			label++;
		}
		s.append("==========================================================");
		return s.toString();
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
		return currentState;
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
