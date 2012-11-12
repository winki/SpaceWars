package spacewars.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.gamelib.Screen;
import spacewars.gamelib.geometrics.Vector;

public class Ship extends GameElement
{
    /**
     * Id for serialization
     */
    private static final long serialVersionUID = 1L;
    
    public int costs;
    public int power;
    public int speed;
    public int health;
    
    public Ship(Vector position)
    {
        super(position, 5, 100);
    }
        
    @Override
    public void render(Graphics2D g)
    {
        final Vector o = Screen.getInstance().getViewport().getOriginPosition();
        final Vector p = getPosition().add(o);
        final int r = getSizeRadius();
        
        g.setColor(Color.BLUE);
        g.fillOval(p.x - r, p.y - r, 2 * r, 2 * r);
    }
}
