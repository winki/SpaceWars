package spacewars.game.model;

import java.util.ArrayList;

public class GameState
{   
    private Map map;
    private ArrayList<Object> gameObjects = new ArrayList<>();
    
    public Map getMap()
    {
        return map;
    }
    
    public void setMap(Map map)
    {
        this.map = map;
    }
    
    public void addObject(Object object){
    	this.gameObjects.add(object);
    }
    
    public int getLength(){
    	return this.gameObjects.size();
    }
    
    
}
