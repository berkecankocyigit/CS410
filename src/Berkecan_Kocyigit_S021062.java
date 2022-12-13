import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Berkecan_Kocyigit_S021062 {
    // A class that represents a rule in a context-free grammar
    static class Rule {
        String left; // left-hand side of the rule
        String right; // right-hand side of the rule

        // Constructor
        Rule(String left, String right) {
            this.left = left;
            this.right = right;
        }

        public String getLeft() {
            return left;
        }

        public String getRight() {
            return right;
        }

        public void setLeft(String left) {
            this.left = left;
        }

        public void setRight(String right) {
            this.right = right;
        }

        // Returns a string representation of the rule
        @Override
        public String toString() {
            return left + " -> " + right;
        }
    }

    // A class that represents a context-free grammar
    static class CFG {
        List<Rule> rules; // list of rules in the grammar
        List<String> nonTerminalSymbols; // list of non-terminal symbols in the grammar
        List<String> terminalSymbols; // list of terminal symbols in the grammar
        String startSymbol; // start symbol of the grammar

        // Constructor
        CFG() {
            this.rules = new ArrayList<Rule>();
            this.nonTerminalSymbols = new ArrayList<String>();
            this.terminalSymbols = new ArrayList<String>();
            this.startSymbol = null;
        }

        // Adds a non-terminal symbol to the grammar
        void addNonTerminalSymbol(String symbol) {
            nonTerminalSymbols.add(symbol);
        }

        // Adds a terminal symbol to the grammar
        void addTerminalSymbol(String symbol) {
            terminalSymbols.add(symbol);
        }

        // Sets the start symbol of the grammar
        void setStartSymbol(String symbol) {
            this.startSymbol = symbol;
        }

        // Adds a rule to the grammar
        void addRule(Rule rule) {
            rules.add(rule);
        }
        // Returns the list of rules in the grammar
        List<Rule> getRules() {
            // List of rules in the grammar
            List<Rule> rules = new ArrayList<Rule>();

            // Iterate over the list of rules in the grammar
            for (Rule rule : this.rules) {
                // Get the left-hand side and right-hand side of the rule
                String left = rule.left;
                String right = rule.right;

                // Create a new Rule object with the left-hand side and right-hand side of the rule
                Rule newRule = new Rule(left, right);

                // Add the new Rule object to the list of rules
                rules.add(newRule);
            }

            // Return the list of rules
            return rules;
        }

        void convertToCNF() {
            // Set the start symbol of the grammar to S'
            String newStartSymbol = "S'";

            // Add a new rule with the start symbol and the original start symbol
            addRule(new Rule(newStartSymbol, startSymbol));
            addNonTerminalSymbol(newStartSymbol);
            setStartSymbol(newStartSymbol);
            Set<String> nullables = new HashSet<String>();
            ArrayList<Rule> setted=new ArrayList<>();
            // Identify and remove all nullable non-terminal symbols in the grammar
            boolean changed = true;
            while (changed) {
                changed = false;
                for (Rule rule : rules) {
                    String left = rule.left;
                    String right = rule.right;
                    if (right.equals("e")) {
                        if (!nullables.contains(left)) {
                            rules.remove(rule);
                            nullables.add(left);
                            changed = true;
                            break;
                        }
                    } else {
                        boolean breaking=false;
                        for (int i = 0; i < right.length(); i++) {
                            String s=""+right.charAt(i);
                            if (nullables.contains(s)&&!setted.contains(rule)) {
                                String newRight=right.substring(0,i)+right.substring(i+1);
                                Rule newRule=new Rule(left,newRight);
                                setted.add(rule);
                                rules.add(newRule);
                                changed=true;
                                breaking=true;
                            }
                        }
                        if(breaking){
                            break;
                        }
                    }
                }
            }
            changed=true;
            while (changed){
                changed =false;
                for(Rule rule:rules){
                    String left = rule.left;
                    String right = rule.right;
                    if(right.length()==1){
                        if(right.equals(left)){
                            rules.remove(rule);
                            changed=true;
                            break;
                        }else {
                            boolean breaking=false;
                            if(!terminalSymbols.contains(right)){
                                rules.remove(rule);
                                ArrayList<Rule> will_be_added=new ArrayList<>();
                                for(Rule r:rules){
                                    if(r.getLeft().equals(right)){
                                        //rule.setRight(r.getRight());
                                        Rule newRule=new Rule(left,r.getRight());
                                        will_be_added.add(newRule);
                                        changed=true;
                                        breaking=true;

                                    }
                                }
                                for(Rule r:will_be_added){
                                    rules.add(r);
                                }
                            }
                            if(breaking){
                                break;
                            }
                        }
                    }
                }
            }

            changed=true;
            ArrayList<Rule> checking=new ArrayList<>();
            while(changed){
                changed=false;
                for(Rule rule:rules){
                    String left = rule.left;
                    String right = rule.right;
                    if(right.length()>2){
                        String firstChar=""+right.charAt(0);
                        String rest=right.substring(1);
                        String symbol="";
                        boolean exist=false;
                        for(Rule r:rules){
                            if(r.getRight().equals(rest)){
                                symbol=r.getLeft();
                                exist=true;


                            }
                        }
                        if(exist){
                            rule.setRight(firstChar+symbol);
                        }else {
                            //create new symbol
                            symbol=generateNewSymbol();
                            rule.setRight(right.charAt(0)+symbol);
                            Rule newRule=new Rule(symbol,right.substring(1));
                            this.addRule(newRule);
                        }
                        changed=true;
                        break;
                    }
                }
            }
            changed=true;
            ArrayList<Rule> added=new ArrayList<>();
            while (changed){
                changed=false;
                for(Rule rule:rules) {
                    String left = rule.left;
                    String right = rule.right;
                    boolean breaking=false;
                    for(int i= 0;i<right.length();i++){
                        String s=""+right.charAt(i);
                        if(terminalSymbols.contains(s)&&right.length()>1){
                            String symbol="";
                            boolean exists=false;
                            for(Rule r: added){
                                if(r.getRight().equals(s)){
                                    symbol=r.getLeft();
                                    exists=true;
                                }
                            }
                            for(Rule r:rules){
                                if(r.getRight().equals(s)){
                                    symbol=r.getLeft();
                                    exists=true;
                                }
                            }
                            if(exists){
                                if(i==0){
                                    right=symbol+right.charAt(1);
                                }else{
                                    right=right.charAt(0)+symbol;
                                }
                            }else{
                                symbol=generateNewSymbol();
                                Rule newRule=new Rule(symbol,s);
                                added.add(newRule);
                                if(i==0){
                                    right=symbol+right.charAt(1);
                                }else{
                                    right=right.charAt(0)+symbol;
                                }
                                this.addRule(newRule);
                            }
                            rule.setRight(right);
                            changed=true;
                            breaking=true;
                        }
                    }
                    if(breaking){
                        break;
                    }
                }
            }
        }
        static int counter=0;
        public String generateNewSymbol(){
            char c = (char) ('A'+counter);
            String symbol = ""+c+"";
            counter++;
            if(nonTerminalSymbols.contains(symbol)){
                return generateNewSymbol();
            }else{
                return symbol;
            }
        }

        public List<String> getNonTerminalSymbols() {
            return nonTerminalSymbols;
        }

        public List<String> getTerminalSymbols() {
            return terminalSymbols;
        }

        public String getStartSymbol() {
            return startSymbol;
        }

        // Returns a string representation of the grammar
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Rule rule : rules) {
                sb.append(rule.toString()).append("\n");
            }
            return sb.toString();
        }

    }
    public static void main(String[] args) throws IOException {
        // Open the input file for reading
        BufferedReader reader = new BufferedReader(new FileReader("src/G1.txt"));

        // Create a new CFG object
        CFG cfg = new CFG();

        // Read the NON-TERMINAL header
        String line = reader.readLine();
        if (line == null || !line.equals("NON-TERMINAL")) {
            throw new IOException("Invalid input file.");
        }

        // Read the non-terminal symbols in the grammar
        while ((line = reader.readLine()) != null) {
            // Check if we have reached the TERMINAL header
            if (line.equals("TERMINAL")) {
                break;
            }

            // Add the non-terminal symbol to the CFG object
            cfg.addNonTerminalSymbol(line);
        }


        // Read the terminal symbols in the grammar
        while ((line = reader.readLine()) != null) {
            // Check if we have reached the RULES header
            if (line.equals("RULES")) {
                break;
            }

            // Add the terminal symbol to the CFG object
            cfg.addTerminalSymbol(line);
        }


        // Read the rules in the grammar
        while ((line = reader.readLine()) != null) {
            // Check if we have reached the START header
            if (line.equals("START")) {
                break;
            }

            // Split the rule into its left-hand side and right-hand side
            String[] split = line.split(":");
            if (split.length != 2) {
                throw new IOException("Invalid input file.");
            }

            // Add the rule to the CFG object
            cfg.addRule(new Rule(split[0], split[1]));
        }
        line=reader.readLine();
        // Set the start symbol of the CFG object
        cfg.setStartSymbol(line);

        // Close the input file
        reader.close();
        // Convert the grammar to its equivalent Chomsky normal form
        cfg.convertToCNF();

        print(cfg);
    }
    static void print(CFG cfg){
        System.out.println("NON-TERMINAL");
        for (String s: cfg.getNonTerminalSymbols()
        ) {
            System.out.println(s);
        }
        System.out.println("TERMINAL");
        for (String s: cfg.getTerminalSymbols()
        ) {
            System.out.println(s);
        }
        System.out.println("RULES");
        // Print the rules in the grammar
        for (Rule rule : cfg.getRules()) {
            System.out.println(rule);
        }
        System.out.println("START");
        System.out.println(cfg.getStartSymbol());
    }
}
