

public interface Graph
{
	public int nodeCount();
	
	public void addEdge(int vertex, int adjVertex, int weight);
	
	public void removeEdge(int vertex, int adjVertex);
	
	public boolean hasEdge(int vertex, int adjVertex);
	
	public int weight(int vertex, int adjVertex);
	
	public void setValue(int vertex, int value);
	
	public int getValue(int vertex);
	
	public void fillValues(int value);
	
	public int[] neighbors(int vertex);
}
