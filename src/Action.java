
public abstract class Action
{
	private String description;
	
	public Action(String description) 
	{
		this.description = description;
	}
	
	public abstract void doAction();
	
	public String getDescription() 
	{
		return description;
	}
	
	public String toString() 
	{
		return description;
	}
}
