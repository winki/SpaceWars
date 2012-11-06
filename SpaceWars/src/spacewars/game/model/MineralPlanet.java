package spacewars.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import spacewars.game.SpaceWarsGame;
import spacewars.gamelib.IRenderable;

public class MineralPlanet extends Planet implements IRenderable
{
    private int x;
    private int y;
    private int mineralReserves;
    private int size;
    
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
        final int x = SpaceWarsGame.game.viewport.getViewport().x / 2 + getX() - getSize()/2;
        final int y = SpaceWarsGame.game.viewport.getViewport().y / 2 + getY() - getSize()/2;     
        
        Color color = new Color(Color.HSBtoRGB(0.3f, 1.0f, 0.01f * getMineralReserves()));
        g.setColor(color);
        g.fillOval(x, y, getSize(), getSize());
    }
}
