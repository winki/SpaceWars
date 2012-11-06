package spacewars.game.model;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import spacewars.game.MapFactory;
import spacewars.game.SpaceWarsGame;
import spacewars.gamelib.IRenderable;

public class Map implements IRenderable
{
    private int                 width;
    private int                 height;
    private List<MineralPlanet> mineralPlanets;
    private List<Point>         homePlanetPositions;
    private List<Star>          stars;
    
    public Map(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.mineralPlanets = new LinkedList<MineralPlanet>();
        this.homePlanetPositions = new LinkedList<Point>();
        this.stars = new LinkedList<Star>();
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public void addMineralPlanet(MineralPlanet planet)
    {
        assert planet != null;
        mineralPlanets.add(planet);
    }
    
    public void addHomePlanetPosition(Point position)
    {
        assert position != null;
        homePlanetPositions.add(position);
    }
    
    public void addStar(Star star)
    {
        assert star != null;
        stars.add(star);
    }
    
    @Override
    public void render(Graphics2D g)
    {
        for (Star star : stars)
        {
            final int DEEP_DELTA = 2;
            final int DEEP_FACTOR= 2;
            final double FACTOR = 0.5;
            final int SIZE = (int) ((MapFactory.NUMBER_OF_LAYERS - star.getLayer()) * FACTOR);
            final int x = SpaceWarsGame.game.viewport.getViewport().x / (2+DEEP_DELTA+star.getLayer()*DEEP_FACTOR) + star.getPosititon().x - SIZE / 2;
            final int y = SpaceWarsGame.game.viewport.getViewport().y / (2+DEEP_DELTA+star.getLayer()*DEEP_FACTOR) + star.getPosititon().y - SIZE / 2;
            
            Composite original = g.getComposite();
            g.setColor(Color.WHITE);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            g.fillOval(x, y, SIZE, SIZE);
            g.setComposite(original);
        }
        
        for (MineralPlanet planet : mineralPlanets)
        {
            planet.render(g);
        }
        
        for (Point position : homePlanetPositions)
        {
            final int SIZE = 26;            
            final int x = SpaceWarsGame.game.viewport.getViewport().x / 2 + position.x - SIZE / 2;
            final int y = SpaceWarsGame.game.viewport.getViewport().y / 2 + position.y - SIZE / 2;           

            Color color = Color.BLUE;
            g.setColor(color);
            g.fillOval(x, y, SIZE, SIZE);
        }
    }
}
