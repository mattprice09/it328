package it328;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Extends Graph to provide the handling of 3CNF data. Stores data about the 3CNFs clauses, truth values, etc.
 * @author Kevin Houlds, Matt Price
 */
public class GraphCNF extends Graph {

  // list of clauses, with clauses represented as length=3 arrays containing node names
  private ArrayList<Integer[]> clauses;
  // original ordered list of the cnf statement
  private ArrayList<Integer> nodeList;
  // the number of unique, absolute value variables in the cnf clause (e.g. [1, 2, -1, -2] has 2)
  private int nRange;
  // maps node names to truth values. note: if 2 is true, that means -2 is false (and vice-versa)
  private Map<Integer, Boolean> truthTable;
  
  // default constructor
  public GraphCNF() {
    super();
    
    this.clauses = new ArrayList<Integer[]>();
    this.nodeList = new ArrayList<Integer>();
    this.nRange = -1;
    this.truthTable = new HashMap<Integer, Boolean>();
    this.initTruths();
  }
  
  // construct from matrix
  public GraphCNF(int [][] data) {
    super(data);
    
    this.clauses = new ArrayList<Integer[]>();
    this.nodeList = new ArrayList<Integer>();
    this.nRange = -1;
    this.truthTable = new HashMap<Integer, Boolean>();
    this.initTruths();
  }
  
  /**
   * Constructor for 3CNF graphs
   */
  public GraphCNF(int[][] data, ArrayList<Integer> nodeList) {
    super();
    
    // deep copy matrix data
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
    
    this.clauses = new ArrayList<Integer[]>();
    this.nodeList = new ArrayList<Integer>();
    // deep copy nodeList
    for (int n : nodeList) {
      this.nodeList.add(n);
    }
    this.nRange = -1;
    this.truthTable = new HashMap<Integer, Boolean>();
    
    this.initTruths();
  }
  
  // Initialize all truth values to FALSE
  private void initTruths() {
    for (int i = 0; i < this.clauses.size(); i++) {
      for (int j = 0; j < this.clauses.get(i).length; j++) {
        int k = this.clauses.get(i)[j];
        this.setTruth(k, false);
      }
    }
  }
 
  // Get the range of node name values (starting at 1)
  public int getNRange() {
    return this.nRange;
  }
  
  // Set the range of node values
  public void setNRange(int n) {
    this.nRange = n;
  }
  
  // Get a variable's truth value
  public boolean getTruth(int key) {
    return this.truthTable.get(key);
  }

  // Set a variable's truth value
  public void setTruth(int key, boolean val) {
    
    if (this.truthTable.containsKey(key)) {
      this.truthTable.replace(key, val);
    } else {
      this.truthTable.put(key, val);
    }
  }
  
  /**
   * Set TRUE and FALSE values from clique results
   * @param cliques
   *    arraylist containing integers that represent nodes 
   */
  public void setCliqueTruths(ArrayList<Integer> clique) {
    for (int ind : clique) {
      int k = this.nodeList.get(ind);
      this.setTruth(k, true);
      this.setTruth(k*-1, false);
    }
  }
}
