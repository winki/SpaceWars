package spacewars.game.model.buildings;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.game.model.Player;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public class SolarStation extends Building
{
   protected static final String name              = "Solar";
   protected static final int[]  maxEnergys        = { 4, 12, 15, 20, 27 };
   protected static final int[]  energyProductions = { 4, 12, 15, 20, 27 };
   
   protected int                 energy;
   
   public SolarStation(final Vector position, final Player player)
   {
      super(position, 15, 100, player, 100);
      
      this.hasEnergy = true;
      this.energy = 0;
   }
   
   @Override
   public String getName()
   {
      return name;
   }
   
   public int getEnergy()
   {
      return energy;
   }
   
   public int getEnergyProduction()
   {
      return energyProductions[level];
   }
   
   public int getMaxEnergy()
   {
      return maxEnergys[level];
   }
   
   @Override
   public void upgrade()
   {
      costs += 50;
      
      super.upgrade();
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      if (energy + getEnergyProduction() > getMaxEnergy())
         energy = getMaxEnergy();
      else energy += getEnergyProduction();
   }
   
   @Override
   protected void renderBuilding(Graphics2D g)
   {
      super.renderBuilding(g);
      
      // draw icon
      final int BORDER = 4;
      g.setColor(Color.BLACK);
      g.fillArc(position.x - radius + BORDER, position.y - radius + BORDER, 2 * (radius - BORDER), 2 * (radius - BORDER), 60, 60);
      g.fillArc(position.x - radius + BORDER, position.y - radius + BORDER, 2 * (radius - BORDER), 2 * (radius - BORDER), 180, 60);
      g.fillArc(position.x - radius + BORDER, position.y - radius + BORDER, 2 * (radius - BORDER), 2 * (radius - BORDER), 300, 60);
   }
}
