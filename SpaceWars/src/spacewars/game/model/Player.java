package spacewars.game.model;

import java.io.Serializable;
import spacewars.game.model.planets.HomePlanet;

@SuppressWarnings("serial")
public class Player implements Serializable
{
    /**
     * which homeplanet does he have
     */
    protected int id;
    /**
     * 
     */
    protected int score;
    /**
     * amount of minerals he owns
     */
    protected int minerals;    
    protected int mineralsPerMinute;
    protected HomePlanet homePlanet;
    protected int energy;
    
    public Player(int id, Map map)
    {   
        this.id = id;
        this.homePlanet = new HomePlanet(map.getHomePlanetPosition(id));
    }

    public int getScore()
    {
        return score;
    }
    
    public void setScore(int score)
    {
        this.score = score;
    }
    
    public int getId()
    {
        return id;
    }
    
    public HomePlanet getHomePlanet()
    {
        return homePlanet;
    }
    
    public void addMinerals(int mineralsToAdd){
    	this.minerals += mineralsToAdd;
    }
    
    public void removeMinerals(int mineralsToRemove){
    	this.minerals -= mineralsToRemove;
    }
    
    public void addEnergy(int energy){
    	this.energy += energy;
    }
    
    public void removeEnergy(int energy){
    	this.energy -= energy;
    }
    
    public int getEnergy(){
    	return this.energy;
    }
    
    public int getMinerals(){
    	return this.minerals;
    }
}
