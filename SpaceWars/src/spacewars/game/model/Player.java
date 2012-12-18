package spacewars.game.model;

import java.awt.Color;
import java.io.Serializable;
import spacewars.game.model.buildings.HomeBase;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public class Player implements Serializable
{
   /**
    * Which homeplanet does he have
    */
   protected int      id;
   /**
    * The players color
    */
   protected Color    color;
   /**
    * The score
    */
   protected int      score;
   /**
    * The amount of minerals he owns
    */
   protected int      minerals;
   /**
    * The amount of minerals mined per minute
    */
   protected int      mineralsPerMinute;
   /**
    * The players home planet
    */
   protected HomeBase homePlanet;
   /**
    * The enable energy
    */
   protected int      energy;
   protected int      energyMax = 100;
   
   public Player(final int id, final Color color, final Vector position, final int minerals)
   {
      this.id = id;
      this.color = color;
      this.homePlanet = new HomeBase(position, this);
      this.minerals = minerals;
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
   
   public HomeBase getHomePlanet()
   {
      return homePlanet;
   }
   
   public int getMinerals()
   {
      return this.minerals;
   }
   
   // TODO: only returns a dummy value
   public int getMineralsPerMinute()
   {
      return 2;
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
   
   // TODO: only returns a dummy value
   public int getEnergyEfficency()
   {
      return 60;
   }
   
   public int getMaxEnergy()
   {
      return energyMax;
   }
   
   public void setMaxEnergy(int maxEnergy)
   {
      this.energyMax = maxEnergy;
   }
   
   public void addEnergy(int energy)
   {
      if (this.energy + energy >= energyMax)
      {
         this.energy = energyMax;
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
   
   public boolean isEnemy(Player player) 
   {
      return id != player.id;
   }
}
