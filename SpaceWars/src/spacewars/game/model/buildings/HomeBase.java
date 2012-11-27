package spacewars.game.model.buildings;

import java.awt.Graphics2D;
import java.awt.Image;
import spacewars.game.model.Player;
import spacewars.gamelib.Vector;
import spacewars.util.Ressources;

/**
 * The home planet is a special solar station.
 */
@SuppressWarnings("serial")
public class HomeBase extends SolarStation
{
   public HomeBase(Vector position, Player player)
   {
      super(position, player);
      
      super.radius = 40;
      super.sight = 100;
      super.placed = true;
   }
   
   @Override
   public void renderBuilding(Graphics2D g)
   {
      final Image image = Ressources.loadImage("homeplanet.png");
      g.drawImage(image, position.x - radius - 35, position.y - radius - 22, null);
   }
}
