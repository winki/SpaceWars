package spacewars.game.model.planets;

import java.awt.Graphics2D;
import java.awt.Image;
import spacewars.gamelib.geometrics.Vector;
import spacewars.util.Ressources;

@SuppressWarnings("serial")
public class HomePlanet extends Planet
{
    public HomePlanet(Vector position)
    {
        super(position, 40, 100);
    }
    
    @Override
    public void render(Graphics2D g)
    {
        final Image image = Ressources.loadImage("homeplanet.png");        
        g.drawImage(image, position.x - radius - 35, position.y - radius - 22, null);
        
        super.render(g);
    }
}
