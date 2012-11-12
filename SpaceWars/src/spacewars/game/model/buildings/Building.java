package spacewars.game.model.buildings;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.game.model.GameElement;
import spacewars.gamelib.Screen;
import spacewars.gamelib.geometrics.Vector;

public abstract class Building extends GameElement
{
    /**
     * Id for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * The upgrade level of the building
     */
    protected int             level = 1;
    protected boolean         placeable;
    protected boolean         placed;
    
    
    
    public Building(Vector position, int sizeRadius, int viewRadius, int costs)
    {
        super(position, sizeRadius, viewRadius, costs);
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
    
    @Override
    public void render(Graphics2D g)
    {
        if (!isPlaced())
        {
            final Vector o = Screen.getInstance().getViewport().getOriginPosition();
            final Vector p = getPosition().add(o);
            final int r = getViewRadius();
            
            g.setColor(isPlaceable() ? Color.WHITE : Color.RED);
            g.drawOval(p.x - r, p.y - r, 2 * r, 2 * r);
        }
    }
    
    public int getLevel(){
    	return this.level;
    }
}
