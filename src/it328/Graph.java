package it328;

import java.util.ArrayList;
import java.util.Set;

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
  
  private void findCliques(ArrayList<ArrayList<Integer>> found, ArrayList<Integer> potentialClique,
                                                    ArrayList<Integer> remainingNodes, ArrayList<Integer> skipNodes) {
    if (remainingNodes.size() == 0 && skipNodes.size() == 0) {
      found.add(potentialClique);
      for (int j = 0; j < potentialClique.size(); j++) {
        System.out.println(potentialClique.get(j));
      }
      System.out.println();
      return;
    }
    
    for (int n = 0; n < remainingNodes.size(); n++) {
//      System.out.println(n);
      System.out.println("n: " + n);
      ArrayList<Integer> neighbors = this.adjacentNodes(n);
      for (int j = 0; j < neighbors.size(); j++) {
        System.out.println(neighbors.get(j));
      }
      
      // Add current node to potential nodes
      ArrayList<Integer> newPotential = (ArrayList<Integer>)potentialClique.clone();
      newPotential.add(n);
      
      // Filter remaining nodes by if they're in the current node's neighbors
      ArrayList<Integer> newRemaining = new ArrayList<Integer>();
      for (int n2 : remainingNodes) {
        if (neighbors.contains(n2)) {
          newRemaining.add(n2);
        }
      }
      
      // Filter skip nodes by if they're in the current node's neighbors
      ArrayList<Integer> newSkips = new ArrayList<Integer>();
      for (int n2 : skipNodes) {
        if (neighbors.contains(n2)) {
          newSkips.add(n2);
        }
      }
      
      this.findCliques(found, newPotential, newRemaining, newSkips);
      
      remainingNodes.remove(n);
      skipNodes.add(n);
    }
  }
  /**
   * Get all K-Cliques in the graph
   * @param k
   *    The size of the cliques
   * @return
   *    An ArrayList containing all of the K-cliques, represented as arrays of node IDs
   */
  public ArrayList<ArrayList<Integer>> findKCliques() {
    
    ArrayList<ArrayList<Integer>> cliques = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> nodeList = new ArrayList<Integer>();
    for (int i = 0; i < this.size(); i++) {
      nodeList.add(i);
    }
    
    this.findCliques(cliques, new ArrayList<Integer>(), nodeList, new ArrayList<Integer>());
    
    for (int i = 0; i < cliques.size(); i++) {
      for (int j = 0; j < cliques.get(i).size(); j++) {
        System.out.print(cliques.get(i).get(j));
      }
      System.out.println();
    }
    
    return cliques;
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
