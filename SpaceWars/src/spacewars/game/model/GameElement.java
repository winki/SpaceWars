package spacewars.game.model;

import java.awt.geom.Line2D;
import java.io.Serializable;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.geometrics.Vector;

public abstract class GameElement implements IRenderable, Serializable
{
    /**
     * Id for serialization
     */
    private static final long serialVersionUID = 1L;
    
    protected Vector position;
    protected int sizeRadius;
    protected int reachRadius;
    
    public GameElement(Vector position, int sizeRadius, int viewRadius)
    {
        this.position = position;
        this.sizeRadius = sizeRadius;
        this.reachRadius = viewRadius;
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
        return sizeRadius;
    }
    
    public int getViewRadius()
    {
        return reachRadius;
    }
    
    public boolean isHit(Vector other)
    {
        return position.distance(other) < sizeRadius;
    }
    
    public boolean doesCollideWith(GameElement element)
    { 
        return position.distance(element.getPosition()) < getSizeRadius() + element.getSizeRadius();
    }
    
    public boolean doesCollideWith(Line2D line)
    { 
        return line.ptSegDist(position.x, position.y) < sizeRadius;
    }
        
    public boolean doesReach(GameElement element)
    {
        return position.distance(element.getPosition()) < getViewRadius();
    }
    
    public boolean isReachableFrom(GameElement element)
    {
        return position.distance(element.getPosition()) < element.getViewRadius();
    }  
}
