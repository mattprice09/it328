package nfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DFAReducer {
  
  // map new state keys to new state objects
  HashMap<String, HashSet<String>> reducedStates;
  // original DFA graph
  DFAGraph original;
  
  // default constructor
  public DFAReducer() {
    this.original = new DFAGraph();
    this.reducedStates = new HashMap<String, HashSet<String>>();
  }
  
  /**
   * Create DFAReducer from given DFAGraph. Performs the initial
   * partitioning of the DFAGraph into ending and non-ending states.
   */
  public DFAReducer(DFAGraph original) {
    
    // shallow copy is preferred since not altering original
    this.original = original;
      
    // initialize partition of states into ending and non-ending states
    this.reducedStates = new HashMap<String, HashSet<String>>();
    this.reducedStates.put("0", new HashSet<String>());
    this.reducedStates.put("1", new HashSet<String>());
    for (String s : original.getStates().keySet()) {
      if (original.getEndStates().contains(s)) {
        // state 0 will always be the ending states
        this.reducedStates.get("0").add(s);
      } else {
        // state 1 starts as all other states
        this.reducedStates.get("1").add(s);
      }
    }
  }
  
  
  public String toString() {
    ArrayList<String> parts = new ArrayList<String>();
    
    for (String s : this.reducedStates.keySet()) {
      parts.add("New state: " + s);
      parts.add("  Members: " + String.join(", ", this.reducedStates.get(s).toString()));
    }
    return String.join("\n", parts);
  }
  
  
  // Gets the combination of new states that a given old state has transitions
  // to. Used by the partition() function to determine sub-partitioning.
  private String getCombo(String state) {
    ArrayList<String> newStateDests = new ArrayList<String>();
    for (int i = 0; i < this.original.transitionOpts.size(); i++) {
      newStateDests.add("");
    }
    for (NFAGraph.Transition t : this.original.getStates().get(state).getTransitions()) {
      // iterate through each original state in a given "new state"
      // e.g. S1 contains states 3, 4, 6...thus this inner loop iterates 
      // through 3, 4, and 6. Get aggregation of this state's "new states".
      int transInd = this.original.getTransitionOpts().indexOf(t.getType());
      String newState = this.newStateFromOldState(t.getDest());
      if (newState.equals("")) {
        System.out.println("ERROR!!");
      }
      newStateDests.set(transInd, newState);
    }
    
    // get hashable set of new states to find if it is new or not
    return String.join("-", newStateDests);
  }
  
  
  // Helper that gets the current new state ID from given old state
  // (e.g. "5" returns "S2")
  private String newStateFromOldState(String oldState) {
    for (String s : this.reducedStates.keySet()) {
      if (this.reducedStates.get(s).contains(oldState)) {
        return s;
      }
    }
    return "";
  }
  
  
  /**
   * Perform single partition of current states as done in the Equivalence Theorem
   * @return
   *    Returns true if there was a successful partition.
   *    Returns false if it could not be partitioned any further.
   */
  private boolean partition() {
    
    boolean flag = false;
    
    Map<String, HashSet<String>> newStates = new HashMap<String, HashSet<String>>();
    int currKey = 0;
    
    for (String ns : this.reducedStates.keySet()) {
      Map<String, HashSet<String>> tempNewStates = new HashMap<String, HashSet<String>>();
      
      // sub-partition each partition
      for (String state : this.reducedStates.get(ns)) {
        // sub-partition a partition
        String stateCombo = this.getCombo(state);
        if (tempNewStates.containsKey(stateCombo)) {
          tempNewStates.get(stateCombo).add(state);
        } else {
          // new partition
          HashSet<String> newState = new HashSet<String>();
          newState.add(state);
          tempNewStates.put(stateCombo, newState);
        }
      }
      // copy the sub-partition into the new partition set
      for (String s : tempNewStates.keySet()) {
        newStates.put(currKey+"", tempNewStates.get(s));
        currKey++;
      }
      // keep track of if there was a new partition
      if (tempNewStates.size() > 1) {
        flag = true;
      }
    }
    if (flag) {
      this.reducedStates = new HashMap<String, HashSet<String>>(newStates);
    }
    return flag;
  }
  
  
  /**
   * Create a new DFAGraph object from the current reduced configuration.
   */
  private DFAGraph createNewDFA() {
    DFAGraph minimized = new DFAGraph();
    // create states
    for (String ns : this.reducedStates.keySet()) {
      minimized.addState(ns);
    }
    // copy transitions
    for (String ns : this.reducedStates.keySet()) {
      for (String s : this.reducedStates.get(ns)) {
        String [] transitions = this.getCombo(s).split("-");
        for (int i = 0; i < transitions.length; i++) {
          if (!transitions[i].equals("")) {
            minimized.addTransition(ns, transitions[i], this.original.getTransitionOpts().get(i));
          }
        }
        break;
      }
    }
    // copy transition options
    minimized.setTransitionOpts(this.original.getTransitionOpts());
    // set end states
    for (String s : this.original.getEndStates()) {
      String eSt = this.newStateFromOldState(s);
      if (!minimized.getEndStates().contains(eSt)) {
        minimized.addEndState(eSt);
      }
    }
    // set start state
    String startState = newStateFromOldState(this.original.getStartState());
    minimized.setStartState(startState);
    return minimized;
  }
  
  
  /**
   * Reduce DFAGraph
   */
  public DFAGraph reduce() {
    
    while (this.partition()) {
      // partition until there is no more partitioning to be done
    }
    
    return this.createNewDFA();
  }

}
