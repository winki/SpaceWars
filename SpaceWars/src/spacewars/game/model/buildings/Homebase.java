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
public class Homebase extends Solar
{
   public Homebase(Vector position, Player player)
   {
      super(position, player);
      
      // already built and produces energy
      super.state = 100;
      super.hasEnergy = true;
   }
   
   @Override
   protected String getConfigName()
   {
      return Homebase.class.getSimpleName();
   }
   
   @Override
   public void renderBuilding(Graphics2D g)
   {
      final int DY_NAME = 6;
      final int radius = getSizeRadius();
      final String name = getPlayer().getName();
      
      final Image image = Ressources.loadImage("homeplanet.png");
      g.drawImage(image, position.x - radius - 35, position.y - radius - 22, null);     

      g.setColor(getPlayer().getColor());
      g.drawString(name, position.x - g.getFontMetrics().stringWidth(name) / 2, position.y + g.getFontMetrics().getHeight() / 2 + getSizeRadius() + DY_NAME);
   }
}
