package nfa;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import nfa.NFAGraph.StateNode;
import nfa.NFAGraph.Transition;

/**
 * Extends NFAGraph so that we don't have function(s) in NFAGraph that
 * are DFA-specific.
 * @author Matt Price
 *
 */
public class DFAGraph extends NFAGraph {
  
  private ArrayList<String> acceptedStrings;
  
  public DFAGraph() {
    super();
    this.acceptedStrings = new ArrayList<String>();
  }
  
  /**
   * Refactor the DFA's naming schema by replacing names such as "1-4-2-3" with
   * shorter names such as "1". Performs a deepcopy of the states map.
   */
  public void refactorNames(Map<String, String> nameMapping) {
    // refactor references within states/transitions
    Map<String, StateNode> temp = this.states;
    this.states = new HashMap<String, StateNode>();
    for (String oldKey : nameMapping.keySet()) {
      String newKey = nameMapping.get(oldKey);
      this.addState(newKey);
      for (Transition t : temp.get(oldKey).getTransitions()) {
        this.states.get(newKey).addTransition(nameMapping.get(t.getDest()), t.getType());
      }
    }
    // refactor start state
    this.startState = nameMapping.get(this.startState);
    // refactor end states
    Set<String> newEnds = new HashSet<String>();
    for (String s : this.endStates) {
      newEnds.add(nameMapping.get(s));
    }
    this.endStates = newEnds;
  }
  
  // sets end states based on a set of states from an NFA graph
  public void setEndStatesFromNFA(Set<String> endStates) {
    for (String nfaSt : endStates) {
      for (String dfaSt : this.states.keySet()) {
        Set<String> stParts = new HashSet<String>(Arrays.asList(dfaSt.split("-")));
        if (stParts.contains(nfaSt)) {
          this.addEndState(dfaSt);
          break;
        }
      }
    }
  }
  
  private boolean stringAccepted(String s) {
    if (s.equals("")) {
      return false;
    }
    StateNode curr = this.states.get(this.startState);
    ArrayList<String> remaining = new ArrayList<String>();
    for (String c : s.split("")) {
      remaining.add(c);
    }
    while (remaining.size() > 0) {
      // find next destination
      boolean transitioned = false;
      for (Transition t : curr.getTransitions()) {
        if (t.getType().equals(remaining.get(0))) {
          // move to next state
          remaining.remove(0);
          curr = this.states.get(t.getDest());
          transitioned = true;
          break;
        }
      }
      // stuck at state - failed
      if (!transitioned) {
        return false;
      }
    }
    // ended on a non-accepting state
    if (!this.endStates.contains(curr.getName())) {
      return false;
    }
    return true;
  }
  
  /**
   * Parse strings file
   */
  public void parseStringsFile(String filename) {
    File infile = new File(filename);
    this.acceptedStrings = new ArrayList<String>();
    try {
      Scanner in = new Scanner(infile);
      
      while (in.hasNextLine()) {
        String s = in.nextLine();
        if (this.stringAccepted(s)) {
          this.acceptedStrings.add(s);
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  public void printGraph() {
    // column titles
    System.out.printf("%5s", "Sigma");
    for (int i = 0; i < this.transitionOpts.size(); i++) {
      System.out.printf("%5s", this.transitionOpts.get(i));
    }
    System.out.println();
    System.out.println("-------------------------");
    
    // print states as rows
    for (int i = 0; i < this.states.size(); i++) {
      System.out.printf("%5s:", i+"");
      
      // get mapping of transition types to list of destinations
      Map<String, String> transitionTypes = new HashMap<String, String>();
      for (Transition t : this.states.get(i+"").getTransitions()) {
        transitionTypes.put(t.getType(), t.getDest());
      }
      // print values, accounting for non-existing transitions in a state
      for (int j = 0; j < this.transitionOpts.size(); j++) {
        if (transitionTypes.containsKey(this.transitionOpts.get(j))) {
          System.out.printf("%5s", transitionTypes.get(this.transitionOpts.get(j)+""));
        } else {
          System.out.printf("%5s", "");
        }
      }
      System.out.println();
    }
    System.out.println("-------------------------");
    System.out.println(this.startState + ": Initial State");
    System.out.println(String.join(", ", this.endStates) + ": Accepting State(s)");
    System.out.println("The following strings are accepted:");
    for (String s : this.acceptedStrings) {
      System.out.println(s);
    }
  }
}
