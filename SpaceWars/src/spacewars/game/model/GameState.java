package spacewars.game.model;

import java.util.LinkedList;
import java.util.List;

public class GameState
{   
    private Map map;
    private final List<GameElement> gameElements;
    private final List<Player> players;
    
    public GameState()
    {
        this.gameElements = new LinkedList<>();
        this.players = new LinkedList<>();
    }
    
    public Map getMap()
    {
        return map;
    }
    
    public void setMap(Map map)
    {
        this.map = map;
    }
    
    public List<GameElement> getGameElements()
    {
        return gameElements;
    }
    
    public List<Player> getPlayers()
    {
        return players;
    }
}
