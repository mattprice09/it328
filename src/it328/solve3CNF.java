package it328;

import java.util.ArrayList;

public class solve3CNF {

  public static void main(String[] args) {
    
    // Accepts a graphs file name as the first and only arg
    if (args.length != 1) {
      System.out.println("Please provide a file name contaiby referencening a graph matrix.");
      System.exit(0);
    }
    String fname = args[0];
    
    // read graphs from file
    ArrayList<Graph> graphs = Helpers.read3CNF(fname);
    
    System.out.printf("* Solve 3CNF in %s: (reduced to K-Clique) *", fname);
    
    // Solve each 3CNF graph
    int i = 0;
    for (Graph graph : graphs) {
      i++;
//      System.out.println(">>> Graph " + i);

      // t will be the time in milliseconds it took to get this solution
      int t = 5;
      
      Helpers.printGraph(graph);
      
      // Get the max clique for the graph
      ArrayList<Integer> maxClique = graph.maxClique(null);
     
      if (maxClique.size() < graph.size() / 3) {
        // No solution to the 3CNF
        System.out.printf("3CNF No.%d: [n=%d k=%d] No %d-clique; no solution (%d ms", i, graph.getNRange(), graph.size() / 3, graph.size() / 3, t);
      } else if (maxClique.size() == graph.size()) {
        // Found solution to 3CNF. Print results
      }
      
//      3CNF No.1:[n=3 k=9] No 9-clique; no solution (3 ms)
//      3CNF No.2:[n=3 k=8] Assignments:[A1=T A2=F A3=F ](0 ms)
      
//      System.out.println(maxClique);
//      System.out.println("Number of nodes in clique: " + maxClique.size());
//      System.out.println("Graph size: " + graph.size() + ", number of clauses: " + graph.size() / 3);
      if (i == 9) {
        System.exit(1);
      }
    }
  }
}
