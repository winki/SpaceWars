package spacewars.game.model;

import java.awt.geom.Line2D;
import spacewars.game.model.buildings.Building;

/**
 * This class represents a connection between the building that will be built
 * and another building that is already built.
 */
public class Link
{
    private Building linkedBuilding;
    private Line2D   line;
    private boolean  collision;
    
    public Link(Building linkedBuilding, Line2D line, boolean collision)
    {
        this.linkedBuilding = linkedBuilding;
        this.line = line;
        this.collision = collision;
    }
    
    public Line2D getLine()
    {
        return line;
    }
    
    public Building getLinkedBuilding()
    {
        return linkedBuilding;
    }
    
    public boolean isCollision()
    {
        return collision;
    }
}
