package it328;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Helpers {
  
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

  /**
   * Will read in 3cnf file
   * @param fname
   * @return
   */
  public static ArrayList<Graph> read3CNF(String fname)
  {   
    int numNodes;
    Scanner reader = null;
    ArrayList<Graph> graphs = new ArrayList<Graph>();
    try{
      reader = new Scanner(new File(fname));
    }catch(FileNotFoundException e)
    {
      System.out.println("> ERROR: Invalid file name");
      System.exit(0);
    }
    
    ArrayList<Integer> myList = new ArrayList<Integer>();
    while(reader.hasNextLine())
    {
      String line = reader.nextLine();
      ArrayList<Integer> nodeList = new ArrayList<Integer>();
      int [][] matrix;
      /* a line of just 0 signals the end of file
       * We do not need this */
      if(!line.equals("0"))
      {
        String [] parts = line.split(" ");
        numNodes = Integer.parseInt(parts[0]);//here num nodes refers to number of unique boolean primitives
        
        for(int i = 1; i < parts.length; i++)
        {
          nodeList.add(Integer.parseInt(parts[i]));
        }
        
        matrix = new int[nodeList.size()][nodeList.size()];
        Graph graph = buildCNFgraph(matrix, nodeList);
        graphs.add(graph); 
      }
    }
    return graphs;
  }
  
  /**
   * Will build a graph for 3CNF 
   * @param matrix
   * @param nodeList
   * @return
   */
  public static Graph buildCNFgraph(int[][] matrix, ArrayList<Integer> nodeList)
  {
    int clauseFlag = 1;
    int node;
    int node_j; 
    for(int i = 0; i < matrix.length; i++)
    {
      for(int j = 0; j<matrix.length; j++)
      {
        node = nodeList.get(i);
        node_j = nodeList.get(j);
        
        if(clauseFlag == 1)
        {
          //cannot have edge with nodes in front of it by 2
          
          if(i == j)//cannot have edge to itself in the same clause
          {
            matrix[i][j] = 0;
          }
          else if((i == j +2) || (i == j + 1))//checking the 2 in front
          {
            matrix[i][j] = 0;
          }
          else if((node + node_j) == 0)//cannot have edge with negation
          {
            matrix[i][j] = 0;
          }
          else
          {
            matrix[i][j] = 1;
          }
          
          clauseFlag = 2;
        }
        else if(clauseFlag == 2)
        {
          //cannot have edge with nodes 1 in front or 1 in behind
          
          if(i == j)//cannot have edge to itself
          {
            matrix[i][j] = 0;
          }
          else if((i == j + 1) || (i == j - 1))//checking 1 in front and 1 in back
          {
            matrix[i][j] = 0;
          }
          else if((node + node_j) == 0)//checking negation
          {
            matrix[i][j] = 0;
          }
          else
          {
            matrix[i][j] = 1;
          } 
          clauseFlag = 3;
        }
        else if(clauseFlag == 3)
        {
          //cannot have edges with nodes 2 nodes behind it
          
          if(i == j)//cannot have edge to itself
          {
            matrix[i][j] = 0;
          }
          else if((i == j - 1) || (i == j - 2))//checking the two nodes behind it
          {
            matrix[i][j] = 0;
          }
          else if((node + node_j) == 0)//cannot have edge with negation
          {
            matrix[i][j] = 0;
          }
          else
          {
            matrix[i][j] = 1;
          } 
          
          clauseFlag = 1;
        }
      }
    }
    Graph graph = new Graph(matrix, nodeList);
    return graph;
  }
}
