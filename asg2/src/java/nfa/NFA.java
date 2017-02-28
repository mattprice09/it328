package nfa;

public class NFA {
  
  public static void main(String[] args) {
    // Handle user input
    if (args.length < 2) {
      System.out.println("> ERROR: Invalid format. Usage: 'java NFA <nfaFile> <testStrings.txt>'");
      System.exit(0);
    }
    String nfaFile = args[0];
    String stringsFile = args[1];
    
    AutomataGraph graph = new AutomataGraph(nfaFile);
    System.out.println(graph.toString());
  }
}
