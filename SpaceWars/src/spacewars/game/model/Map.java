package spacewars.game.model;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import spacewars.game.MapFactory;
import spacewars.game.SpaceWars;
import spacewars.game.model.planets.MineralPlanet;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.Screen;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class Map implements IRenderable, Serializable
{
    private int                 width;
    private int                 height;
    private List<MineralPlanet> mineralPlanets;
    private List<Vector>        homePlanetPositions;
    private List<Star>          stars;
    
    public Map(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.mineralPlanets = new LinkedList<>();
        this.homePlanetPositions = new LinkedList<>();
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
    
    public void addHomePlanetPosition(Vector position)
    {
        assert position != null;
        homePlanetPositions.add(position);
    }
    
    public Vector getHomePlanetPosition(int index)
    {
        assert index >= 0 && index < homePlanetPositions.size();
        return homePlanetPositions.get(index);
    }
    
    public void addStar(Star star)
    {
        assert star != null;
        stars.add(star);
    }
    
    public List<MineralPlanet> getMineralPlanets()
    {
        return mineralPlanets;
    }
    
    @Override
    public void render(Graphics2D g)
    {
        // render stars
        final float TRANSPARENCY = 0.4f;
        final AffineTransform transform = g.getTransform();
        final AffineTransform starTransform = AffineTransform.getTranslateInstance(0, 0);
        final Composite original = g.getComposite();
        g.setColor(Color.WHITE);
        g.setTransform(starTransform);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY));
        for (Star star : stars)
        {
            final int DEEP_DELTA = 2;
            final int DEEP_FACTOR = 1;
            final double FACTOR = 0.5;
            final int SIZE = (int) ((MapFactory.NUMBER_OF_LAYERS - star.getLayer()) * FACTOR);
            
            final Vector o = Screen.getInstance().getViewport().getOriginPosition();
            final int screenw = Screen.getInstance().getSize().width;
            final int screenh = Screen.getInstance().getSize().height;
            
            int x = (o.x / (1 + DEEP_DELTA + star.getLayer() * DEEP_FACTOR) + star.getPosititon().x - SIZE / 2) % screenw;
            int y = (o.y / (1 + DEEP_DELTA + star.getLayer() * DEEP_FACTOR) + star.getPosititon().y - SIZE / 2) % screenh;
            if (x < 0) x += screenw;
            if (y < 0) y += screenh;
            
            g.fillOval(x, y, SIZE, SIZE);
        }
        g.setComposite(original);
        g.setTransform(transform);
        
        // render mineral planets
        for (MineralPlanet planet : mineralPlanets)
        {
            planet.render(g);
        }
        
        if (SpaceWars.DEBUG)
        {
            g.setColor(Color.RED);
            
            // render map bounds
            g.drawRect(0, 0, width, height);
            
            // render home planet positions
            for (Vector position : homePlanetPositions)
            {
                final int radius = 39;
                g.drawOval(position.x - radius, position.y - radius, 2 * radius, 2 * radius);
            }
        }
    }
}
