package spacewars.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.Screen;

public class MineralPlanet extends Planet implements IRenderable
{
    private int mineralReserves;
    
    public MineralPlanet(int x, int y, int mineralReserves)
    {
    	this.x = x;
        this.y = y;
        this.mineralReserves = mineralReserves;
        this.size = 20;
    }
    
    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getMineralReserves()
    {
        return mineralReserves;
    }
    
    public int getSize()
    {
        return size;
    }

    @Override
    public void render(Graphics2D g)
    {
        final int x = Screen.getInstance().getViewport().getOriginPosition().x + getX() - getSize()/2;
        final int y = Screen.getInstance().getViewport().getOriginPosition().y + getY() - getSize()/2;     
        
        Color color = new Color(Color.HSBtoRGB(0.3f, 1.0f, 0.01f * getMineralReserves()));
        g.setColor(color);
        g.fillOval(x, y, getSize(), getSize());
    }
}
