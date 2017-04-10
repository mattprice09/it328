package nfa;

/**
 * Minimizes a DFA and parses a strings file to test input strings.
 * In all cases, the list of strings was the same as pre-minimization,
 * and the size of the minimized DFA was always less than the size of 
 * the original DFA.
 * @author Matt Price
 *
 */
public class parse {
  
  /**
   * Runs the main DFA minimization functions.
   * Input:
   *    Original DFA
   * Output: 
   *    Minimized DFA
   */
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
    
    /**
     * NOTE: I didn't remove the below commented code because it
     * can be used for testing/debugging purposes. If uncommented,
     * it will print the output from Asg2 (NFA to DFA). This allows
     * the programmer to analyze the DFA both before and after minimization.
     * In all cases currently, the DFA's size is reduced (as expected).
     */
    
    String rootStr = "asg2/src/resources/";
    if (!nfaFile.contains("/")) {
      // runs the algorithm on one single nfa file
      
//      System.out.println("\n#################################\n");
      NFAGraph nfaGraph = new NFAGraph(rootStr+nfaFile);
//      nfaGraph.printGraph();
//      
//      System.out.println("\n\nTo DFA:");
      DFAGraph dfaGraph = NFA.convertNFA(nfaGraph);
//      dfaGraph.parseStringsFile(stringsFile);
//      dfaGraph.printGraph();
      
      System.out.println("\n\nMinimized DFA from " + nfaFile + ":");
      DFAGraph minimized = minimize(dfaGraph);
      minimized.parseStringsFile(stringsFile);
      minimized.printGraph();
    } else {
      // this is used for testing purposes...runs the algorithm
      // on all available input nfa files
      
        rootStr = rootStr + "nfa";
        for (int i = 1; i < 10; i++) {
//          System.out.println("\n#################################\n");
          NFAGraph nfaGraph = new NFAGraph(rootStr+i);
//          nfaGraph.printGraph();
//          
//          System.out.println("\n\nTo DFA:");
          DFAGraph dfaGraph = NFA.convertNFA(nfaGraph);
//          dfaGraph.parseStringsFile(stringsFile);
//          dfaGraph.printGraph();
//          
          System.out.println("\n\nMinimized DFA from " + nfaFile + ":");
          DFAGraph minimized = minimize(dfaGraph);
          minimized.parseStringsFile(stringsFile);
          minimized.printGraph();
        }
    }
    
  }
}
