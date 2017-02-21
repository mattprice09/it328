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
    int nRange;
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
      
      //Building our simple structures
      ArrayList<Integer> nodeList = new ArrayList<Integer>();
      ArrayList<Integer> nodeListTemp = new ArrayList<Integer>();
      ArrayList<Integer []> clauses = new ArrayList<Integer []>();
      ArrayList<Integer []> newClauses;
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
        
        //now we are building our clause list
        for(int i = 0; i < nodeList.size(); )
        {
        	Integer [] temp = new Integer [3];
        	for(int k = 0; k < 3; k ++)
        	{
        		temp[k] = nodeList.get(i);
        		i++;
        	}
        	
        	clauses.add(temp);
        }
       
        newClauses = simplify3CNF(clauses);
        
        //rebuilding nodeList from simplified 3CNF
        
        for(int i = 0; i < newClauses.size(); i++)
        {
        	Integer [] temp = newClauses.get(i);
        	for(int k = 0; k < 3; k++)
        	{
        		nodeListTemp.add(temp[k]);
        	}
        }
        
       //transferring nodeListTemp to nodeList
        /*for(int i = 0; i < nodeList.size(); i++)
        {
        	nodeList.set(i, nodeListTemp.get(i));
        }*/
        
        matrix = new int[nodeList.size()][nodeList.size()];
        
        Graph graph = buildCNFgraph(matrix, nodeListTemp, clauses);
        graph.setNRange(nRange);
        graphs.add(graph); 
      }
    }
    return graphs;
  }
  
  /**
   * Will eliminate redundant terms in a single clause for the 3cnf statement.
   * Any redundancy will be replaced with a zero.
   */
  
  private static ArrayList<Integer []> simplify3CNF(ArrayList<Integer []> clauses)
  {
	  ArrayList<Integer []> newClauses = new ArrayList<Integer []>();
	 for(int i = 0; i < clauses.size(); i++)
	 {
		 Integer [] temp = clauses.get(i);
		 if(temp[0] == temp[1] && temp[0] == temp[2])
		 {
			 temp[1] = 0;
			 temp[2] = 0;
			 newClauses.add(temp);
		 }
		 else if(temp[0] == temp[1])//inside 2 terms
		 {
			 temp[1] = temp[2];
			 temp[2] = 0;
			 newClauses.add(temp);
		 }
		 else if(temp[0] == temp [2])//outside 2 terms
		 {
			 temp[2] = 0;
			 newClauses.add(temp);
		 }
		 else if(temp[1] == temp[2])//last 2 terms
		 {
			 temp[2] = 0;
			 newClauses.add(temp);
		 }
		 else
		 {
			 newClauses.add(temp);
		 }
	 }
	 return newClauses; 
  }
  
  /**
   * Will build a graph for 3CNF 
   * @param matrix
   * @param nodeList
   * @return
   */
  public static Graph buildCNFgraph(int[][] matrix, ArrayList<Integer> nodeList, ArrayList<Integer []> clauses)
  {
    int clauseFlag = 1;
    int node;
    int node_j; 
    
    //copying clauses array
    for(int i = 0; i < matrix.length; i++)
    {
    	node = nodeList.get(i);
    	
    	for(int j = 0; j<matrix.length; j++)
    	{
        
    		node_j = nodeList.get(j);
        
    		if(clauseFlag == 1)
    		{
    			//cannot have edge with nodes in front of it by 2
          
    			if(i == j)//cannot have edge to itself in the same clause
    			{
    				matrix[i][j] = 0;
    			}
    			else if(node == 0 || node_j == 0)//will not create edges with zero nodes
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
          else if(node == 0 || node_j == 0)//will not create edges with zero nodes
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
          else if(node == 0 || node_j == 0)//will not create edges with zero nodes
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
    Graph graph = new Graph(matrix, nodeList, clauses);
    return graph;
  }
}
