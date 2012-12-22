package spacewars.game.model.buildings;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.game.model.Player;
import spacewars.game.model.Ship;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.Vector;
import spacewars.util.Config;

@SuppressWarnings("serial")
public class Shipyard extends Building
{
   public Shipyard(final Vector position, final Player player)
   {
      super(position, player);
   }
   
   @Override
   protected String getConfigName()
   {
      return Shipyard.class.getSimpleName();
   }
   
   public double getBuildingFrequency()
   {
      return 1.0 / Config.getIntArray("buildings/" + getConfigName() + "/buildingTime")[level];
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      if (isBuilt() && hasEnergy())
      {
         // repeat every x seconds depending on the level
         if (gameTime.timesPerSecond(getBuildingFrequency()))
         {
            final Ship ship = new Ship(player, new Vector(position), 0);
            getServerGameState().getShips().add(ship);
         }
      }
   }
   
   @Override
   protected void renderBuilding(Graphics2D g)
   {
      super.renderBuilding(g);
      
      final int radius = getSizeRadius();
      
      if (isBuilt() || !isPlaced())
      {
         // draw icon
         final int BORDER = 6;
         g.setColor(Color.BLACK);
         g.fillOval(position.x - radius + BORDER, position.y - radius + BORDER, 2 * (radius - BORDER), 2 * (radius - BORDER));
      }
   }
}
