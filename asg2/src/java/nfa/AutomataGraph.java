package nfa;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
      Transition trans = new Transition(dest, type);
      this.transitions.add(trans);
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
      String [] parts = line.split(" ");
      Map<Integer, String> transitionTypes = new HashMap<Integer, String>();
      for (int i = 0; i < parts.length; i++) {
        transitionTypes.put(i, parts[i]);
      }
      
      // Read info about all states (essentially a matrix)
      for (int i = 0; i < numStates; i++) {
        line = reader.nextLine();
        parts = line.split(" ");
        String currState = parts[0].replace(":", "");
        for (int j = 1; j < parts.length; j++) {
          // array of neighbors
          String [] neighbors = parts[j].replace("{", "").replace("}", "").split(",");
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
}
