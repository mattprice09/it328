package it328;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class solveISet {

  
  public static void main(String[] args) {
    
    // Accepts a graphs file name as the first and only arg
    if (args.length != 1) {
      System.out.println("Please provide a file name contaiby referencening a graph matrix.");
      System.exit(0);
    }
    String fname = args[0];
    
    // read graphs from file
    ArrayList<Graph> graphs = Helpers.read3CNF(fname);
//    ArrayList<Graph> graphs = readFile(fname);
    int i = 0;
    for (Graph graph : graphs) {
      i++;
      System.out.println(">>> Graph " + i);
      Helpers.printGraph(graph);
      ArrayList<ArrayList<Integer>> cliques = graph.findKCliques();
      ArrayList<Integer> maxClique = graph.maxClique(cliques);
//      if (maxClique.size() < graph.size() / 3) {
//        System.out.printf("3CNF No.%i: [n=");
//      }
      System.out.println(maxClique);
      System.out.println("Number of nodes in clique: " + maxClique.size());
      System.out.println("Graph size: " + graph.size() + ", number of clauses: " + graph.size() / 3);
      if (i == 9) {
        System.exit(1);
      }
    }
  }
}

