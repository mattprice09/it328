package it328;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Asg1 {
  
  /**
   * Prints a graph (2D int array) row by row
   */
  public static void printGraph(Graph graph) {
    for (int i = 0; i < graph.size(); i++) {
      String [] row = new String[graph.size()];
      for (int j = 0; j < graph.size(); j++) {
        row[j] = graph.getEdge(i,  j) + "";
      }
      System.out.println(String.join(" ", row));
    }
  }
  
  /**
   * Reads graphs from a given file name
   */
  public static ArrayList<Graph> readFile(String fname) {
    int numNodes;
    
    // Open file for viewing
    Scanner reader = null;
    try {
      reader = new Scanner(new File(fname));
    } catch (FileNotFoundException e) {
      System.out.println("> ERROR: Invalid file name");
      System.exit(0);
    }
    
    ArrayList<Graph> graphs = new ArrayList<Graph>();
    
    // Read in multiple graphs
    while (reader.hasNextLine()) {
      
      // first line contains the number of nodes
      String line = reader.nextLine();
      numNodes = Integer.parseInt(line);
      
      // 2-D array to represent graph matrix
      int[][] matrix = new int[numNodes][numNodes];
      
      // read each line of the matrix
      for(int i = 0; i < matrix.length; i++) {
        line = reader.nextLine();
        String [] parts = line.split(" ");
        for (int j = 0; j < parts.length; j++) {
          matrix[i][j] = Integer.parseInt(parts[j]);
        }
      }
      Graph graph = new Graph(matrix);
      graphs.add(graph);
    }
    return graphs;
  }

  public static void main(String[] args) {
    
    // Accepts a graphs file name as the first and only arg
    if (args.length != 1) {
      System.out.println("Please provide a file name contaiby referencening a graph matrix.");
      System.exit(0);
    }
    String fname = args[0];
    
    // read graphs from file
    ArrayList<Graph> graphs = readFile(fname);
    
    // print all graphs to console
    int i = 0;
    for (Graph graph : graphs) {
      i++;
      System.out.println(">>> Graph " + i);
      printGraph(graph);
    }
  }
}
