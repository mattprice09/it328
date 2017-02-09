package it328;

import java.util.ArrayList;

public class Graph {
  
  private int[][] matrix;
  
  /**
   * Default constructor
   */
  public Graph() {
    this.matrix = new int[0][0];
  }
  
  /** 
   * Create the graph from a 2-D array
   */
  public Graph(int[][] data) {
    this.matrix = new int[data.length][data.length];
    for (int i = 0; i < data.length; i++) {
      for (int j = 0; j < data.length; j++) {
        // automatically remove self-loops
        if (i == j) {
          this.matrix[i][j] = 0;
        } else {
          this.matrix[i][j] = data[i][j];
        }
      }
    }
  }
  
  /**
   * Get the size of the graph
   */
  public int size() {
    return this.matrix.length;
  }
  
  /**
   * Gets the edge value at i, j
   * @param i
   *    Source node
   * @param j
   *    Sink node
   * @return
   *    1 if an edge exists, 0 otherwise
   */
  public int getEdge(int i, int j) {
    if (i >= this.matrix.length || j >= this.matrix.length) {
      System.out.println("> ERROR: Edge index out of bounds");
      return -1;
    }
    return this.matrix[i][j];
  }
  
  /**
   * Set an edge v5alue in the matrix
   */
  public void setEdge(int i, int j, int val) {
    if ((i < 0 || i > 1) || (j < 0 || j > 1)) {
      System.out.println("> ERROR: Matrix value must be 0 or 1.");
      return;
    }
    this.matrix[i][j] = val;
  }
  
  // Get a list of all other nodes that a node is connected to
  private ArrayList<Integer> adjacentNodes(int n) {
    ArrayList<Integer> sinks = new ArrayList<Integer>();
    
    for (int j = 0; j < this.size(); j++) {
      if (this.getEdge(n, j) == 1) {
        sinks.add(j);
      }
    }
    return sinks;
  }
  
  /**
   * Get all K-Cliques in the graph
   * @param k
   *    The size of the cliques
   * @return
   *    An ArrayList containing all of the K-cliques, represented as arrays of node IDs
   */
  public ArrayList<int[]> findKCliques(int k) {
    
    for (int n = 0; n < this.size(); n++) {
      ArrayList<Integer> adjNodes = this.adjacentNodes(n);
      
      for (int i = 0; i < adjNodes.size(); i++) {
        for (int j = 0; j < adjNodes.size(); j++) {
          
        }
      }
    }
    
    return null;
  }
  
  /**
   * Will return the copliment of a graph 
   * @return The compliment 
   */
  public Graph getComplement() {
	Graph compliment = new Graph(this.matrix);
	for(int i = 0; i < this.matrix.length; i++)
	{
		for(int j = 0; j < this.matrix.length; j++)
		{
			if(i == j)
			{
				compliment.setEdge(i, j, 1);
			}
			else if(compliment.getEdge(i,j) == 0)
			{
				compliment.setEdge(i, j, 1);
			}
			else if(compliment.getEdge(i,j) == 1)
			{
				compliment.setEdge(i, j, 0);
			}
		}
	}
	
	
    return compliment;
  }
}
