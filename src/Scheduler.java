import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;


public class Scheduler
{
	private ArrayList<Course> courseList;
	
	private ArrayList<Course> sortedCourseList;
	
	private AdjList requiredByXGraph;
	
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
	
	
	public States addClass(int vertex) 
	{
		int semesterUnits = getSemesterUnits();
		if(semesterUnits > maxUnits) 
		{
			throw new RuntimeException("Semester Units was able to exceed max units");
		}
		else if(semesterUnits + courseList.get(vertex).getUnit() > maxUnits) 
		{
			return States.CLASS_EXCEEDS_MAX_UNITS;
		}
		else if(getSemesterUnits() == maxUnits) 
		{
			return States.MAX_UNITS_REACHED;
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
			return States.AT_UNIT_MAX;
		}
		else 
		{
			return States.CLASS_ADDED;
		}
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
			return INVALID_INDEX;
		}
		return vertex;
	}
	
	public int getVertexFromRemoveList(int listIndex) 
	{
		if(listIndex < 0) 
		{
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
	
	
	public States changeSemester(int semester) 
	{
		if(semester < 0) 
		{
			return States.NEGATIVE_SEMESTER;
		}
		this.semester = semester;
		return States.SEMESTER_CHANGED;
	}
		
	public boolean donePlanning() 
	{
		return classesTaken == courseList.size();
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
