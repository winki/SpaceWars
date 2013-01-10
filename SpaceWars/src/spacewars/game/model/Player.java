package spacewars.game.model;

import java.awt.Color;
import java.io.Serializable;
import spacewars.game.model.buildings.Homebase;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public class Player implements Serializable
{
   /**
    * The players id. Can be used to get the player object from the players list
    * in the gamestate.
    * <p>
    * <code>gameState.getPlayers().get(id);</code>
    */
   protected final int    id;
   /**
    * The players name
    */
   protected final String name;
   /**
    * The players color
    */
   protected final Color  color;
   /**
    * The score
    */
   protected int          score;
   /**
    * The amount of minerals he owns
    */
   protected int          minerals;
   /**
    * The amount of minerals mined per minute
    */
   protected int          mineralsPerMinute;
   /**
    * The players home planet
    */
   protected Homebase     homePlanet;
   /**
    * The enable energy
    */
   protected int          energy;
   /**
    * The maximum energy capacity from all solars
    */
   protected int          energyCapacity;
   
   public Player(final int id, final String name, final Color color, final Vector position, final int minerals)
   {
      this.id = id;
      this.name = name;
      this.color = color;
      this.homePlanet = new Homebase(position, this);
      this.minerals = minerals;
   }
   
   public int getId()
   {
      return id;
   }
   
   public boolean isEnemy(Player player)
   {
      return id != player.id;
   }
   
   public String getName()
   {
      return name;
   }
   
   public int getScore()
   {
      return score;
   }
   
   public void resetScore()
   {
      this.score = 0;
   }
   
   public void addScore(int score)
   {
      this.score += score;
   }
   
   public Color getColor()
   {
      return color;
   }
   
   public Homebase getHomePlanet()
   {
      return homePlanet;
   }
   
   public int getMinerals()
   {
      return this.minerals;
   }
   
   public int getMineralsPerMinute()
   {
      return this.mineralsPerMinute;
   }
   
   public void resetMineralsPerMinute()
   {
      this.mineralsPerMinute = 0;
   }
   
   public void addMinerals(int mineralsToAdd)
   {
      this.minerals += mineralsToAdd;
   }
   
   public void removeMinerals(int mineralsToRemove)
   {
      this.minerals -= mineralsToRemove;
   }
   
   public int getEnergy()
   {
      return this.energy;
   }
   
   public void addEnergy(int energy)
   {
      if (this.energy + energy >= energyCapacity)
      {
         this.energy = energyCapacity;
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
   
   public int getEnergyCapacity()
   {
      return energyCapacity;
   }
   
   public void addEnergyCapacity(int energyCapacity)
   {
      this.energyCapacity += energyCapacity;
   }
   
   public void resetEnergyCapacity()
   {
      this.energyCapacity = 0;
   }
   
   public void addMineralsPerMinute(int mineralsPerMinute)
   {
      this.mineralsPerMinute += mineralsPerMinute;
   }
   
   /**
    * Gets the percentage of the available energy to the total energy capcity of
    * all solars.
    * 
    * @return available energy in percent
    */
   public double getEnergyEfficency()
   {
      return 100.0 * getEnergy() / getEnergyCapacity();
   }
   
   @Override
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (obj instanceof Player)
      {
         final Player player = (Player) obj;
         return id == player.id;
      }
      return false;
   }
   
   @Override
   public int hashCode()
   {
      return id;
   }
}
