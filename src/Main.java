import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main 
{	
	static Scanner kb = new Scanner(System.in);
	
	static Scanner file;
	
	static Parser parsedFile;
	
	static Scheduler scheduler;
	
	static ArrayList<Action> actions;
	
	public static void main(String[] args) throws Exception
	{
		addActions();
		while(true)
		{
			file = openFile();
			parsedFile = new Parser(file);
			scheduler = new Scheduler(parsedFile);
			float maxUnits = getNumber("Enter the max units you wish to take per semester: ", 1, Integer.MAX_VALUE);
			scheduler.setMaxUnits((int)maxUnits);
			
			while(scheduler.donePlanning() == false) 
			{
				for(int i = 0; i < actions.size(); i++) 
				{
					System.out.println((i + 1) + ") " + actions.get(i).getDescription());
				}
				int listIndex = -1;
				do 
				{
					listIndex = (int)getNumber("Enter the number of the action you want to take: ", 1, actions.size());
				}while(listIndex < 1 || listIndex > actions.size());
				listIndex--;
				
				actions.get(listIndex).doAction();
				
			}
			System.out.println(scheduler.getScheduleStr());
			
		}
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
	 * Get number get a number and also handles errors when a number would be below or above a specified 
	 * range. Or if the passed number is not a number.
	 * @param prompt
	 * @param min
	 * @param max
	 * @return
	 */
	public static float getNumber(String prompt, int min, int max)
	{
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
					System.out.println("The entered number is below the set minimum (" + min + ")");
				}
				else if(num > max) 
				{
					System.out.println("The entered number is above the set maximum (" + max + ")");
				}
				else 
				{
					pickedNum = true;
				}
			}
			catch(NumberFormatException e) 
			{
				System.out.println("The entered number cannot be parsed");
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
		
		actions.add(new Action("Add a class to the current semester") 
		{
			public void doAction() 
			{
				boolean classPicked = false;
				while(classPicked == false) 
				{
					System.out.println(scheduler.getAvailableClasses());
					int choice = (int)getNumber("Enter the number of the class you want to add or -1 to go back: ", -1,Integer.MAX_VALUE);
					if(choice == -1) return;
					int vertex = scheduler.getVertexFromAddList(choice);
					if(vertex == Scheduler.INVALID_INDEX) 
					{
						System.out.println(States.INVALID_INDEX.getMessage());
					}
					else 
					{
						States returnState = scheduler.addClass(vertex);
						System.out.println(returnState.getMessage());
						if(returnState == States.CLASS_ADDED || returnState == States.AT_UNIT_MAX) 
						{
							classPicked = true;
						}
					}
				}
			}
		});
		
		actions.add(new Action("Remove a class from the current semester") 
		{
			public void doAction() 
			{
				boolean doneRemoving = false;
				while(doneRemoving == false)
				{	
					System.out.println(scheduler.getSemesterStr());
					int choice = (int) getNumber("Enter the number of the class you want ot remove or -1 to go back: ", -1, Integer.MAX_VALUE);
					if(choice == -1)return;
					int vertex = scheduler.getVertexFromRemoveList(choice);
					if(vertex == Scheduler.INVALID_INDEX) 
					{
						System.out.println(States.INVALID_INDEX.getMessage());
					}
					else 
					{
						ArrayList<Integer> removedClases = scheduler.removeClass(vertex);
						System.out.println("These are the clases that were removed from the schedule:");
						for(int i = 0; i < removedClases.size(); i++) 
						{
							System.out.println("\t" + scheduler.getCourseList().get(removedClases.get(i)));
						}
						doneRemoving = true;
					}
				}
				
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
	
}	



