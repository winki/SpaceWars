package spacewars.game.model.planets;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.Screen;
import spacewars.gamelib.geometrics.Vector;

public class MineralPlanet extends Planet implements IRenderable
{
    /**
     * Id for serialization
     */
    private static final long serialVersionUID = 1L;
    
    protected int mineralReserves;
    
    public MineralPlanet(Vector position, int sizeRadius, int mineralReserves)
    {
        super(position, sizeRadius, 0);
        this.mineralReserves = mineralReserves;
    }
    
    public int getMineralReserves()
    {
        return mineralReserves;
    }
    
    public void setMineralReserves(int mineralReserves)
    {
        this.mineralReserves = mineralReserves;
    }
    
    @Override
    public void render(Graphics2D g)
    {
        final Vector o = Screen.getInstance().getViewport().getOriginPosition();
        final Vector p = getPosition().add(o);
        final int r = getSizeRadius();
        
        Color color = new Color(Color.HSBtoRGB(0.3f, 1.0f, 0.01f * getMineralReserves()));
        g.setColor(color);
        g.fillOval(p.x - r, p.y - r, 2 * r, 2 * r);
    }
}
