import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.LinkedList;


/**
 * The purpose of the parser class is turn the input file generated from the user into 
 * the data structures and respective objects needed for the scheduler to perform its job. 
 * The parser operates on the following assumptions:
 * 1) Each class follows this syntax -> <CLASS_ID> , <CLASS_NAME> , <UNITS> {, <PREREQ_ID>}
 * 2) Each class can only occupy 1 line of input
 * The class id is a unique identifier used to specifically distinguish classes from one another.
 * It is similar to the idea of a variable name. The class id cannot be reused after it has been used
 * for one class and is also meant to be short and easy to type out. EX) Mat101 PSY110 CSC341. It can only consists of 
 * letters and digits but the length does not matter. That means the user can have a identifier of single letter if they 
 * would like ex) 'a' is a valid identifier.  
 * The class name is is just the name of the class. It can consist of any characters and the only thing that is changed
 * about it is that the leading and trailing whitespace is removed. 
 * Units is the unit worth of the class. It can be written as a decimal or intger but it will be casted to 
 * an integer after it is parsed. This is just to make it easier for the user as most of this information 
 * can be copied and pasted and it may be written as a float on the users school portal. 
 * The last requirement is optional. It is a list of classes that are required for the class to be taken.
 * It consists of class id's. There is not limit on the length of the prerequisites.It can also be empty if the class has no prerequisites.
 * 
 * Note: the commas must be present. The spaces in between the names is not necessary and only there
 * for readability. The angle brackets are also not needed they are just there for readability.
 * However all this information must be present in one line.
 * Casing and spacing does not matter. Internally all characters are changed to uppercase and all
 * whitespace is removed.
 * If a class has a prerequisite that can be taken concurrently than that class should have "-C" or "-c"
 * at the end of the it and before the next prerequisite
 * Here is an example line of input
 * 
 * Assume the prereq identifiers are declared elsewhere
 * 			CSC123, Intro to computer science, 4.00, Mat 193, CSC123,mat142,pSy 7 -C,MNY 10-c
 * 
 * The inputfile is just the raw data from the user
 * 
 * RequiredByGraph -> This is a graph that for each vertex, v, all the edges of v are others classes that have v as a 
 * prerequisite. For example say class A has a prerequisite of class B. Say there is also a function vertex(class name) that when
 * given a class name will return the vertex in the graph. If we call vertex(B) and look at the edges of the B we will find the vertex(A)
 * there since B is a prerequisite to take A. 
 * 
 * PrereqGrahp -> The prereq graph is the opposite of the requireByXGraph. It is short for prerequisite graph.
 * And each vertex,v, in the graph gives the list of prerequisites for the vertex v.
 * 
 * courseList -> The course list is an arraylist of all the parsed data from the input file. 
 * 
 * NOTE: the course vertexe's are labeled with the numbers [0, n-1] with n being the number of classes present 
 * in the input file. Therefore, when looking at the ith element in the course list or the requiredByXGraph or
 * the prereqGraph they all refer to the same class. 
 * 
 * unprocessedEdges -> I did not specify that the classes need to be declared in order. They can be declared in any
 * order that is convienent to the user. However, when looking through the prerequisite list of a certain class 
 * we may encounter a vertex that is undeclared. 
 * So we wont know what the actual integer that represents that courses vertex is. So this linkedlist holds
 * all the <PREREQ_ID> 's that were listed for the i'th class. 
 * 
 * inDegreeCount -> This whole project is based on Khan's algorithm. So the indegreecount tells how many prerequisites
 * need to be completed before a class can be taken. 
 * 
 * idToVertex -> This hashmap holds the <COURSE_ID> and it respective vertex.
 * 
 * DEFAULT_EDGE_WEIGHT -> if a class cannot be taken conccurently it gets this edge weight in both graphs
 * 
 * CONCURRENT_WEIGHT -> if a class can be taken concurrently it gets this edge weight in the prereqGraph
 * 
 * MINIMUM_DATA_LENGTH -> if the user follows the syntax listed above then the length of the data array should 
 * be at least 3
 * 
 * ID_INDEX, NAME_INDEX, UNIT_INDEX, PREREQ_INDEX -> all these refer to an index in the data array
 * at that index you can find the data that the variable describes
 */
public class Parser
{
	private Scanner inputFile;
	
	private AdjList requiredByXGraph;
	
	private AdjList prereqGraph;
	
	private ArrayList<Course> courseList;
	
	private LinkedList<LinkedList<String>> unprocessedEdges;
	
	private int[] inDegreeCount;
	
	private HashMap<String,Integer> idToVertex;
	
	private final static int DEFAULT_EDGE_WEIGHT = 1;
	
	public final static int CONCURRENT_WEIGHT = 2;
	
	private final static int MINIMUM_DATA_LENGTH = 3;
	
	private final static int ID_INDEX = 0;
	
	private final static int NAME_INDEX = 1;
	
	private final static int UNIT_INDEX = 2;
	
	private final static int PREREQ_INDEX = 3;
	
	private final static String SEPERATOR = ",";
	
	private final static String CONCURRENT_FLAG = "-C";
	
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
	 * Parse courses populates the course list array with all the courses in the input file. It also 
	 * gives each course id a vertex to be paired with it. Then for each vertex it creates its list of 
	 * prerequisites to be translated afterward. 
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
	 * Here the graphs are actually created. Since we know for sure how many classes are in the input file we can 
	 * actually initialize the graphs we the amount of vertexes that need to be in it and the same is
	 * true for the indegreeCount array. We can also process the edges of the graph since we know each class identifier's
	 * respective vertex. 
	 */
	public void buildGraph() 
	{
		requiredByXGraph = new AdjList(courseList.size());
		prereqGraph = new AdjList(courseList.size());
		inDegreeCount = new int[courseList.size()];
		int vertex  = 0; 
		while(unprocessedEdges.isEmpty() == false) 
		{
			LinkedList<String> prereqs = unprocessedEdges.removeFirst();
			while(prereqs.isEmpty() == false) 
			{
				String className = prereqs.removeFirst();
				boolean hasConcurrClass = false;
				
				if(className.endsWith(CONCURRENT_FLAG)) 
				{
					className = className.substring(0,className.length() - CONCURRENT_FLAG.length());
					hasConcurrClass = true;
				}
				
				Integer adjVertex = idToVertex.get(className);
				if(adjVertex == null) 
				{
					System.out.println(States.UNDECLARED_IDENTIFIER.getMessage() + ": " + className);
					System.exit(0);
				}
				
				if(hasConcurrClass) 
				{
					courseList.get(vertex).setCoreqsPresent(true);
				}
				//The weight for edges of requiredByXGraph do not matter and are always default
				requiredByXGraph.addEdge(adjVertex, vertex, DEFAULT_EDGE_WEIGHT);
				//In the prereq graph the edge weight matters as it is used to determine if a class can be taken
				prereqGraph.addEdge(vertex, adjVertex, hasConcurrClass ? CONCURRENT_WEIGHT : DEFAULT_EDGE_WEIGHT);
				inDegreeCount[vertex]++;
			}
			vertex++;
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
