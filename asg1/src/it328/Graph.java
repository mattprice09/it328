package it328;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Graph class that represents an undirected, unweighted graph. Provides functionality for finding K-Cliques
 * @author Kevin Houlds, Matt Price
 */
public class Graph {
  
  protected int[][] matrix;
  protected int numEdges;
  
  /**
   * Default constructor
   */
  public Graph() {
    this.matrix = new int[0][0];
    this.numEdges = 0;
    
    this.initEdgeCounts();
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
    this.initEdgeCounts();
  }
 
  // get number of edges
  public int getNumEdges() {
    return this.numEdges;
  }
  
  // set number of edges
  public void setNumEdges(int n) {
    this.numEdges = n;
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
   * Set an edge value in the matrix
   */
  public void setEdge(int i, int j, int val) {
    if ((i < 0 || i > this.size()) || (j < 0 || j > this.size())) {
      System.out.println("> ERROR: Matrix indeces are out of bounds.");
      System.exit(0);
    }
    if (val < -1 && val > 1) {
      System.out.println("> ERROR: Matrix value must be -1, 0 or 1.");
      System.exit(0);
    }
    this.matrix[i][j] = val;
  }
  

  //Initialize edge count for graph (e.g. the number of 1's in the matrix)
  protected void initEdgeCounts() {
    this.numEdges = 0;
    for (int i = 0; i < this.size(); i++) {
      for (int j = 0; j < this.size(); j++) {
        if (this.matrix[i][j] == 1) {
          this.numEdges++;
        }
      }
    }
    this.numEdges /= 2;
  }
  
  
  /**
   * Get the size of the graph (# of nodes)
   */
  public int size() {
    return this.matrix.length;
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
  
  // Recursive method used to find all K-cliques
  @SuppressWarnings("unchecked")
  private void findCliques(int k, ArrayList<ArrayList<Integer>> found, ArrayList<Integer> potentialClique,
                                                    ArrayList<Integer> remainingNodes, ArrayList<Integer> skipNodes) {
    if (remainingNodes.size() == 0 && skipNodes.size() == 0) {
      found.add(potentialClique);
      return;
    }
    
    for (int n = 0; n < remainingNodes.size(); n++) {
      int node = remainingNodes.get(n);
      ArrayList<Integer> neighbors = this.adjacentNodes(node);
      
      // Add current node to potential nodes
      ArrayList<Integer> newPotential = (ArrayList<Integer>)potentialClique.clone();
      newPotential.add(node);
      
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
      
      this.findCliques(k, found, newPotential, newRemaining, newSkips);
      
      remainingNodes.remove(n);
      skipNodes.add(node);
    }
  }
  /**
   * Get all K-Cliques in the graph
   * @return
   *    An ArrayList containing all of the K-cliques, represented as arrays of node IDs
   */
  public ArrayList<ArrayList<Integer>> findKCliques(int k, Set<Integer> initialSkips) {
    
    ArrayList<ArrayList<Integer>> cliques = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> nodes = new ArrayList<Integer>();
    for (int i = 0; i < this.size(); i++) {
      
      if (k != -1) {
        // filter out any nodes that have no chance of being in the clique
        if (initialSkips.contains(i)) {
          continue;
        }
        
        // attempt to compare the number of clauses this node is related to and the K value
        // this segment does not work :(
        // @ Dr. Li, if you see this, how far off were we here? 
//        Set<Integer> neighborClauses = new HashSet<Integer>();
//        ArrayList<Integer> neighbors = this.adjacentNodes(i);
//        for (int neigh : neighbors) {
//          int clause = neigh / 3;
//          if (!neighborClauses.contains(clause)) {
//            neighborClauses.add(clause);
//          }
//        }
//        if (neighborClauses.size() >= k) {
//          nodes.add(i);
//        }
        nodes.add(i);
      } else {
        // no need to reduce
        nodes.add(i);
      }
    }
    
    // in-place assignment of cliques
    this.findCliques(k, cliques, new ArrayList<Integer>(), nodes, new ArrayList<Integer>());
    
    return cliques;
  }

  /**
   * Will return the compliment of a graph 
   * @return The compliment 
   */
  public Graph getComplement() {
    Graph compliment = new Graph(this.matrix);
    for(int i = 0; i < this.matrix.length; i++) {
      for(int j = 0; j < this.matrix.length; j++)
      {
        if(i == j)
        {
          compliment.setEdge(i, j, 0);
        }
        else if(compliment.getEdge(i,j) == 0)
        {
          compliment.setEdge(i, j, 1);
        }
        else
        {
          compliment.setEdge(i, j, 0);
        }
      }
    }
    return compliment;
  }
  
  /**
   * Get the biggest clique from a list of all cliques of the graph. 
   * @param cliques
   *    The list of cliques found from findKCliques(). If null, will call Graph.findKCliques()
   * @return
   *    An ArrayList containing all integers in the max clique
   */
  public ArrayList<Integer> maxClique(int k, Set<Integer> initialSkips, ArrayList<ArrayList<Integer>> cliques) {
    
    // If cliques parameter is null, function will internally call findKCliques()
    if (cliques == null) {
      cliques = this.findKCliques(k, initialSkips);
    }
    
    int ind = 0;
    int high = cliques.get(0).size();
    for (int i = 1; i < cliques.size(); i++) {
      if (cliques.get(i).size() > high) {
        ind = i;
        high = cliques.get(i).size();
      }
    }
    
    // sort and return max clique
    ArrayList<Integer> maxCl = cliques.get(ind);
    Collections.sort(maxCl);
    return maxCl;
  }
}
