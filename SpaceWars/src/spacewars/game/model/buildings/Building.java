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

@SuppressWarnings("serial")
public abstract class Building extends PlayerElement implements IUpdateable
{
   /**
    * The upgrade level (zero-based)
    */
   protected byte              level;
   /**
    * The build state (0 to 100 percent). -1 for not placed.
    */
   protected byte              state;
   /**
    * Helper flags for client to indicate if a building is placeable
    */
   protected transient boolean placeable;
   /**
    * Helper flag to check if a building has already been check for energy
    * availability
    */
   protected transient boolean isCheckedForEngery;
   /**
    * Is the building on the energy net
    */
   protected boolean           hasEnergy;
   /**
    * The buildings this building is linked with
    */
   protected List<Building>    linkedBuildings;
   
   public Building(final Vector position, final int radius, final int sight, final Player player)
   {
      super(position, radius, sight, player, 100);
      
      this.state = -1;
      this.isCheckedForEngery = false;
      this.hasEnergy = false;
      this.placeable = true;
      this.linkedBuildings = new LinkedList<>();
   }
   
   public boolean isPlaceable()
   {
      return placeable;
   }
   
   public void setPlaceable(boolean placeable)
   {
      this.placeable = placeable;
   }
   
   public boolean isPlaced()
   {
      return state > -1;
   }
   
   public boolean hasEnergy()
   {
      return hasEnergy;
   }
   
   public boolean isCheckedForEngery()
   {
      return isCheckedForEngery;
   }
   
   public void setCheckedForEngery(boolean isCheckedForEngery)
   {
      this.isCheckedForEngery = isCheckedForEngery;
   }
   
   public void setHasEnergy(boolean hasEnergy)
   {
      this.hasEnergy = hasEnergy;
   }
   
   public void place()
   {
      this.state = 0;
   }
   
   /**
    * Gets the building name of the derrived subclass.
    * 
    * @return the building name
    */
   public abstract String getName();
   
   public int getLevel()
   {
      return this.level;
   }
   
   public List<Building> getLinks()
   {
      return linkedBuildings;
   }
   
   /**
    * Gets the costs of a building
    * 
    * @return costs in minerals
    */
   public abstract int getCosts();
   
   /**
    * Upgrade building
    */
   public void upgrade()
   {
      this.level++;
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      // don't update building if nothing is specified
      // has to be overwritten by sub class
   }
   
   @Override
   protected boolean renderHealth()
   {
      // render health only if building is placed
      return isPlaced();
   }
   
   @Override
   public final void render(Graphics2D g)
   {
      renderBuilding(g);
      
      if (isPlaced())
      {
         // no energy, render red point
         if (!hasEnergy())
         {
            final int POINT_RADIUS = 3;
            g.setColor(Color.RED);
            g.fillOval(position.x - POINT_RADIUS, position.y - POINT_RADIUS, 2 * POINT_RADIUS, 2 * POINT_RADIUS);
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
      g.setColor(isPlaced() ? player.getColor() : (isPlaceable() ? Color.WHITE : Color.RED));
      g.fillOval(position.x - radius, position.y - radius, 2 * radius, 2 * radius);
   }
}
