package it328;

/**
 * Find max K-cliques of graphs
 * @author Kevin Houlds, Matt Price
 */
public class solveClique {
  
  public static void main(String[] args) {
    
    // Require file path for graph file
    if (args.length != 1) {
      System.out.println("Please provide a file path in the IT328 graph matrix format");
      System.exit(0);
    }
    String fname = args[0];
    
    SolveGraphGeneral.solveGraph(fname, 1);
  }
}
