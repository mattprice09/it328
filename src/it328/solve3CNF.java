package it328;

import java.util.ArrayList;

/**
 * Solve 3CNF problems by reducing CNF logical propositions to K-cliques and making determinations from there
 * @author Kevin Houlds, Matt Price
 */
public class solve3CNF {

  public static void main(String[] args) {
    
    // Require file path for 3cnf file
    if (args.length != 1) {
      System.out.println("Please provide a file name contaiby referencening a graph matrix.");
      System.exit(0);
    }
    String fname = args[0];
    
    // read graphs from file
    ArrayList<GraphCNF> graphs = Helpers.read3CNF(fname);
    
    System.out.printf("* Solve 3CNF in %s: (reduced to K-Clique) *\n", fname);
    
    // solve each 3CNF graph
    int i = 0;
    for (GraphCNF graph : graphs) {
      i++;
      
//      Helpers.printCNFGraphLabelledMatrix(graph);
//      Helpers.printCNFGraphClauses(graph);
      
//      int nBefore = graph.getNumEdges();
//      System.out.println("Presolving...");
      graph.presolve();
//      System.out.println("Done presolving...");
//      System.out.println("------");
//      Helpers.printCNFGraphLabelledMatrix(graph);
//      Helpers.printCNFGraphClauses(graph);
//      System.out.printf("# edges before: %d, # edges after: %d\n", nBefore, graph.getNumEdges());
      
      // Get the max clique for the graph
      long sTime = System.currentTimeMillis();
      ArrayList<Integer> maxClique = graph.maxClique(null);
      double t = Helpers.getTimeElapsed(sTime, "ms");

//      for (int k = 0; k < maxClique.size(); k++) {
//        System.out.printf("%d, ", graph.getNodeList().get(maxClique.get(k)));
//      }
//      System.out.println();
     
      if (maxClique.size() < graph.getNumClauses()) {
        // no solution to 3CNF
        System.out.printf("3CNF No.%d: [n=%d k=%d] No %d-clique; no solution (%.4f ms)\n", i, graph.getNRange(), graph.size() / 3, graph.size() / 3, t);
      
      } else if (maxClique.size() == graph.getNumClauses()) {
        // found solution to 3CNF
        
        graph.setCliqueTruths(maxClique);
        
        String assignmentsStr = graph.getAssignmentsStr();
        
        System.out.printf("3CNF No.%d: [n=%d k=%d] Assignments:[%s] (%.4f ms)\n", i, graph.getNRange(), graph.size() / 3, assignmentsStr, t);
      }
//      if (i == 1) {
//        System.exit(1);
//      }
    }
  }
}
