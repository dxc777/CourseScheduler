import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main 
{	
	static Scanner kb = new Scanner(System.in);
	
	static Scanner file;
	
	static Parser parsedFile;
	
	static Scheduler scheduler;
	
	static ArrayList<Action> actions;
	
	static int CHANGE_UNITS_INDEX = 3;
	
	static int RETURN = 0;
	
	public static void main(String[] args) throws Exception
	{
		addActions();
		while(true)
		{
			file = openFile();
			parsedFile = new Parser(file);
			scheduler = new Scheduler(parsedFile);
			
			actions.get(CHANGE_UNITS_INDEX).doAction();
			
			while(scheduler.donePlanning() == false) 
			{
				executeActions();
				
			}
			System.out.println(scheduler.getScheduleStr());
			
		}
	}




	private static void executeActions()
	{
		//Print useful information for the user
		System.out.println("Current semester: " + (scheduler.currSemester() + 1));
		ArrayList<Integer> courseList = scheduler.getSemesterCourses();
		if(scheduler.getState() == States.NO_SEMESTER_COURSES) 
		{
			System.out.println(States.NO_SEMESTER_COURSES.getMessage());
		}
		else 
		{
			System.out.println(printCourseList(courseList, "Courses taken this semester"));
			System.out.println("Total units taken this semster: " + scheduler.getSemesterUnits());
			System.out.println(States.seperator);
		}
		
		
		//Print the action list and execute the corresponding action
		for(int i = 0; i < actions.size(); i++) 
		{
			System.out.println((i + 1) + ") " + actions.get(i).getDescription());
		}
		int listIndex = (int)getNumber("Enter the number of the action you want to take: ", 1, actions.size());
		listIndex--;
		actions.get(listIndex).doAction();
	}
	

	
	
	public static Scanner openFile() 
	{
		File file = null;
		Scanner openedFile = null;
		boolean fileFound = false;
		do
		{
			try 
			{
				System.out.print("Enter the file name: ");
				String fileName = kb.nextLine();
				file = new File(fileName);
				openedFile = new Scanner(file);
				fileFound = true;
			}
			catch(FileNotFoundException e) 
			{
				System.out.println(States.FILE_NOT_FOUND.getMessage());
			}
		}while(fileFound == false);
		
		return openedFile;
	}
	
	/**
	 * Handles getting a number from the user. It will keep the user in a loop until the entered number 
	 * is within the specified range of [min, max]. It also checks to see if the range makes sense and throws an error if it
	 * doesn't.
	 * @param prompt
	 * @param min
	 * @param max
	 * @return
	 */
	public static float getNumber(String prompt, int min, int max)
	{
		if(max < min) 
		{
			throw new IllegalArgumentException("The max value must be greater than the min value "
					+ "(In this case the max value is less than the min value )");
		}
		
		float num = 0;
		boolean pickedNum = false;
		do 
		{
			try 
			{
				System.out.print(prompt);
				num = kb.nextFloat();
				kb.nextLine();
				if(num < min) 
				{
					System.out.println("Error -> The entered number is below the set minimum (" + min + ")");
				}
				else if(num > max) 
				{
					System.out.println("Error -> The entered number is above the set maximum (" + max + ")");
				}
				else 
				{
					pickedNum = true;
				}
			}
			catch(InputMismatchException e) 
			{
				System.out.println("Error -> The entered number cannot be parsed");
				kb.nextLine();
			}
			
		}while(pickedNum == false);
		return num;
	}
	
	
	/**
	 * This function implements all the actions a user can take. I chose to do it this way as I
	 * didn't like defining functions and then using a switch statement to determine which 
	 * function to call. With this implementation the functions are defined here and added 
	 * to the actions arraylist. A seperate function will print the list in order and the user 
	 * will pass the index of the action they want to take. This way I only need to define a new action 
	 * class to add a new action instead of adding a new action and then having to edit the corresponding
	 * switch case. The major down side to this solution is that this one function is huge. 
	 */
	public static void addActions() 
	{
		actions = new ArrayList<>();
		actions.add(new Action("Show the schedule that has been planned so far") 
		{
			public void doAction() 
			{
				System.out.println(scheduler.getScheduleStr());
			}
		});
		
		//TODO: change this one as well
		actions.add(new Action("Add a class to the current semester") 
		{
			public void doAction() 
			{
				ArrayList<Integer> availableClasses = scheduler.getAvailableClasses();
				States returnState = scheduler.getState();
				if(returnState == States.NO_AVAILABLE_COURSE) 
				{
					printState(returnState);
					return;
				}

				String availableStr = printCourseList(availableClasses, "These are the classes that can be taken for semester #" + (scheduler.currSemester() + 1));
				System.out.println(availableStr);
				
				int choice = (int)getNumber("Enter the number of the class you want to add or "+ RETURN + " to go back: ", RETURN,availableClasses.size());
				if(choice == RETURN) return;
				int vertex = availableClasses.get(choice - 1);
				scheduler.addClass(vertex);
				returnState = scheduler.getState();
				
				printState(returnState);
			}
		});
		
		actions.add(new Action("Remove a class from the current semester") 
		{
			public void doAction() 
			{
				ArrayList<Integer> semesterCourses = scheduler.getSemesterCourses();
				
				States returnState = scheduler.getState();
				if(returnState == States.NO_SEMESTER_COURSES)
				{
					printState(returnState);
					return;
				}
				
				String semesterStr = printCourseList(semesterCourses, "Classes taken during this semster: ");
				System.out.println(semesterStr);
				int choice = (int) getNumber("Enter the number of the class you want to remove or " + RETURN + " to go back: ", RETURN, semesterCourses.size());
				if(choice == RETURN) return;
				
				
				//At this point the "choice" variable is in the range [1 - semesterCourse.size()]
				System.out.println(States.seperator);
				ArrayList<Integer> removedClases = scheduler.removeClass(semesterCourses.get(choice - 1));
				System.out.println("These are the clases that were removed from the schedule:");
				for(int i = 0; i < removedClases.size(); i++) 
				{
					System.out.println("\t" + scheduler.getCourseList().get(removedClases.get(i)));
				}
				System.out.println(States.seperator);
			
			}
		});
		
		
		actions.add(new Action("Change set max units") 
		{
			public void doAction() 
			{
				scheduler.setMaxUnits((int) getNumber("Enter the max units allowed for one semester (must be greater than 0): ", 1, Integer.MAX_VALUE));
				printState(scheduler.getState());
			}
		});
		
		
		actions.add(new Action("Move to a different semester") 
		{
			public void doAction() 
			{
				int semester = (int) getNumber("Enter the semester number you want to move to: ",1, Integer.MAX_VALUE);
				semester--;
				scheduler.changeSemester(semester);
			}
		});
		
		
		actions.add(new Action("Move up one semester") 
		{
			public void doAction() 
			{
				scheduler.changeSemester(scheduler.currSemester() + 1);
			}
		});
		
		actions.add(new Action("Move down one semester") 
		{
			public void doAction() 
			{
				if(scheduler.currSemester() == 0) 
				{
					System.out.println("Cannot move down a semester");
				}
				else 
				{
					scheduler.changeSemester(scheduler.currSemester() - 1);
				}
			}
		});

		
		
	}
	

	private static String printCourseList(ArrayList<Integer> vertexes, String header)
	{
		StringBuilder s = new StringBuilder();
		s.append(States.seperator);
		s.append('\n');
		s.append(header);
		s.append(':');
		s.append('\n');
		ArrayList<Course> courseList = scheduler.getCourseList();
		for(int i = 0; i < vertexes.size(); i++) 
		{
			s.append(i + 1);
			s.append(')');
			s.append(courseList.get(vertexes.get(i)).toString());
			s.append('\n');
		}
		s.append(States.seperator);
		return s.toString();
	}
	
	public static void printState(States returnState) 
	{
		System.out.println(States.seperator);
		if(returnState.getType() == StateType.ERROR) 
		{
			System.out.println("ERROR --> " + returnState.getMessage());
		}
		else if(returnState.getType() == StateType.SUCCESS) 
		{
			System.out.println(returnState.getMessage());
		}
		System.out.println(States.seperator);

	}
	
}	



