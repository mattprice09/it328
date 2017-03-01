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

public class AutomataGraph {
  private Map<String, StateNode> states;
  private String startState;
  private Set<String> endStates;
  
  /**
   * Transitions (edges) between two states (nodes)
   */
  private class Transition {
    private String dest;
    private String type;
    
    public Transition(String d, String t) {
      this.dest = d;
      this.type = t;
    }
    
    public String getDest() {
      return this.dest;
    }
    public String getType() {
      return this.type;
    }
    
    public String toString() {
      return "next: " + this.dest + ", transition: " + this.type;
    }
  }
  /**
   * States (nodes) in the graph
   */
  private class StateNode {
    private String name;
    private ArrayList<Transition> transitions;
    
    public StateNode(String name) {
      this.name = name;
      this.transitions = new ArrayList<Transition>();
    }
    
    // add a transition (edge) to the state
    public void addTransition(String dest, String type) {
      // first, make sure this transition doesn't already exist from this state
      for (Transition t : this.transitions) {
        if (t.dest.equals(dest) && t.type.equals(type)) {
          return;
        }
      }
      Transition trans = new Transition(dest, type);
      this.transitions.add(trans);
    }
    
    // get list of transitions (edges) for this state
    public ArrayList<Transition> getTransitions() {
      return this.transitions;
    }
    
    // returns true if this state has an outgoing lambda transition
    public boolean isForwardingState() {
      for (Transition trans : this.transitions) {
        if (trans.type.equals("lambda")) {
          return true;
        }
      }
      return false;
    }
    
    // stringified representation of the state
    public String toString() {
      ArrayList<String> strBuilder = new ArrayList<String>();
      strBuilder.add("State: " + this.name);
      strBuilder.add("    Transitions: ");
      for (Transition trans : this.transitions) {
        strBuilder.add("    " + trans.toString());
      }
      return String.join("\n", strBuilder);
    }
  }
  
  // default constructor
  public AutomataGraph() {
    this.states = new HashMap<String, StateNode>();
    this.startState = "";
    this.endStates = new HashSet<String>();
  }
  
  /**
   * Constructor which accepts an input file of specific format
   */
  public AutomataGraph(String fileName) {
    this.states = new HashMap<String, StateNode>();
    File infile = new File(fileName);
    try {
      Scanner reader = new Scanner(infile);
      // create state nodes
      String line = reader.nextLine();
      int numStates = Integer.parseInt(line);
      for (int i = 0; i < numStates; i++) {
        this.addState(i + "");
      }
      // map transition matrix indeces to their keys
      line = reader.nextLine();
      String [] parts = line.replaceAll("\t", " ").trim().replaceAll(" +", " ").split(" ");
      Map<Integer, String> transitionTypes = new HashMap<Integer, String>();
      for (int i = 0; i < parts.length; i++) {
        transitionTypes.put(i, parts[i]);
      }
      
      // Read info about all states (essentially a matrix)
      for (int i = 0; i < numStates; i++) {
        line = reader.nextLine();
        parts = line.replaceAll("\t", " ").trim().replaceAll(" +", " ").split(" ");
        String currState = parts[0].replace(":", "");
        for (int j = 1; j < parts.length; j++) {
          // array of neighbors
          String [] neighbors = parts[j].replace("{", "").replace("}", "").trim().replaceAll(" +", " ").split(",");
          for (String neighbor : neighbors) {
            if (neighbor.equals("")) {
              continue;
            }
            if (transitionTypes.containsKey(j-1)) {
              this.addTransition(currState, neighbor, transitionTypes.get(j-1));
            } else {
              this.addTransition(currState, neighbor, "lambda");
            }
          }
        }
      }
      
      // start state
      this.startState = reader.nextLine();
      // end states
      this.endStates = new HashSet<String>();
      parts = reader.nextLine().replace("{", "").replace("}", "").split(",");
      for (String st : parts) {
        this.endStates.add(st);
      }
      
      reader.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  // get map of states
  public Map<String, StateNode> getStates() {
    return this.states;
  }
  
  // get/set the start state
  public String getStartState() {
    return this.startState;
  }
  public void setStartState(String s) {
    this.startState = s;
  }
  
  // get/append the end states
  public Set<String> getEndStates() {
    return this.endStates;
  }
  public void addEndState(String s) {
    this.endStates.add(s);
  }
  
  // sets end states based on a set of states from an NFA graph
  // implies that this instance is a DFA graph
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
  
  // add a state (node) to the graph
  public void addState(String name) {
    this.states.put(name, new StateNode(name));
  }
  
  // add a transition (edge) to the graph
  public void addTransition(String source, String dest, String type) {
    this.states.get(source).addTransition(dest, type);
  }
  
  // stringified representation of the graph
  public String toString() {
    ArrayList<String> strBuilder = new ArrayList<String>();
    strBuilder.add("Start state: " + this.startState);
    strBuilder.add("End states: " + String.join(",", this.endStates));
    for (StateNode state : this.states.values()) {
      strBuilder.add(state.toString());
    }
    return String.join("\n", strBuilder);
  }
  
  @SuppressWarnings("unchecked")
  private void recursiveGetAllTransitions(Map<String, HashSet<String>> transOptions, HashSet<String> visited,
                                          String state, HashSet<String> compound, String parentType) {
//    if (visited.contains(state)) {
//      return;
//    }
    visited.add(state);
    ArrayList<Transition> transQueue = this.states.get(state).getTransitions();
    for (Transition trans : transQueue) {
      String transType = parentType;
      if (trans.type.equals("lambda") && parentType.equals("")) {
        continue;
      }
      if (transType.equals("")) {
        transType = trans.type;
      }
      if (!(transOptions.containsKey(transType) && transOptions.get(transType).contains(trans.dest))) {
        // New state function
        
        if (this.states.get(trans.dest).isForwardingState()) {
          // recursion for lambdas
          HashSet<String> comp = (HashSet<String>)compound.clone();
          if (!comp.contains(trans.dest)) {
            comp.add(trans.dest);
            this.recursiveGetAllTransitions(transOptions, visited, trans.dest, comp, transType);
          }
        } else if (!transOptions.containsKey(transType)) {
          // new transition type
          HashSet<String> newSet = (HashSet<String>)compound.clone();
          newSet.add(trans.dest);
          transOptions.put(transType, newSet);
        } else {
          // another option for the transition
          if (compound.size() > 0) {
            for (String s : compound) {
              transOptions.get(transType).add(s);
            }
          }
          transOptions.get(transType).add(trans.dest);
        }
      }
    }
  }
  
  public Map<String, String> getAllPossibleTransitions(String [] states) {
    Map<String, HashSet<String>> transOptions = new HashMap<String, HashSet<String>>();
    HashSet<String> visited = new HashSet<String>();
    for (String state : states) {
      this.recursiveGetAllTransitions(transOptions, visited, state, new HashSet<String>(), "");
    }
    
    // convert list of states to string representation
    // ex: ["a", "c", "d"] -> "a-c-d"
    Map<String, String> transOptionStrings = new HashMap<String, String>();
    for (String type : transOptions.keySet()) {
      String [] transStates = new String[transOptions.get(type).size()];
      transStates = (String[])transOptions.get(type).toArray(transStates);
      Arrays.sort(transStates);
      transOptionStrings.put(type, String.join("-", transStates));
    }
    return transOptionStrings;
  }
}
