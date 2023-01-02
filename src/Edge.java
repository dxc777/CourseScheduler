

class Edge
{
	public int adjVertex;
	
	public int weight;
	
	public Edge next;
	
	public Edge() 
	{
		adjVertex = weight = -1;
		next = null;
	}

	public Edge(int adjVertex, int weight, Edge next)
	{
		this.adjVertex = adjVertex;
		this.weight = weight;
		this.next = next;
	}
	
	
}
