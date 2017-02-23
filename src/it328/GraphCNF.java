package it328;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
  public GraphCNF(int[][] data, ArrayList<Integer> nodeList, ArrayList<Integer []> clauses) {
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
    for (Integer [] cl : clauses) {
      this.clauses.add(cl);
    }
    this.nodeList = new ArrayList<Integer>();
    // deep copy nodeList
    for (int n : nodeList) {
      this.nodeList.add(n);
    }
    this.nRange = -1;
    this.truthTable = new HashMap<Integer, Boolean>();
    
    this.initTruths();
    this.initEdgeCounts();
    
    this.reduceZeroes();
  }
  
  // Initialize all truth values to FALSE
  private void initTruths() {
    for (int i = 0; i < this.clauses.size(); i++) {
      for (int j = 0; j < this.clauses.get(i).length; j++) {
        int k = this.clauses.get(i)[j];
        if (k != 0) {
          this.setTruth(k, false);
        }
      }
    }
  }
  
//~~~~~~~~~~~~~~~~~~~~~~~~ GETTERS AND SETTERS START
  
  public Integer[] getClause(int n) {
    return this.clauses.get(n);
  }
  
  public ArrayList<Integer[]> getClauses() {
    return this.clauses;
  }
  
  public ArrayList<Integer> getNodeList() {
    return this.nodeList;
  }
  
  public Map<Integer, Boolean> getTruthTable() {
    return this.truthTable;
  }
 
  // Get the range of node name values (starting at 1)
  public int getNRange() {
    return this.nRange;
  }
  
  // Set the range of node values
  public void setNRange(int n) {
    this.nRange = n;
  }
  
  // Get the number of clauses at beginning 
  public int getNumClauses() {
    return this.clauses.size();
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
  
  public String getAssignmentsStr() {
    // get formatted string of truth assignments
    ArrayList<String> assignments = new ArrayList<String>();
    for (int k = 1; k <= this.getNRange(); k++) {
      
      // convert boolean value to T/F string
      String s = "T";
      if (!this.getTruth(k)) {
        s = "F";
      }
      assignments.add("A" + k + "=" + s);
    }
    String assignmentsStr = String.join(" ", assignments);
    return assignmentsStr;
  }
  
  // ~~~~~~~~~~~~~~~~~~~~~~~~ GETTERS AND SETTERS END
  
  public boolean presolve() {
    
    if (this.clauses.size() == 0) {
      return false;
    }
    
    Map<Integer, Boolean> known  = new HashMap<Integer, Boolean>();
    boolean changed = true;
    while (changed && known.size() < this.nRange) {
      changed = false;
      
      for (int c = 0; c < this.clauses.size(); c++) {
        Integer [] clause = this.clauses.get(c);
        
        // temporarily convert known false values to 0s
        for (int i = 0; i < clause.length; i++) {
          if (known.containsKey(clause[i]) && known.get(clause[i]) == false) {
            clause[i] = 0;
          }
        }
        
        // First, check for all values being the same
        Set<Integer> uniqueVals = new HashSet<Integer>();
        for (Integer n : clause) {
          if ((!uniqueVals.contains(n)) && n != 0) {
            uniqueVals.add(n);
          }
        }
        if (uniqueVals.size() == 1) {
          int unique = (int)uniqueVals.toArray()[0];
          if (!known.containsKey(unique)) {
            // clause contains only one unique value, therefore it must be true (ex: (3, 3, 3))
            known.put(unique, true);
            // the inverse value must then be false
            known.put(unique*-1, false);
            changed = true;
            break;
          }
        }
        
        // Next, check if there is a current contradiction
        boolean aTruth = false;
        for (int i = 0; i < clause.length; i++) {
          if (clause[i] == 0) {
            continue;
          }
          if (known.containsKey(clause[i]) && known.get(clause[i]) == false) {
            continue;
          }
          aTruth = true;
        }
        if (!aTruth) {
          return false;
        }
      }
    }
    
    // Set truths for all keys
    for (Integer key : known.keySet()) {
      this.setTruth(key, known.get(key));
    }
    
    // Remove unnecessary clauses from clauses data structure
    for (int cl = 0; cl < this.clauses.size(); cl++) {
      Integer [] clause = this.clauses.get(cl);
      Integer [] clCopy = Arrays.copyOf(clause, clause.length);
      changed = false;
      for (int c = 0; c < clause.length; c++) {
        if (known.containsKey(clause[c]) && known.get(clause[c]) == false) {
          clCopy[c] = 0;
          changed = true;
        }
      }
      if (changed) {
        this.clauses.set(cl, clCopy);
      }
    }
    // Reduce matrix size accordingly
    for (int i = 0; i < this.size(); i++) {
      if (known.containsKey(this.nodeList.get(i)) && known.get(this.nodeList.get(i)) == false) {
        this.nodeList.set(i, 0);
        for (int z = 0; z < this.size(); z++) {
          if (this.matrix[i][z] == 1) {
            this.matrix[i][z] = 0;
            this.matrix[z][i] = 0;
            this.numEdges--;
          }
        }
      }
    }
    this.reduceZeroes();
    
    return true;
  }
  
  private void reduceZeroes() {
    int newSize = this.size();
    ArrayList<Integer> newNodeList = new ArrayList<Integer>();
    
    // get indeces of zero'd rows/cols
    Set<Integer> zeroes = new HashSet<Integer>();
    for (int i = 0; i < this.nodeList.size(); i++) {
      if (this.nodeList.get(i) == 0) {
        zeroes.add(i);
        newSize--;
      } else {
        newNodeList.add(this.nodeList.get(i));
      }
    }
    
    int newNEdges = 0;
    
    // make new matrix with reduced size
    int[][] newMatrix = new int[newSize][newSize];
    int x = 0;
    
    for (int i = 0; i < this.size(); i++) {
      int[] col = new int[newSize];
      int y = 0;
      if (!zeroes.contains(i)) {
        for (int j = 0; j < this.size(); j++) {
          if (!zeroes.contains(j)) {
            col[y] = this.matrix[i][j];
            if (i == j) {
              col[y] = 0;
            }
            
            // keep track of new number of edges
            if (col[y] == 1) {
              newNEdges++;
            }
            y++;
          }
        }
        newMatrix[x] = col;
        x++;
      }
    }
    this.matrix = newMatrix;
    this.nodeList = newNodeList;
    this.numEdges = newNEdges / 2;
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
