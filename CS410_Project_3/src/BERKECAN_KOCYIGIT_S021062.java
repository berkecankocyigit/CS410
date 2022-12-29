import java.io.File;
import java.util.*;

public class BERKECAN_KOCYIGIT_S021062 {
    public static void main(String[] args) throws Exception {
        Scanner sc=new Scanner(new File("src\\input.txt"));
        int numberOfAlphabet=0;
        char[] alphabetSymbols;
        int numberOfTape=0;
        char[] tapeSymbols;
        char blank;
        int numberOfStates=0;
        List<String> states=new ArrayList<>();
        String startState;
        String acceptState;
        String rejectState;
        List<Transition> transitions=new ArrayList<>();
        String input="";
        numberOfAlphabet=sc.nextInt();
        alphabetSymbols=new char[numberOfAlphabet];
        for(int i=0;i<numberOfAlphabet;i++){
            alphabetSymbols[i]= sc.next().charAt(0);
        }

        numberOfTape=sc.nextInt();
        tapeSymbols=new char[numberOfTape];
        for(int i=0;i<numberOfTape;i++){
            tapeSymbols[i]= sc.next().charAt(0);
        }
        blank=sc.next().charAt(0);

        numberOfStates=sc.nextInt();
        for(int i=0;i<numberOfStates;i++){
            states.add(sc.next());
        }
        sc.nextLine();
        startState=sc.nextLine();
        acceptState=sc.nextLine();
        rejectState=sc.nextLine();
        while (true){
            String[] line=sc.nextLine().split(" ");
            if(!states.contains(line[0])){
                input=line[0];
                break;
            }else{
                transitions.add(new Transition(line[0],line[1].charAt(0),line[2].charAt(0),line[3].charAt(0),line[4]));
            }
        }
        TuringMachine tm =new TuringMachine();
        tm.blankSymbol=blank;
        tm.alphabet=alphabetSymbols;
        tm.states=states;
        tm.acceptState=acceptState;
        tm.tapeAlphabet=tapeSymbols;
        tm.startState=startState;
        tm.rejectState=rejectState;
        tm.transitions=transitions;

        int accepted=tm.run(input,0,tm);
        try{System.out.print("ROUT: "+startState+" ");

            for(String s:tm.path){
                System.out.print(s);
                if(!s.equals(acceptState)&&!s.equals(rejectState)){
                    System.out.print(" ");
                }else {
                    System.out.println();
                }
            }}catch(Exception ignored){

        }
        System.out.print("RESULT: ");
        if(accepted==1){
            System.out.print("accepted");
        }else if(accepted==0){
            System.out.print("rejected");
        }else {
            System.out.print("halted\n");
            throw new Exception("Halted!");
        }

    }
    public static class TuringMachine{
        //List<State> states=new ArrayList<>();
        char[] alphabet;
        char[] tapeAlphabet;
        List<String> states;
        char blankSymbol;
        List<Transition> transitions=new ArrayList<>();
        String startState;
        String acceptState;
        String rejectState;
        String currentState;
        char[] tape=new char[Integer.MAX_VALUE/2];
        int head=Integer.MAX_VALUE/4;
        List<String> path=new ArrayList<>();
        public TuringMachine() {
        }

        public TuringMachine(char[] alphabet, char[] tapeAlphabet, List<String> states, char blankSymbol, String startState, String acceptState, String rejectState) {
            this.alphabet = alphabet;
            this.tapeAlphabet = tapeAlphabet;
            this.states = states;
            this.blankSymbol = blankSymbol;
            this.startState = startState;
            this.acceptState = acceptState;
            this.rejectState = rejectState;
        }
        public int run(String input,int HALT, TuringMachine reference){
            path.clear();
            currentState=startState;
            Arrays.fill(tape,blankSymbol);
            for(int i=0;i<input.length();i++){
                tape[i+head]=input.charAt(i);
            }
            Collections.shuffle(transitions);
            if(HALT<20){
                while(!currentState.equals(acceptState)&&!currentState.equals(rejectState)){
                    boolean exit=false;
                    for (Transition transition : transitions) {

                        if (transition.state.equals(currentState) && transition.read == tape[head]) {
                            tape[head] = transition.write;
                            currentState=transition.to;
                            path.add(transition.to);
                            if (transition.move == 'R') {

                                if (head >= ((Integer.MAX_VALUE/2) - 1000)) {
                                    exit=true;
                                    break;
                                }
                                head++;
                            } else {
                                if (head <= 1000) {
                                    exit=true;
                                    break;
                                }
                                head--;
                            }
                        }
                    }
                    if(exit){
                        break;
                    }
                }
                if(currentState.equals(acceptState)){
                    return 1;
                }else if(currentState.equals(rejectState)){
                    return 0;
                }else {
                    HALT++;
                    return reference.run(input,HALT,reference);
                }
            }else{
                return -1;
            }



        }
    }
    public static class Transition {
        String state;
        char read;
        char write;
        char move;
        String to;

        public Transition(String state, char read, char write, char move, String to) {
            this.state = state;
            this.read = read;
            this.write = write;
            this.move = move;
            this.to = to;
        }

        public Transition(){}

        @Override
        public String toString() {
            return "Transition{" +
                    "state='" + state + '\'' +
                    ", read=" + read +
                    ", write=" + write +
                    ", move=" + move +
                    ", to='" + to + '\'' +
                    '}';
        }
    }
}