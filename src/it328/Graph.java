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
        this.matrix[i][j] = data[i][j];
      }
    }
  }
  
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
   * Set an edge value in the matrix
   */
  public void setEdge(int i, int j, int val) {
    if ((i < 0 || i > 1) || (j < 0 || j > 1)) {
      System.out.println("> ERROR: Matrix value must be 0 or 1.");
      return;
    }
    this.matrix[i][j] = val;
  }
  
  /**
   * Get all K-Cliques in the graph
   * @param k
   *    The size of the cliques
   * @return
   *    An ArrayList containing all of the K-cliques, represented as arrays of node IDs
   */
  public ArrayList<int[]> findKCliques(int k) {
    return null;
  }
  
  public Graph getComplement() {
    return null;
  }
}
