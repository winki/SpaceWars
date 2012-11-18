package spacewars.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import spacewars.game.model.buildings.Building;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class GameState implements IRenderable, Serializable
{
    private Map                  map;
    private final List<Player>   players;
    private final List<Building> buildings;
    private final List<Ship>     ships;
    
    public GameState(Map map)
    {
        this.map = map;
        this.players = new LinkedList<>();
        this.buildings = new LinkedList<>();
        this.ships = new LinkedList<>();        
    }
    
    public GameState()
    {
        this.players = new LinkedList<>();
        this.buildings = new LinkedList<>();
        this.ships = new LinkedList<>();        
    }
    
    public Map getMap()
    {
        return map;
    }
    
    public List<Building> getBuildings()
    {
        return buildings;
    }

    public List<Ship> getShips()
    {
        return ships;
    }
    
    public List<Player> getPlayers()
    {
        return players;
    }
    
    @Override
    public void render(Graphics2D g)
    {
        // render map
        map.render(g);
        
        // render connection lines
        g.setColor(Color.MAGENTA);
        for (Building building : buildings)
        {
            for (GameElement linked : building.getLinks())
            {
                final Vector p1 = building.getPosition();
                final Vector p2 = linked.getPosition();
                final Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
                
                g.draw(line);
            }            
        }
        
        // render players
        for (Player player : players)
        {
            player.getHomePlanet().render(g);
        }
        
        // render building elements
        for (Building element : buildings)
        {
            element.render(g);
        }
        
        // render ships
        for (Ship ship : ships)
        {
            ship.render(g);
        }
    }
}
