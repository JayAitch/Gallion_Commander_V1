package uk.ac.brighton.jh1152.gallioncommanderv1;

import java.util.HashMap;
import java.util.Map;

public class BoatData {
    private int lives;
    private int players;

    public BoatData(){
    }

    public BoatData(int lives, int players){
        this.lives = lives;
        this.players = players;
    }

    public int getLives(){
        return  lives;
    }

    public int getPlayers(){
        return players;
    }

    public Map<String, Object> getData(){
        HashMap<String, Object> data = new HashMap<>();
        data.put("lives", lives);
        data.put("players", players);
        return data;
    }
}
