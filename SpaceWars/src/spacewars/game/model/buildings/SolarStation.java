package spacewars.game.model.buildings;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.gamelib.Screen;
import spacewars.gamelib.geometrics.Vector;

public class SolarStation extends Building
{    
    /**
     * Id for serialization
     */
    private static final long serialVersionUID = 1L;
    
    private static int costs = 400;
    
    protected int energyPerMin = 100;
    
    public SolarStation(Vector position)
    {
        super(position, 15, 100, costs);
        this.energyPerMin = level * energyPerMin;
    }
    
    @Override
    public void render(Graphics2D g)
    {
        super.render(g);
        
        final Vector o = Screen.getInstance().getViewport().getOriginPosition();
        final Vector p = getPosition().add(o);
        final int r = getSizeRadius();
        
        g.setColor(isPlaceable() ? Color.WHITE : Color.RED);
        g.fillOval(p.x - r, p.y - r, 2 * r, 2 * r);
        g.drawString("Solar", p.x + r + 2, p.y + 4);
    }
    
    public int getEnergyPerMin(){
    	return energyPerMin;
    }
}
