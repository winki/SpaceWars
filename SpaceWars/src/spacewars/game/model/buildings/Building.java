package spacewars.game.model.buildings;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;
import spacewars.game.model.Player;
import spacewars.game.model.PlayerElement;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.IUpdateable;
import spacewars.gamelib.Vector;
import spacewars.util.Config;

@SuppressWarnings("serial")
public abstract class Building extends PlayerElement implements IUpdateable
{
   /**
    * Is the building on the energy net
    */
   protected boolean           hasEnergy;
   /**
    * Helper flag to check if a building has already been check for energy
    * availability
    */
   protected transient boolean isCheckedForEngery;
   /**
    * The zero-based upgrade level
    */
   protected byte              level;
   /**
    * The buildings this building is linked with
    */
   protected List<Building>    linkedBuildings;
   /**
    * Helper flags for client to indicate if a building is placeable
    */
   protected transient boolean placeable;
   /**
    * The build state (0 to 100 percent). -1 for not placed.
    */
   protected byte              state;
   
   public Building(final Vector position, final Player player)
   {
      super(position, player, 100);
      
      this.state = -1;
      this.isCheckedForEngery = false;
      this.hasEnergy = false;
      this.placeable = true;
      this.linkedBuildings = new LinkedList<>();
   }
   
   @Override
   public int getSizeRadius()
   {
      return Config.getInt("buildings/" + getConfigName() + "/size");
   }
   
   @Override
   public int getViewRadius()
   {
      return Config.getInt("buildings/" + getConfigName() + "/view");
   }
   
   /**
    * Gets the name of the config section of the derrived subclass.
    * 
    * @return the config section name
    */
   protected abstract String getConfigName();
   
   /**
    * Gets the building name.
    * 
    * @return the building name
    */
   public String getName()
   {
      return Config.getString("buildings/" + getConfigName() + "/name");
   }
   
   /**
    * Gets the building or the upgrading costs. Depends on the level of the
    * building.
    * 
    * @return costs in minerals
    */
   public int getCosts()
   {
      if (!isPlaced()) return Config.getInt("buildings/" + getConfigName() + "/buildingCosts");
      else return Config.getIntArray("buildings/" + getConfigName() + "/upgradingCosts")[level];
   }
   
   /**
    * Gets the highest upgrade level. 0 means, the building can't be upgraded.
    * 
    * @return the highest upgrade level
    */
   public int getHighestLevel()
   {
      return Config.getInt("buildings/" + getConfigName() + "/levels");
   }
   
   /**
    * Gets the current one-based level.
    * 
    * @return upgrade level
    */
   public int getLevel()
   {
      return this.level + 1;
   }
   
   /**
    * Continues the building process by a specified percent value. The building
    * state can't exceed a maximum of 100 percent.
    * 
    * @param percent the percent value
    */
   public void establish(int percent)
   {
      final int state = this.state + percent;
      if (state > 100) this.state = (byte) 100;
      else this.state += (byte) percent;
   }
   
   public int getBuildState()
   {
      return state < 0 ? 0 : state;
   }
   
   /**
    * Gets the total energy consum that is needed to fully establish a building.
    * 
    * @return the total energy consum
    */
   public int getBuildEnergyConsum()
   {
      return Config.getInt("buildings/buildEnergyConsum");
   }
   
   /**
    * Gets a list of all buildings, this building is connected with.
    * 
    * @return list of connected buildings
    */
   public List<Building> getLinks()
   {
      return linkedBuildings;
   }
   
   public boolean hasEnergy()
   {
      return hasEnergy;
   }
   
   public boolean isBuilt()
   {
      return state >= 100;
   }
   
   public boolean isCheckedForEngery()
   {
      return isCheckedForEngery;
   }
   
   public boolean isPlaceable()
   {
      return placeable;
   }
   
   public boolean isPlaced()
   {
      return state > -1;
   }
   
   public boolean isUpgradeable()
   {
      return level + 1 < getHighestLevel();
   }
   
   public int getRecycleReward()
   {
      return 100;
   }
   
   public void place()
   {
      this.state = 0;
   }
   
   @Override
   public final void render(Graphics2D g)
   {
      renderBuilding(g);
      
      final int radius = getSizeRadius();
      final int sight = getViewRadius();
      
      if (isPlaced())
      {
         // no energy, render red point
         if (!hasEnergy())
         {
            final int POINT_RADIUS = 3;
            g.setColor(Color.RED);
            g.fillOval(position.x - POINT_RADIUS, position.y - POINT_RADIUS, 2 * POINT_RADIUS, 2 * POINT_RADIUS);
         }
         
         if (!isBuilt())
         {
            // render build state
            final int maxState = 100;
            final int WIDTH = 20;
            final int HEIGHT = 2;
            final int DY = 8;
            
            // g.setColor(new Color(20, 90, 88));
            // g.fillRect(position.x - WIDTH / 2, position.y - radius - DY -
            // HEIGHT, WIDTH, HEIGHT);
            g.setColor(Color.WHITE);
            g.fillRect(position.x - WIDTH / 2, position.y - radius - DY - HEIGHT, (int) ((double) WIDTH * state / maxState), HEIGHT);
         }
      }
      else
      {
         g.setColor(isPlaceable() ? Color.WHITE : Color.RED);
         // draw name
         g.drawString(getName(), position.x + radius + 2, position.y + 4);
         // draw view radius
         g.drawOval(position.x - sight, position.y - sight, 2 * sight, 2 * sight);
      }
      
      super.render(g);
   }
   
   /**
    * Draw the building itself.
    * 
    * @param g graphics object
    */
   protected void renderBuilding(Graphics2D g)
   {
      final int radius = getSizeRadius();
      
      g.setColor(isPlaced() ? player.getColor() : (isPlaceable() ? Color.WHITE : Color.RED));
      g.fillOval(position.x - radius, position.y - radius, 2 * radius, 2 * radius);
   }
   
   @Override
   protected boolean renderHealth()
   {
      // render health only if building is placed
      return isPlaced();
   }
   
   public void setCheckedForEngery(boolean isCheckedForEngery)
   {
      this.isCheckedForEngery = isCheckedForEngery;
   }
   
   public void setHasEnergy(boolean hasEnergy)
   {
      this.hasEnergy = hasEnergy;
   }
   
   public void setPlaceable(boolean placeable)
   {
      this.placeable = placeable;
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      // don't update building if nothing is specified
      // has to be overwritten by sub class
   }
   
   /**
    * Upgrade building
    */
   public void upgrade()
   {
      if (isUpgradeable())
      {
         this.level++;
      }
   }
}
