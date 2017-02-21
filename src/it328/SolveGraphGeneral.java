package it328;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Solve a graph problem as indicated from Ilstu IT328 assignment 1. Solution type options:
 * 1 -> solve clique
 * 2 -> solve independent set
 */
public class SolveGraphGeneral {
  
  // valid choices of graph solutions
  private static final Integer[] TYPES = {1, 2};
  private static final Set<Integer> SOLUTION_TYPES = new HashSet<Integer>(Arrays.asList(TYPES));

  public static void solveGraph(String fname, int solutionType) {
    
    // check for valid solution type
    if (!SOLUTION_TYPES.contains(solutionType)) {
      System.out.println("> ERROR: Invalid solution type.");
      System.exit(0);
    }
    
    // read graphs from file
    ArrayList<Graph> graphs = Helpers.readFile(fname);
    
    if (solutionType == 1) {
      System.out.printf("*  Max Cliques in graphs in %s\n", fname);
      System.out.println("    (|V|,|E|) Cliques (size, ms used)");
    } else if (solutionType == 2) {
      System.out.printf("* Max Independent Sets in graphs in %s : (reduced to K-Clique) *\n", fname);
      System.out.println("    (|V|,|E|) Independent Set (size, ms used)");
    }
    
    // solve independent set for each graph
    int i = 0;
    for (Graph graph : graphs) {
      i++;
      
      // we're solving independent set
      if (solutionType == 2) {
        graph = graph.getComplement();
      }

      // get max clique for the graph's compliment
      long sTime = System.currentTimeMillis();
      ArrayList<Integer> maxClique = graph.maxClique(null);
      double t = Helpers.getTimeElapsed(sTime, "ms");
      
      // format output
      List<String> strClique = maxClique.stream().map(
        n -> n.toString()).collect(Collectors.toList()
      );
      String s = String.join(",", strClique);
      
      System.out.printf("G%d (%d, %d) {%s} (size=%d, %.1f ms)\n", i, graph.size(), graph.getNumEdges(), s, maxClique.size(), t);
    }
  }
}
