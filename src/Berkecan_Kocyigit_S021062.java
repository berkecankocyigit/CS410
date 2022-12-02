import java.util.*;
import java.io.*;

public class Berkecan_Kocyigit_S021062 {

    public static void main(String[] args) throws Exception {
        NFA NFA=new NFA();
        File file = new File("src/NFA1.txt");
        if(!file.exists()){System.out.println("File not exists!");}
        Scanner scanner=new Scanner(file);
        fileParser(scanner,NFA);
        DFA dfa=nfaToDfaConverter(NFA);
        //print(NFA);
        print(dfa);

    }
    public static void fileParser(Scanner scanner,NFA NFA)throws Exception{
        ArrayList<String> alphabet=new ArrayList<>();
        ArrayList<State> states=new ArrayList<>();
        ArrayList<Transition> transitions=new ArrayList<>();


        if(scanner.nextLine().equals("ALPHABET")){
            String letter = scanner.nextLine();
            while(!letter.equals("STATES")){
                alphabet.add(letter);
                letter=scanner.nextLine();
            }
            NFA.setAlphabet(alphabet);


            String stateLine = scanner.nextLine();
            State state;
            //states.add(state);
            while(!stateLine.equals("START")){
                state = new State(stateLine);
                states.add(state);
                stateLine=scanner.nextLine();
            }
            NFA.setStates(states);

            String startState=scanner.nextLine();
            for (State value : states) {
                if (value.getName().equals(startState)) {
                    NFA.setStartState(value);
                }
            }
            scanner.nextLine();
            String endState=scanner.nextLine();
            for (State value : states) {
                if (value.getName().equals(endState)) {
                    ArrayList<State> temp=new ArrayList<>();
                    temp.add(value);
                    NFA.setEndState(temp);
                }
            }


            scanner.nextLine();
            String transitionLine=scanner.nextLine();
            Transition transition;
            while(!transitionLine.equals("END")){
                String from=transitionLine.substring(0,1);
                String with=transitionLine.substring(2,3);
                String to=transitionLine.substring(4,5);
                //System.out.println("from: "+from+" to: "+to);
                State home=null;
                State destination=null;

                for(State value:states){
                    //System.out.println("value name: "+value.getName());
                    if(value.getName().equals(from)){
                        //System.out.println("Froma eşit value name: "+value.getName());
                        home=value;
                    }
                    if(value.getName().equals(to)){
                        //System.out.println("To ya eşit value name: "+value.getName());
                        destination=value;
                    }
                }
                if(home == null||destination==null){
                    throw new Exception("null çıktı");
                }
                transition=new Transition(home,with,destination);
                transitions.add(transition);
                transitionLine= scanner.nextLine();
            }
            NFA.setTransitions(transitions);

        }

    }
    public static DFA nfaToDfaConverter(NFA NFA) throws Exception {
        DFA dfa = new DFA();
        ArrayList<State> states = NFA.getStates();
        ArrayList<Transition> transitions = NFA.getTransitions();
        ArrayList<String> alphabet = NFA.getAlphabet();


        ArrayList<Transition> DFATransitions=new ArrayList<>();
        ArrayList<ArrayList<State>> DFAStates=new ArrayList<>();
        ArrayList<State> startStateList= new ArrayList<>();
        startStateList.add(NFA.getStartState());
        ArrayList<State> deadstateList=new ArrayList<>();
        DFAStates.add(startStateList);
        State deadState=new State("deadState");
        deadstateList.add(deadState);
        for(String letter:alphabet){
            Transition emptyState0=new Transition(deadstateList,letter,deadstateList);
            DFATransitions.add(emptyState0);
        }

        int sizeOfTransition= 0;
        int sizeOfStates=DFAStates.size();
        int oldTsize=0;
        int oldSsize=0;

        while(true){
            ArrayList<ArrayList<State>> tempDFAStates= (ArrayList<ArrayList<State>>) DFAStates.clone();
            for(ArrayList<State> stateSet:DFAStates){
                for(String letter:alphabet){
                    ArrayList<State> tempState=findWhere(transitions,stateSet,letter);
                    if(!DFAStates.contains(tempState)&&tempState.size()>0){
                        tempDFAStates.add(tempState);
                    }
                    boolean doesContainsTransition=false;

                    boolean doesHaveTheDeadSpace=false;
                    for(Transition t:DFATransitions){
                        State firstOfT=t.mTo().get(0);
                        if(t.mFrom().equals(stateSet)&&firstOfT.equals(deadState)){
                            doesHaveTheDeadSpace=true;
                        }
                        if(t.mFrom().equals(stateSet)&&t.mTo().equals(tempState)&&t.with().equals(letter)){
                            doesContainsTransition=true;
                        }
                    }
                    if(!doesContainsTransition&&tempState.size()>0){
                        DFATransitions.add(new Transition(stateSet,letter,tempState));
                    }
                    if(!(tempState.size()>0)&&!doesHaveTheDeadSpace){
                        ArrayList<State> tempDead=new ArrayList<>();
                        tempDead.add(deadState);
                        DFATransitions.add(new Transition(stateSet,letter,tempDead));
                        if(!DFAStates.contains(deadState)){
                          tempDFAStates.add(tempDead);
                        }
                    }
                }
            }
            for(ArrayList<State> s:tempDFAStates){
                if(!DFAStates.contains(s)){
                    DFAStates.add(s);
                }
            }
            oldTsize=sizeOfTransition;
            oldSsize=sizeOfStates;
            sizeOfStates=DFAStates.size();
            sizeOfTransition=DFATransitions.size();


            if(oldSsize==sizeOfStates&&oldTsize==sizeOfTransition){
                break;
            }
        }
        int count=0;
        for(Transition t:DFATransitions){
            if(t.mFrom().equals(deadstateList)){
                count++;
            }
            if(t.mTo().equals(deadstateList)){
                count++;
            }
        }
        if(count==4){
            DFATransitions.removeIf(transition -> {
                try {
                    return transition.mFrom().equals(deadstateList);
                } catch (Exception ignored) {

                }
                return false;
            });
        }
        /*for(ArrayList<State> eS:DFAStates){ //burası hatalı
            for(State s:eS){
                if(s.equals(NFA.getEndState())){
                    dfa.getEndState().add(eS);
                }
            }
        }*/
        for(ArrayList<State> es: DFAStates){
            for(State s:es){
                for (State s_prime: NFA.getEndState()){
                    if(s_prime.equals(s)&&!dfa.getEndState().contains(es)){
                        dfa.getEndState().add(es);
                    }
                }
            }
        }

        dfa.setAlphabet(alphabet);
        dfa.setStates(DFAStates);
        dfa.setTransitions(DFATransitions);

        dfa.setStartState(startStateList);
        return dfa;
    }


