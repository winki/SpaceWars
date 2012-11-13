package spacewars.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import spacewars.game.SpaceWars;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class GameElement implements IRenderable, Serializable
{
    protected Vector                  position;
    protected int                     radius;
    protected int                     sight;    
    protected int                     health;
    protected int                     power;    
    protected final List<GameElement> links;
    
    public GameElement(Vector position, int radius, int sight)
    {
        this.position = position;
        this.radius = radius;
        this.sight = sight;
        this.links = new LinkedList<>();
    }
    
    public Vector getPosition()
    {
        return position;
    }
    
    public void setPosition(Vector position)
    {
        this.position = position;
    }
    
    public int getSizeRadius()
    {
        return radius;
    }
    
    public int getViewRadius()
    {
        return sight;
    }
    
    public boolean isHit(Vector other)
    {
        return position.distance(other) < radius;
    }
    
    public boolean doesCollideWith(GameElement element)
    {
        return position.distance(element.getPosition()) < getSizeRadius() + element.getSizeRadius();
    }
    
    public boolean doesCollideWith(Line2D line)
    {
        return line.ptSegDist(position.x, position.y) < radius;
    }
    
    public boolean doesReach(GameElement element)
    {
        return position.distance(element.getPosition()) < getViewRadius();
    }
    
    public boolean isReachableFrom(GameElement element)
    {
        return position.distance(element.getPosition()) < element.getViewRadius();
    }
    
    public List<GameElement> getLinks()
    {
        return links;
    }
    
    @Override
    public void render(Graphics2D g)
    {
        if (SpaceWars.DEBUG)
        {
            g.setColor(Color.RED);
            g.drawOval(position.x - radius, position.y - radius, 2 * radius, 2 * radius);
        }
    }
}
