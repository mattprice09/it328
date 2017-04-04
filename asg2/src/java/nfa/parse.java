package nfa;

import java.util.Scanner;

public class parse {
  
  
  
  public static DFAGraph minimize(DFAGraph original) {
    
    DFAReducer reducer = new DFAReducer(original);
    return reducer.reduce();
  }
  
  public static void main(String [] args) {
    // Handle user input
    if (args.length < 2) {
      System.out.println("> ERROR: Invalid format. Usage: 'java NFA <nfaFile> <testStrings.txt>'");
      System.exit(0);
    }
    String nfaFile = args[0];
    String stringsFile = args[1];
    
    Scanner in = new Scanner(System.in);
    
    String rootStr = "asg2/src/resources/nfa";
    for (int i = 1; i < 9; i++) {
      System.out.println("\n#################################\n");
      NFAGraph nfaGraph = new NFAGraph(rootStr+i);
      nfaGraph.printGraph();
      
      System.out.println("\n\nTo DFA:");
      DFAGraph dfaGraph = NFA.convertNFA(nfaGraph);
      dfaGraph.parseStringsFile(stringsFile);
      dfaGraph.printGraph();
      
      System.out.println("\n\nReduced DFA:");
      DFAGraph minimized = minimize(dfaGraph);
      minimized.parseStringsFile(stringsFile);
      minimized.printGraph();
    }
  }
}
