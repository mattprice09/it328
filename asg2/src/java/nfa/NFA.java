package nfa;

import java.util.ArrayList;
import java.util.HashMap;
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
  public static DFAGraph convertNFA(NFAGraph nfaGraph) {
    DFAGraph dfa = new DFAGraph();
    dfa.setStartState(nfaGraph.getStartState());
    dfa.setTransitionOpts(nfaGraph.getTransitionOpts());
    
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
    
    // get name mapping to use for refactoring names
    Map<String, String> nameMapping = new HashMap<String, String>();
    int i = 0;
    for (String name : dfa.states.keySet()) {
      nameMapping.put(name, i+"");
      i++;
    }
    // refactor names in DFA
    dfa.refactorNames(nameMapping);
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
    
    String rootStr = "asg2/src/resources/nfa";
    for (int i = 1; i < 9; i++) {
      NFAGraph nfaGraph = new NFAGraph(rootStr+i);
      nfaGraph.printGraph();
      System.out.println();
      System.out.println("To DFA:");
      DFAGraph dfaGraph = convertNFA(nfaGraph);
      dfaGraph.parseStringsFile(stringsFile);
      dfaGraph.printGraph();
    }
//    NFAGraph nfaGraph = new NFAGraph(nfaFile);
//    nfaGraph.printGraph();
//    System.out.println();
//    System.out.println("To DFA:");
//    DFAGraph dfaGraph = convertNFA(nfaGraph);
//    dfaGraph.parseStringsFile(stringsFile);
//    dfaGraph.printGraph();
    
  }
}
