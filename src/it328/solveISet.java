package it328;

/**
 * Solve independent set graph problems by finding the max clique inverted graph(s)
 * @author Kevin Houlds, Matt Price
 */
public class solveISet {

  public static void main(String[] args) {
    
    // Require file path for graph file
    if (args.length != 1) {
      System.out.println("Please provide a file path in the IT328 graph matrix format");
      System.exit(0);
    }
    String fname = args[0];
    
    SolveGraphGeneral.solveGraph(fname, 2);
  }
}
