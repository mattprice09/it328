package it328;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Helpers {
  
  /**
   * Get the time elapsed since a given starting time
   * @param sTime
   *    The starting time, in ms
   * @param unit
   *    Unit of time measurement. Current options are either seconds or milliseconds
   * @return
   *    Returns the time elapsed as a double
   */
  public static double getTimeElapsed(long sTime, String unit) {
    long eTime = System.currentTimeMillis();
    long tDelta = eTime - sTime;
    
    if (unit.equals("ms") || unit.equals("milliseconds")) {
      return (double)tDelta;
    } else if (unit.equals("seconds") || unit.equals("sec")) {
      return tDelta / 1000.0;
    }
    
    // user entered invalid unit string
    System.out.println("> ERROR: Units of time must be either seconds or milliseconds.");
    System.exit(0);
    return -1;
  }
  
  /**
   * Prints a Graph's matrix
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
   * Reads graph matrices/metadata from a given file name
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
      
      // graph size of 0 indicates end of graph file
      if (numNodes == 0) {
        return graphs;
      }
      
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
  public static ArrayList<GraphCNF> read3CNF(String fname)
  {   
    int nRange;
    Scanner reader = null;
    ArrayList<GraphCNF> graphs = new ArrayList<GraphCNF>();
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
        nRange = Integer.parseInt(parts[0]);//here num nodes refers to number of unique boolean primitives
        
        for(int i = 1; i < parts.length; i++)
        {
          nodeList.add(Integer.parseInt(parts[i]));
        }
        
        matrix = new int[nodeList.size()][nodeList.size()];
        GraphCNF graph = buildCNFgraph(matrix, nodeList);
        graph.setNRange(nRange);
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
  public static GraphCNF buildCNFgraph(int[][] matrix, ArrayList<Integer> nodeList)
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
    GraphCNF graph = new GraphCNF(matrix, nodeList);
    return graph;
  }
}
