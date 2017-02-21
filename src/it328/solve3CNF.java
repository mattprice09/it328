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
    ArrayList<Graph> graphs = Helpers.read3CNF(fname);
    
    System.out.printf("* Solve 3CNF in %s: (reduced to K-Clique) *", fname);
    
    // solve each 3CNF graph
    int i = 0;
    for (Graph graph : graphs) {
      i++;
      
//      Helpers.printGraph(graph);
      
      // Get the max clique for the graph
      long sTime = System.currentTimeMillis();
      ArrayList<Integer> maxClique = graph.maxClique(null);
      double t = Helpers.getTimeElapsed(sTime, "ms");
     
      if (maxClique.size() < graph.size() / 3) {
        // no solution to 3CNF
        System.out.printf("3CNF No.%d: [n=%d k=%d] No %d-clique; no solution (%.4f ms)", i, graph.getNRange(), graph.size() / 3, graph.size() / 3, t);
      
      } else if (maxClique.size() == graph.size() / 3) {
        // found solution to 3CNF
        
        graph.setCliqueTruths(maxClique);
        
        // get formatted string of truth assignments
        ArrayList<String> assignments = new ArrayList<String>();
        for (int k = 1; k <= graph.getNRange(); k++) {
          
          // convert boolean value to T/F string
          String s = "T";
          if (!graph.getTruth(k)) {
            s = "F";
          }
          assignments.add("A" + k + "=" + s);
        }
        String assignmentsStr = String.join(" ", assignments);
        
        System.out.printf("3CNF No.%d: [n=%d k=%d] Assignments:[%s] (%.4f ms)\n", i, graph.getNRange(), graph.size() / 3, assignmentsStr, t);
      }
      System.out.print("\n");
      
//      if (i == 9) {
//        System.exit(1);
//      }
    }
  }
}
