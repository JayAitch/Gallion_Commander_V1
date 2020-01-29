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


    public void manageInstructionList(BoatAction action) {

        if (action.isActionComplete()) {
            removeFromInstructions(action.documentReference);

        } else {
            addToInstructions(action);
        }
    }


    public String getCurrentInstructionString(){
        return currentInstruction.getValue();
    }


    private void removeFromInstructions(String actionRef) {

        if(boatInstructions.containsKey(actionRef)){
            boatInstructions.remove(actionRef);
        }
    }


    private void addToInstructions(BoatAction action){
        boatInstructions.put(action.documentReference, action.getInstructionText());
    }


    public void setRandomInstruction(){
        int instructionSize = boatInstructions.entrySet().size();
        if(instructionSize > 0){
            int instructionsIncrementor = 0;
            int randomPosition = random.nextInt(instructionSize);
            for(Map.Entry<String, String> instruction: boatInstructions.entrySet()){

                if(instructionsIncrementor == randomPosition){
                    currentInstruction = instruction;
                }
                instructionsIncrementor++;
            }

        }
    }

    public void removeCurrentInstruction(){
        currentInstruction = null;
    }

    public int getInstructionsSize(){
        return  boatInstructions.entrySet().size();
    }

    public boolean hasAnInstruction(){
        return (currentInstruction != null);
    }
    public boolean isCurrentInstruction(String instructionKey){
        if(currentInstruction == null) return false;
        return (instructionKey == currentInstruction.getKey());
    }

}
