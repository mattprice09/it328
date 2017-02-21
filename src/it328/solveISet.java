package it328;

import java.util.ArrayList;

public class solveISet {

  public static void main(String[] args) {
    
    // Accepts a graphs file name as the first and only arg
    if (args.length != 1) {
      System.out.println("Please provide a file name contaiby referencening a graph matrix.");
      System.exit(0);
    }
    String fname = args[0];
    
    // read graphs from file
    ArrayList<Graph> graphs = Helpers.readFile(fname);
    
    System.out.printf("* Solve 3CNF in %s: (reduced to K-Clique) *", fname);
    
    // Solve each 3CNF graph
    int i = 0;
    for (Graph graph : graphs) {
      i++;
      
      graph = graph.getComplement();

      // t will be the time in milliseconds it took to get this solution
      long sTime = System.currentTimeMillis();
      
//      Helpers.printGraph(graph);
      
      // Get the max clique for the graph
      ArrayList<Integer> maxClique = graph.maxClique(null);
      
      double t = Helpers.getTimeElapsed(sTime, "ms");
     
      // OUTPUT results
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
        
        // print assignments
        System.out.printf("3CNF No.%d: [n=%d k=%d] Assignments:[%s] (%.4f ms)\n", i, graph.getNRange(), graph.size() / 3, assignmentsStr, t);
      }
      System.out.print("\n");
      
//      if (i == 9) {
//        System.exit(1);
//      }
    }
  }
}
