package spacewars.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.Screen;
import spacewars.gamelib.geometrics.Vector;

public class MineralPlanet extends Planet implements IRenderable
{
    protected int mineralReserves;

    
    public MineralPlanet(Vector position, int mineralReserves)
    {
        super(position, 20, 0);

    	this.x = x;
        this.y = y;

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
        final Vector p = Screen.getInstance().getViewport().getOriginVector().add(position);
        
        Color color = new Color(Color.HSBtoRGB(0.3f, 1.0f, 0.01f * getMineralReserves()));
        g.setColor(color);
        g.fillOval(p.x - getSizeRadius(), p.y - getSizeRadius(), 2 * getSizeRadius(), 2 * getSizeRadius());
    }
}