    public static ArrayList<State> findWhere(ArrayList<Transition> transitions,ArrayList<State> sources,String letter) throws Exception {
        //print("Find called with parameters: transitions: "+transitions+" sources: "+sources+" letter: "+ letter);
        ArrayList<State> v=new ArrayList<>();
        /*for(State source:sources){
            for (Transition t:
                    SetTransitions) {
                try{
                    t.from(); // checks for multiple or not
                    if(t.from().equals(source)&& t.with().equals(letter)){
                        v.add(t.to());
                    }
                }catch (Exception e){
                    //multiple part
                    try{
                        for(State subSource: t.mFrom()){
                            for(Transition iT:initalTransitions){
                                if(iT.from().equals(subSource)&&iT.with().equals(letter)){
                                    v.add(iT.to());
                                }
                            }
                        }
                    }catch (Exception ignored){}
                }
            }
        }
        */
        for(State source:sources){
            //print("inside for loop source: "+source);
            for(Transition t:transitions){
                //print("--inside second for loop transition: "+t);
                if(t.from().equals(source)&&t.with().equals(letter)&&!v.contains(t.to())){
                    v.add(t.to());
                }
            }
        }
        //print("Returning value: "+v);
        return v;
    }
    public static void print(DFA dfa){
        System.out.println("ALPHABET");
        for(String s: dfa.getAlphabet()){
            System.out.println(s);
        }
        System.out.println("STATES");
        for(ArrayList<State> s: dfa.getStates()){
            System.out.println(s);
        }
        System.out.println("START");

        System.out.println(dfa.getStartState());

        System.out.println("FINAL");
        for(ArrayList<State> s: dfa.getEndState()){
            System.out.println(s);
        }
        System.out.println("TRANSITIONS");
        for(Transition s: dfa.getTransitions()){
            System.out.println(s);
        }
        System.out.print("END");
    }
}
class Transition {
    State singleHome =null;
    State singleDestination =null;
    ArrayList<State> multipleHome=null;
    ArrayList<State> multipleDestination=null;
    String letter=null;

