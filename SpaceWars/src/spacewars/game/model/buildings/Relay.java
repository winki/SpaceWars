package spacewars.game.model.buildings;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.game.model.Player;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public class Relay extends Building
{
   public Relay(final Vector position, final Player player)
   {
      super(position, player);
   }
   
   @Override
   protected String getConfigName()
   {
      return Relay.class.getSimpleName();
   }
   
   @Override
   protected void renderBuilding(Graphics2D g)
   {
      super.renderBuilding(g);
      
      final int radius = getSizeRadius();
      
      if (isBuilt() || !isPlaced())
      {
         // draw icon
         final int BORDER = 2;
         g.setColor(Color.CYAN);
         g.fillOval(position.x - radius + BORDER, position.y - radius + BORDER, 2 * (radius - BORDER), 2 * (radius - BORDER));
      }
   }
}
