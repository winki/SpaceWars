package spacewars.game.model.buildings;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.game.model.Player;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public class Relay extends Building
{
   protected static final String name = "Relay";
   
   public Relay(final Vector position, final Player player)
   {
      super(position, 5, 100, player);
   }
   
   @Override
   public String getName()
   {
      return name;
   }
   
   @Override
   public int getCosts()
   {
      return 200;
   }
   
   @Override
   protected void renderBuilding(Graphics2D g)
   {
      super.renderBuilding(g);
      
      if (isBuilt() || !isPlaced())
      {
         // draw icon
         final int BORDER = 2;
         g.setColor(Color.CYAN);
         g.fillOval(position.x - radius + BORDER, position.y - radius + BORDER, 2 * (radius - BORDER), 2 * (radius - BORDER));
      }
   }
}
