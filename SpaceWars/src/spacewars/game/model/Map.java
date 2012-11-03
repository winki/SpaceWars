package spacewars.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import spacewars.gamelib.IRenderable;

public class Map implements IRenderable
{
    private static final int    UNIT_SIZE = 6;
    
    private int                 width;
    private int                 height;
    private List<MineralPlanet> mineralPlanets;
    private List<Point>         homePlanetPositions;
    
    public Map(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.mineralPlanets = new LinkedList<MineralPlanet>();
        this.homePlanetPositions = new LinkedList<Point>();
    }
    
    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public void addMineralPlanet(int x, int y, int mineralReserves)
    {
        mineralPlanets.add(new MineralPlanet(getPixelPosition(x), getPixelPosition(y), mineralReserves));
    }
    
    public void addHomePlanetPosition(int x, int y)
    {
        homePlanetPositions.add(new Point(getPixelPosition(x), getPixelPosition(y)));
    }    
    
    private int getPixelPosition(int unitPosition)
    {
        return unitPosition * UNIT_SIZE + UNIT_SIZE / 2;
    }
    
    @Override
    public void render(Graphics2D g)
    {
        for (MineralPlanet planet : mineralPlanets)
        {  
            planet.render(g);
        }
        
        for (Point position : homePlanetPositions)
        {  
            Color color = Color.BLUE;
            g.setColor(color);
            final int SIZE = 10;
            g.fillOval(position.x - SIZE/2, position.y - SIZE/2, SIZE, SIZE);
        }
    }
}
