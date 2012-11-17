package spacewars.game.model.buildings;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.game.model.GameElement;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public abstract class Building extends GameElement
{
    /**
     * The upgrade level of the building
     */
    protected int     level;
    protected boolean placeable;
    protected boolean placed;
    protected int     costs;
    
    /*
     * TODO: kai
     * - Baustatus
     */
    
    public Building(Vector position, int sizeRadius, int viewRadius, int costs)
    {
        super(position, sizeRadius, viewRadius);
        this.placeable = true;
    }
    
    public boolean isPlaceable()
    {
        return placeable;
    }
    
    public void setPlaceable(boolean placeable)
    {
        this.placeable = placeable;
    }
    
    public boolean isPlaced()
    {
        return placed;
    }
    
    public void place()
    {
        this.placed = true;
    }
    
    /**
     * Gets the building name of the derrived subclass.
     * 
     * @return the building name
     */
    public abstract String getName();
    
    public int getLevel()
    {
        return this.level;
    }
    
    @Override
    public void render(Graphics2D g)
    {
        g.setColor(isPlaceable() ? (isPlaced() ? Color.MAGENTA : Color.WHITE) : Color.RED);
        g.fillOval(position.x - radius, position.y - radius, 2 * radius, 2 * radius);
        
        if (!isPlaced())
        {
            g.setColor(isPlaceable() ? Color.WHITE : Color.RED);
            g.drawString(getName(), position.x + radius + 2, position.y + 4);
            
            g.drawOval(position.x - sight, position.y - sight, 2 * sight, 2 * sight);
        }
        
        super.render(g);
    }
   
    /**
     * upgrade building
     *  
     */
    public void upgrade(){
        this.level++;
    }
}
