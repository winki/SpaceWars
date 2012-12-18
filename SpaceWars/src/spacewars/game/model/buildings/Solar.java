package spacewars.game.model.buildings;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.game.model.Player;
import spacewars.gamelib.Vector;
import spacewars.util.Config;

@SuppressWarnings("serial")
public class Solar extends Building
{  
   public Solar(final Vector position, final Player player)
   {
      super(position, 15, 100, player);
   }
   
   @Override
   protected String getConfigName()
   {
      return Solar.class.getSimpleName();
   }
   
   public int getEnergyProduction()
   {
      return Config.getIntArrayValue("buildings/" + getConfigName() + "/energyProduction")[level];
   }
   
   public int getEnergyCapacity()
   {
      return Config.getIntArrayValue("buildings/" + getConfigName() + "/energyCapacity")[level];
   }   

   /*
    * TODO: handle energy production
   @Override
   public void update(GameTime gameTime)
   {
      if (energy + getEnergyProduction() > getMaxEnergy()) energy = getMaxEnergy();
      else energy += getEnergyProduction();
   }
   */
   
   @Override
   protected void renderBuilding(Graphics2D g)
   {
      super.renderBuilding(g);
      
      if (isBuilt() || !isPlaced())
      {
         // draw icon
         final int BORDER = 4;
         g.setColor(Color.BLACK);
         g.fillArc(position.x - radius + BORDER, position.y - radius + BORDER, 2 * (radius - BORDER), 2 * (radius - BORDER), 60, 60);
         g.fillArc(position.x - radius + BORDER, position.y - radius + BORDER, 2 * (radius - BORDER), 2 * (radius - BORDER), 180, 60);
         g.fillArc(position.x - radius + BORDER, position.y - radius + BORDER, 2 * (radius - BORDER), 2 * (radius - BORDER), 300, 60);
      }
   }
}
