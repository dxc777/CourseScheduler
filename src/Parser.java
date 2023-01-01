import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.LinkedList;

public class Parser
{
	private Scanner inputFile;
	
	private AdjList requiredByXGraph;
	
	private AdjList prereqGraph;
	
	private ArrayList<Course> courseList;
	
	private LinkedList<LinkedList<String>> unprocessedEdges;
	
	private int[] inDegreeCount;
	
	private HashMap<String,Integer> idToVertex;
	
	//If all information is present in a line then it should a least be of length 3
	private final static int MINIMUM_DATA_LENGTH = 3;
	
	private final static int DEFAULT_EDGE_WEIGHT = 1;
	
	private final static int ID_INDEX = 0;
	
	private final static int NAME_INDEX = 1;
	
	private final static int UNIT_INDEX = 2;
	
	private final static int PREREQ_INDEX = 3;
	
	private final static String SEPERATOR = ",";
	
	public Parser(Scanner inputFile) 
	{
		this.inputFile = inputFile;
		
		unprocessedEdges = new LinkedList<>();
		idToVertex = new HashMap<>();
		courseList = new ArrayList<>();
		
		parseCourses();
		buildGraph();
	}

	/**
	 * The main goal of parse courses is to map class id to vertexes and to fill the course list with 
	 * the required information. Each line in the input will follow this format:
	 * <CLASSID>,<ClassName>,<UnitWorth>,[ListOfPrerequisitesForClass]
	 * It will fill the unproccessedEdges list with the list of classes that require the vertex x as a prerequisite 
	 * This and the hashmap will be used to build the graph
	 * 
	 */
	private void parseCourses()
	{
		while(inputFile.hasNextLine()) 
		{
			String line = inputFile.nextLine();
			if(line.isBlank()) 
			{
				continue;
			}
			String[] data = line.split(SEPERATOR);
			if(data.length < MINIMUM_DATA_LENGTH) 
			{
				throw new IllegalArgumentException(States.INSUFFICIENT_DATA.getMessage() + line);
			}
			String courseIdentifier = filterData(data[ID_INDEX]);
			String courseName = data[NAME_INDEX].trim();
			float courseUnits = -1;
			
			try 
			{
				courseUnits = Float.parseFloat(data[UNIT_INDEX]);
			}
			catch(NumberFormatException e) 
			{
				System.out.println(States.NAN.getMessage() + Arrays.toString(data));
				System.exit(0);
			}
			
			courseList.add(new Course(courseName,courseUnits));
			Integer returnValue = idToVertex.put(courseIdentifier,courseList.size() - 1);
			if(returnValue != null)  
			{
				throw new IllegalArgumentException("Course Identifiers cannot be reused:\n"
						+ line + "\nIdentifier that was reused: " + courseIdentifier);
			}
			unprocessedEdges.add(new LinkedList<>());
			for(int i = PREREQ_INDEX; i < data.length; i++) 
			{
				unprocessedEdges.getLast().add(filterData(data[i]));
			}
		}
	}
	
	/**
	 * THe build graph function will take the unproccessed prereqs list and name to index hashmap
	 * and use it to build the graph. At the end of this function the unproccessed prereqs will be empty.
	 * 
	 * 
	 */
	public void buildGraph() 
	{
		requiredByXGraph = new AdjList(courseList.size());
		prereqGraph = new AdjList(courseList.size());
		inDegreeCount = new int[courseList.size()];
		int i  = 0; 
		while(unprocessedEdges.isEmpty() == false) 
		{
			LinkedList<String> prereqs = unprocessedEdges.removeFirst();
			while(prereqs.isEmpty() == false) 
			{
				String className = prereqs.removeFirst();
				Integer vertex = idToVertex.get(className);
				if(vertex == null) 
				{
					System.out.println(States.UNDECLARED_IDENTIFIER.getMessage() + ": " + className);
					System.exit(0);
				}
				int adjVertex = vertex;
				requiredByXGraph.addEdge(adjVertex, i, DEFAULT_EDGE_WEIGHT);
				prereqGraph.addEdge(i, adjVertex, DEFAULT_EDGE_WEIGHT);
				inDegreeCount[i]++;
			}
			i++;
		}
	}
	
	/**
	 * Filter data removes any character that is not a letter or digit until
	 * the end of the string is reached. The character '-' is also allowed as
	 * it signifies that a class can be taken concurrently with other classes.
	 * @param str
	 */
	public String filterData(String str) 
	{
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < str.length(); i++) 
		{
			if(Character.isAlphabetic(str.charAt(i)) ) 
			{
				s.append(Character.toUpperCase(str.charAt(i)));
			}
			else if(Character.isDigit(str.charAt(i))) 
			{
				s.append(str.charAt(i));
			}
			else if(str.charAt(i) == '-')
			{
				s.append('-');
			}
		}
		return s.toString();
	}
	

	public ArrayList<Course> getCourseList()
	{
		return courseList;
	}
	
	public AdjList getRequiredByXGraph() 
	{
		return requiredByXGraph;
	}
	
	public AdjList getPrereqGraph() 
	{
		return prereqGraph;
	}
	
	public int[] getInDegreeCount() 
	{
		return inDegreeCount;
	}
	
}
