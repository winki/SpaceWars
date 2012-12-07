package spacewars.game.model.buildings;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.game.model.Player;
import spacewars.game.model.Ship;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public class Shipyard extends Building
{
   protected static final String name = "Shipyard";  
   protected static final int[]  secondsPerShipbuild        = { 8, 7, 6, 5, 4 };
   
   
   public Shipyard(final Vector position, final Player player)
   {
      super(position, 20, 100, player, 500);
   }
   
   @Override
   public String getName()
   {
      return name;
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      if (hasEnergy())
      {         
         // repeat every x seconds depending on the level
         if (gameTime.timesPerSecond(1/secondsPerShipbuild[level]))
         {
            final Ship ship = new Ship(player, new Vector(position), 0);
            getGameState().getShips().add(ship);
         }
      }
   }
   
   @Override
   protected void renderBuilding(Graphics2D g)
   {
      super.renderBuilding(g);
      
      // draw icon
      final int BORDER = 6;
      g.setColor(Color.BLACK);
      g.fillOval(position.x - radius + BORDER, position.y - radius + BORDER, 2 * (radius - BORDER), 2 * (radius - BORDER));   
   }
}
