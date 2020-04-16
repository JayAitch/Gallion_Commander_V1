package uk.ac.brighton.jh1152.gallioncommanderv1;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InstructionManager {
    private HashMap.Entry<String, String> currentInstruction;
    private HashMap<String, String> boatInstructions;
    Random random;


    public InstructionManager(){
        boatInstructions = new HashMap<>();
        random = new Random();
    }

    // either remove or add the action as an instrution depending on its completeness
    public void manageInstructionList(BoatAction action) {

        if(action == null) return;
        if (action.isActionComplete()) {
            removeFromInstructions(action.documentReference);

        } else {
            addToInstructions(action);
        }
    }

    // how is the current instruction to be displayed
    public String getCurrentInstructionString(){
        return currentInstruction.getValue();
    }


    // remove the action from the instructions list
    private void removeFromInstructions(String actionRef) {

        if(boatInstructions.containsKey(actionRef)){
            boatInstructions.remove(actionRef);
        }
    }

    //incomplete instruction, add to boat instruction collection
    private void addToInstructions(BoatAction action){
        boatInstructions.put(action.documentReference, action.getInstructionText());
    }

    // role a random instruction for the player to read out
    public void setRandomInstruction(){
        int instructionSize = boatInstructions.entrySet().size();
        if(instructionSize > 0){
            int instructionsIncrementor = 0;
            int randomPosition = random.nextInt(instructionSize);
            for(Map.Entry<String, String> instruction: boatInstructions.entrySet()){

                if(instructionsIncrementor == randomPosition){
                    currentInstruction = instruction;
                    return;
                }
                instructionsIncrementor++;
            }

        }
    }

    public void removeCurrentInstruction(){
        currentInstruction = null;
    }

    // how many actions are left incomplete
    public int getInstructionsSize(){
        return  boatInstructions.entrySet().size();
    }

    // is the instruction allocated
    public boolean hasAnInstruction(){
        return (currentInstruction != null);
    }

    // is the action a current instruction
    public boolean isCurrentInstruction(String instructionKey){
        if(currentInstruction == null) return false;
        return (instructionKey == currentInstruction.getKey());
    }

}
