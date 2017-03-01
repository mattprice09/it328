package nfa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NFA {
  
  /** 
   * Convert NFA to DFA, leaving the NFA data unmutated
   * @param nfaGraph
   *    A graph object representing an NFA
   * @return
   *    A graph object representing a DFA
   */
  public static AutomataGraph convertNFA(AutomataGraph nfaGraph) {
    AutomataGraph dfa = new AutomataGraph();
    dfa.setStartState(nfaGraph.getStartState());
    
    // initialize queue
    ArrayList<String> queue = new ArrayList<String>();
    queue.add(dfa.getStartState());
    dfa.addEndState(dfa.getStartState());
    Set<String> visited = new HashSet<String>();
    
    // iterate through queue of all possible combinations
    while (queue.size() > 0) {
      
      // current dfa state
      String currState = queue.get(0);
      queue.remove(0);
      dfa.addState(currState);
      visited.add(currState);
      
      // get all possibles transitions
      String [] nfaStates = currState.split("-");
      // all new transitions
      Map<String, String> newTransitions = nfaGraph.getAllPossibleTransitions(nfaStates);
      for (String type : newTransitions.keySet()) {
        dfa.addTransition(currState, newTransitions.get(type), type);
        if (!visited.contains(newTransitions.get(type))) {
          queue.add(newTransitions.get(type));
        }
      }
    }
    // set end states for DFA
    dfa.setEndStatesFromNFA(nfaGraph.getEndStates());
    return dfa;
  }
  
  public static void main(String[] args) {
    // Handle user input
    if (args.length < 2) {
      System.out.println("> ERROR: Invalid format. Usage: 'java NFA <nfaFile> <testStrings.txt>'");
      System.exit(0);
    }
    String nfaFile = args[0];
    String stringsFile = args[1];
    
//    String rootStr = "asg2/src/resources/nfa";
//    for (int i = 1; i < 9; i++) {
//      System.out.println("\n> NEW GRAPH\n\n  NFA:\n");
//      AutomataGraph nfaGraph = new AutomataGraph(rootStr + i);
//      System.out.println(nfaGraph.toString());
//      System.out.println("\n  DFA:\n");
//      AutomataGraph dfaGraph = convertNFA(nfaGraph);
//      System.out.println(dfaGraph.toString());
//    }
    AutomataGraph nfaGraph = new AutomataGraph(nfaFile);
    System.out.println(nfaGraph.toString());
    AutomataGraph dfaGraph = convertNFA(nfaGraph);
    System.out.println(dfaGraph.toString());
  }
}
