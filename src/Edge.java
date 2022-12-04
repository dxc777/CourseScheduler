

class Edge
{
	public int adjVertex;
	
	public int weight;
	
	public Edge next;
	
	public Edge() {}

	public Edge(int adjVertex, int weight, Edge next)
	{
		this.adjVertex = adjVertex;
		this.weight = weight;
		this.next = next;
	}
	
	
}
