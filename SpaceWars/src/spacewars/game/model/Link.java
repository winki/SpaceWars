package spacewars.game.model;

import java.awt.geom.Line2D;

/**
 * This class represents a connection between the building that will be built
 * and another building that is already built.
 */
public class Link<T>
{
    private T linkedElement;
    private Line2D   line;
    private boolean  collision;
    
    public Link(T linkedElement, Line2D line, boolean collision)
    {
        this.linkedElement = linkedElement;
        this.line = line;
        this.collision = collision;
    }
    
    public Line2D getLine()
    {
        return line;
    }
    
    public T getLinkedElement()
    {
        return linkedElement;
    }
    
    public boolean isCollision()
    {
        return collision;
    }
}
