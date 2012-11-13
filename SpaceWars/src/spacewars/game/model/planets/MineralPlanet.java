package spacewars.game.model.planets;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class MineralPlanet extends Planet implements IRenderable
{
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
        Color color = new Color(Color.HSBtoRGB(0.3f, 1.0f, 0.01f * getMineralReserves()));
        g.setColor(color);
        g.fillOval(position.x - radius, position.y - radius, 2 * radius, 2 * radius);
        
        super.render(g);
    }
}
