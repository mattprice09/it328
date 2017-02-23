package it328;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
      
      // Found out before running K-cliques that it is unsolveable
      boolean solveable = graph.presolve();
      if (!solveable) {
        System.out.printf("3CNF No.%d: [n=%d k=%d] No %d-clique; no solution (%.4f ms)\n", i, graph.getNRange(), graph.getNumClauses(), graph.getNumClauses(), 0.0000);
        continue;
      }
      
//      Helpers.printCNFGraphLabelledMatrix(graph);
//      Helpers.printCNFGraphClauses(graph);
      
      // Get the number of unsolved clauses
      int unsolvedClauses = 0;
      Set<Integer> skipNodes = new HashSet<Integer>();
      int counter = 0;
      for (Integer[] clause : graph.getClauses()) {
        ArrayList<Integer> nonZeroes = new ArrayList<Integer>();
        for (int c = 0; c < clause.length; c++) {
          if (clause[c] != 0) {
            nonZeroes.add(counter);
            counter++;
          }
        }
        if (nonZeroes.size() > 1) {
          unsolvedClauses++;
        } else {
          // skip clauses with only 1 value remaining
          for (int nz : nonZeroes) {
            skipNodes.add(nz);
          }
        }
      }
      
      // Get the max clique for the graph
      long sTime = System.currentTimeMillis();
      ArrayList<Integer> maxClique = graph.maxClique(unsolvedClauses, skipNodes, null);
      double t = Helpers.getTimeElapsed(sTime, "ms");
      
//      System.out.printf("max clique size: %d, numClauses(): %d\n", maxClique.size(), graph.getNumClauses());
     
      if (maxClique.size() < graph.getNumClauses()) {
        // no solution to 3CNF
        System.out.printf("3CNF No.%d: [n=%d k=%d] No %d-clique; no solution (%.4f ms)\n", i, graph.getNRange(), graph.getNumClauses(), graph.getNumClauses(), t);
      
      } else if (maxClique.size() == graph.getNumClauses()) {
        // found solution to 3CNF
        
        graph.setCliqueTruths(maxClique);
        
        String assignmentsStr = graph.getAssignmentsStr();
        
        System.out.printf("3CNF No.%d: [n=%d k=%d] Assignments:[%s] (%.4f ms)\n", i, graph.getNRange(), graph.getNumClauses(), assignmentsStr, t);
      }
    }
  }
}
