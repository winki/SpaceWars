package spacewars.game.model.planets;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.game.SpaceWars;
import spacewars.gamelib.Screen;
import spacewars.gamelib.geometrics.Vector;
import spacewars.util.Ressources;

public class HomePlanet extends Planet
{
    /**
     * Id for serialization
     */
    private static final long serialVersionUID = 1L;
    
    public HomePlanet(Vector position)
    {
        super(position, 40, 100);
    }
    
    @Override
    public void render(Graphics2D g)
    {
        final Vector o = Screen.getInstance().getViewport().getOriginPosition();
        final Vector p = getPosition().add(o);
        final int r = getSizeRadius();
        
        final int dx = -35;
        final int dy = -22; 
        g.drawImage(Ressources.loadBufferedImage("homeplanet.png"), p.x - r + dx, p.y - r + dy, null);
        
        if (SpaceWars.DEBUG)
        {
            g.setColor(Color.RED);
            g.drawOval(p.x - r, p.y - r, 2 * r, 2 * r);
        }
    }
}