    public Transition(ArrayList<State> multipleHome,String letter,ArrayList<State> multipleDestination){
        //System.out.println("Created Transiton with multiples from:"+multipleHome+" with: "+letter+" to: "+multipleDestination);
        this.multipleDestination=multipleDestination;
        this.multipleHome=multipleHome;
        this.letter=letter;
    }
    public Transition(State home,String letter, State destination) {
        //System.out.println("Created Transiton with single from:"+home+" with: "+letter+" to: "+destination);
        this.singleHome = home;
        this.singleDestination = destination;
        this.letter=letter;
    }
    public ArrayList<State> mFrom() throws Exception {
        if(singleHome == null){return multipleHome;}else{throw new Exception("singularda multi from");}
    }
    public ArrayList<State> mTo() throws Exception {
        if(singleDestination == null){return multipleDestination;}else{throw new Exception("singularda multi to");}
    }
    public State from() throws Exception {
        if(multipleHome==null){return singleHome;}else{throw new Exception("multide singular from");}
    }
    public State to() throws Exception {
        if(multipleDestination==null){return singleDestination;}else{throw new Exception("multide singular to");}
    }
    public String with(){
        return letter;
    }

    @Override
    public String toString() {
        if(singleHome==null){
            return
                    multipleHome + " " + letter + " " + multipleDestination;
        }else {
            return singleHome + " " + letter+ " "+singleDestination ;
        }
    }
}
class State {
    String name=null;

    public State(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}
class NFA{
     ArrayList<String> alphabet=new ArrayList<>();
     ArrayList<State> states=new ArrayList<>();
     ArrayList<Transition> transitions=new ArrayList<>();
    ArrayList<State> endState=null;
    State startState=null;
    public NFA(){}
    public NFA(ArrayList<String> alphabet,ArrayList<State>states,ArrayList<Transition>transitions) {
    this.alphabet =alphabet;
    this.states=states;
    this.transitions=transitions;
    }

    public  ArrayList<String> getAlphabet() {
        return alphabet;
    }

    @Override
    public String toString() {
        return "NFA{" +
                "alphabet=" + alphabet +
                ", states=" + states +
                ", transitions=" + transitions +
                ", endState=" + endState +
                ", startState=" + startState +
                '}';
    }

    public void setAlphabet(ArrayList<String> alphabet) {
        this.alphabet = alphabet;
    }

    public ArrayList<State> getEndState() {
        return endState;
    }

    public void setEndState(ArrayList<State> endState) {
        this.endState = endState;
    }

    public State getStartState() {
        return startState;
    }

    public void setStartState(State startState) {
        this.startState = startState;
    }

    public  ArrayList<State> getStates() {
        return states;
    }

    public void setStates(ArrayList<State> states) {
        this.states = states;
    }

    public  ArrayList<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(ArrayList<Transition> transitions) {
        this.transitions = transitions;
    }

}
class DFA{
    ArrayList<String> alphabet=new ArrayList<>();
    ArrayList<ArrayList<State>> states=new ArrayList<>();
    ArrayList<Transition> transitions=new ArrayList<>();
    ArrayList<ArrayList<State>> endStates=new ArrayList<>();
    ArrayList<State> startState=null;
    public DFA(){}
    public DFA(ArrayList<String> alphabet,ArrayList<ArrayList<State>>states,ArrayList<Transition>transitions) {
        this.alphabet =alphabet;
        this.states=states;
        this.transitions=transitions;
    }

    public  ArrayList<String> getAlphabet() {
        return alphabet;
    }

    @Override
    public String toString() {
        return "DFA{" +
                "alphabet=" + alphabet +
                ", states=" + states +
                ", transitions=" + transitions +
                ", endState=" + endStates +
                ", startState=" + startState +
                '}';
    }

    public void setAlphabet(ArrayList<String> alphabet) {
        this.alphabet = alphabet;
    }

    public ArrayList<ArrayList<State>> getEndState() {
        return endStates;
    }

    public void setEndState(ArrayList<ArrayList<State>> endState) {
        this.endStates = endState;
    }

    public ArrayList<State> getStartState() {
        return startState;
    }

    public void setStartState(ArrayList<State> startState) {
        this.startState = startState;
    }

    public  ArrayList<ArrayList<State>> getStates() {
        return states;
    }

    public void setStates(ArrayList<ArrayList<State>> states) {
        this.states = states;
    }

    public  ArrayList<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(ArrayList<Transition> transitions) {
        this.transitions = transitions;
    }

}

