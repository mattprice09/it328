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

import nfa.NFAGraph.Transition;

/**
 * A graph data structure containing node objects and edge objects, named
 * StateNode and Transition, respectively.
 * @author Matt Price
 *
 */
public class NFAGraph {
  protected Map<String, StateNode> states;
  protected String startState;
  protected Set<String> endStates;
  protected ArrayList<String> transitionOpts;
  
  /**
   * ~~~ BEGIN INNER CLASSES
   */
  /**
   * Transitions (edges) between two states (nodes)
   */
  protected class Transition {
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
  }
  /**
   * States (nodes) in the graph
   */
  protected class StateNode {
    private String name;
    private ArrayList<Transition> transitions;
    
    public StateNode(String name) {
      this.name = name;
      this.transitions = new ArrayList<Transition>();
    }
    
    // getter/setter for name
    public String getName() {
      return this.name;
    }
    public void setName(String n) {
      this.name = n;
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
    
    public String toString() {
      ArrayList<String> parts = new ArrayList<String>();
      parts.add("> State name: " + this.name);
      parts.add("  Transitions: ");
      for (Transition t : this.transitions) {
        parts.add("    " + t.type + " -> " + t.dest);
      }
      return String.join("\n", parts);
    }
    
  }
  /**
   * ~~~ END INNER CLASSES
   */
  
  // default constructor
  public NFAGraph() {
    this.states = new HashMap<String, StateNode>();
    this.startState = "";
    this.endStates = new HashSet<String>();
    this.transitionOpts = new ArrayList<String>();
  }
  
  /**
   * Constructor which accepts an input file of specific format
   */
  public NFAGraph(String fileName) {
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
      this.transitionOpts = new ArrayList<String>();
      for (int i = 0; i < parts.length; i++) {
        transitionTypes.put(i, parts[i]);
        this.transitionOpts.add(parts[i]);
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
        this.endStates.add(st.replaceAll(" ", ""));
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
  
  // get/set transition options (e.g. ["a", "b", ...]
  @SuppressWarnings("unchecked")
  public void setTransitionOpts(ArrayList<String> newOpts) {
    this.transitionOpts = (ArrayList<String>) newOpts.clone();
  }
  public ArrayList<String> getTransitionOpts() {
    return this.transitionOpts;
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
  
  // add a state (node) to the graph
  public void addState(String name) {
    this.states.put(name, new StateNode(name));
  }
  
  // add a transition (edge) to the graph
  public void addTransition(String source, String dest, String type) {
    this.states.get(source).addTransition(dest, type);
  }
  
  // recursively find all possible transitions from a given starting state
  @SuppressWarnings("unchecked")
  private void recursiveGetAllTransitions(Map<String, HashSet<String>> transOptions, HashSet<String> visited,
                                          String state, HashSet<String> compound, String parentType) {
    
    visited.add(state);
    ArrayList<Transition> transQueue = this.states.get(state).getTransitions();
    for (Transition trans : transQueue) {
      String transType = parentType;
      if (trans.type.equals("lambda") && parentType.equals("")) {
        // lambda at starting state, ignore
        continue;
      }
      if (transType.equals("")) {
        transType = trans.type;
      }
      if (!(transOptions.containsKey(transType) && transOptions.get(transType).contains(trans.dest))) {
        // New state function
        
        if (this.states.get(trans.dest).isForwardingState()) {
          // this transition's destination has an outgoing lambda. make recursive call
          // from destination state
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
  
  /**
   * Get all transition possibilities for a set of states
   */
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
  
  // print stringified representation of the graph
  public void printGraph() {
    System.out.println("Sigma: " + String.join(" ", this.transitionOpts));
    System.out.println("------");
    for (int i = 0; i < this.states.size(); i++) {
      System.out.printf("%2s:", i+"");
      // get mapping of transition types to list of destinations
      Map<String, ArrayList<String>> transitionTypes = new HashMap<String, ArrayList<String>>();
      for (Transition t : this.states.get(i+"").getTransitions()) {
        String type = t.getType();
        if (type.equals("lambda")) {
          type = " ";
        }
        if (!transitionTypes.containsKey(type)) {
          transitionTypes.put(type, new ArrayList<String>());
        } 
        transitionTypes.get(type).add(t.getDest());
      }
      // print values, accounting for non-existing transitions in a state
      for (int j = 0; j < this.transitionOpts.size(); j++) {
        if (transitionTypes.containsKey(this.transitionOpts.get(j))) {
          System.out.printf("%2s(%s,{%s})", "", this.transitionOpts.get(j)+"", String.join(", ", transitionTypes.get(this.transitionOpts.get(j)+"")));
        } else {
          System.out.printf("%2s(%s,{%s})", "", this.transitionOpts.get(j), "");
        }
      }
      if (transitionTypes.containsKey(" ")) {
        System.out.printf("%2s(%s,{%s})", "", " ", String.join(", ", transitionTypes.get(" ")));
      } else {
        System.out.printf("%2s(%s,{%s})", "", " ", "");
      }
      System.out.println();
    }
    System.out.println("------");
    System.out.println(this.startState + ": Initial State");
    System.out.println(String.join(", ", this.endStates) + ": Accepting State(s)");
  }
}
