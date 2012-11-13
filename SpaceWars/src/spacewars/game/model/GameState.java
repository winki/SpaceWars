package spacewars.game.model;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import spacewars.game.model.buildings.Building;
import spacewars.gamelib.IRenderable;

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
        
        // render players
        for (Player player : players)
        {
            player.getHomePlanet().render(g);
        }
        
        // render building elements
        for (GameElement element : buildings)
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
