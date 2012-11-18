package spacewars.game.model;

import java.awt.Color;
import java.io.Serializable;
import spacewars.game.model.planets.HomePlanet;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class Player implements Serializable
{
    /**
     * Which homeplanet does he have
     */
    protected int        id;
    /**
     * The players color
     */
    protected Color color;    
    /**
     * The score
     */
    protected int        score;
    /**
     * The amount of minerals he owns
     */
    protected int        minerals;
    /**
     * The amount of minerals mined per minute
     */
    protected int        mineralsPerMinute;
    /**
     * The players home planet
     */
    protected HomePlanet homePlanet;    
    /**
     * The enable energy
     */
    protected int        energy;
    
    protected int        maxEnergy;
    
    public Player(int id, Color color, Vector homePlanetPosition)
    {
        this.id = id;
        this.color = color;
        this.homePlanet = new HomePlanet(homePlanetPosition);
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
    
    public Color getColor()
    {
        return color;
    }
    
    public HomePlanet getHomePlanet()
    {
        return homePlanet;
    }
    
    public void addMinerals(int mineralsToAdd)
    {
        this.minerals += mineralsToAdd;
    }
    
    public void removeMinerals(int mineralsToRemove)
    {
        this.minerals -= mineralsToRemove;
    }
    
    public void addEnergy(int energy)
    {
        if (this.energy + energy >= maxEnergy)
        {
            this.energy = maxEnergy;
        }
        else
        {   
            this.energy += energy;
        }

    }
    
    public boolean removeEnergy(int energy)
    {
        if (this.energy - energy < 0)
        {
            return false;
        }
        else
        {
            this.energy -= energy;
            return true;
        }
    }
    
    public int getEnergy()
    {
        return this.energy;
    }
    
    public int getMinerals()
    {
        return this.minerals;
    }
    
    public int getMaxEnergy(){
        return maxEnergy;
    }
    
    public void setMaxEnergy(int maxEnergy){
        this.maxEnergy = maxEnergy;
    }
}
