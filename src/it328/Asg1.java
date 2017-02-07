package it328;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Asg1 {
  
  /**
   * Prints a graph (2D int array) row by row
   */
  public static void printGraph(int[][] graph) {
    for (int i = 0; i < graph.length; i++) {
      String [] row = new String[graph.length];
      for (int j = 0; j < graph.length; j++) {
        row[j] = graph[i][j] + "";
      }
      System.out.println(String.join(" ", row));
    }
  }
	
  /**
   * Reads graphs from a given file name
   */
	public static ArrayList<int[][]> readFile(String fname) {
	  int numNodes;
	  
	  // Open file for viewing
	  Scanner reader = null;
		try {
      reader = new Scanner(new File(fname));
    } catch (FileNotFoundException e) {
      System.out.println("> ERROR: Invalid file name");
      System.exit(0);
    }
		
		ArrayList<int[][]> graphs = new ArrayList<int[][]>();
		
		// Read in multiple graphs
		while (reader.hasNextLine()) {
		  
		  // first line contains the number of nodes
	    String line = reader.nextLine();
	    numNodes = Integer.parseInt(line);
	    
	    // 2-D array to represent graph matrix
	    int[][] graph = new int[numNodes][numNodes];
	    
	    // read each line of the matrix
	    for(int i = 0; i < graph.length; i++) {
	      line = reader.nextLine();
	      String [] parts = line.split(" ");
	      for (int j = 0; j < parts.length; j++) {
	        graph[i][j] = Integer.parseInt(parts[j]);
	      }
	    }
	    graphs.add(graph);
		}
		
		return graphs;
	}

	public static void main(String[] args) {
	  
	  // Accepts a graphs file name as the first and only arg
		if (args.length != 1) {
			System.out.println("Please provide a file name containing a graph matrix.");
			System.exit(0);
		}
		String fname = args[0];
		
		// read graphs from file
		ArrayList<int[][]> graphs = readFile(fname);
		
		// print all graphs to console
		int i = 0;
		for (int[][] graph : graphs) {
		  i++;
		  System.out.println(">>> Graph " + i);
	    printGraph(graph);
		}
	}
}
