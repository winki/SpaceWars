package spacewars.game.model.buildings;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;
import spacewars.game.model.Player;
import spacewars.game.model.PlayerElement;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.IUpdateable;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public abstract class Building extends PlayerElement implements IUpdateable
{
   /**
    * The upgrade level of the building
    */
   protected int              level              = 1;
   protected boolean          placeable;
   protected boolean          placed;
   protected int              costs;
   /**
    * Helper flag to check if a building has already been check for energy
    * availability
    */
   protected volatile boolean isCheckedForEngery = false;
   /**
    * Is the building on the energy net
    */
   protected boolean          hasEnergy          = false;
   protected List<Building>   linkedBuildings;
   
   /*
    * TODO: kai
    * - Baustatus
    */
   
   public Building(final Player player, final Vector position, int sizeRadius, int viewRadius, int costs)
   {
      super(player, position, sizeRadius, viewRadius);
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
      return placed;
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
      this.placed = true;
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
   
   @Override
   public void update(GameTime gameTime)
   {
      // don't update building if nothing is specified
      // has to be overwritten by sub class
   }
   
   @Override
   public void render(Graphics2D g)
   {
      g.setColor(isPlaceable() ? (isPlaced() ? player.getColor() : Color.WHITE) : Color.RED);
      g.fillOval(position.x - radius, position.y - radius, 2 * radius, 2 * radius);
      
      if (isPlaced())
      {
         // no energy, render red point
         if (!hasEnergy)
         {
            g.setColor(Color.RED);
            g.fillOval(position.x - radius / 2, position.y - radius / 2, radius, radius);
         }
      }
      else
      {
         g.setColor(isPlaceable() ? Color.WHITE : Color.RED);
         g.drawString(getName(), position.x + radius + 2, position.y + 4);
         
         g.drawOval(position.x - sight, position.y - sight, 2 * sight, 2 * sight);
      }
      
      super.render(g);
   }
   
   /**
    * upgrade building
    * 
    */
   public void upgrade()
   {
      this.level++;
   }
}
