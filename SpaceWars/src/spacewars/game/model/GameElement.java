package spacewars.game.model;

import spacewars.gamelib.geometrics.Vector;

public class GameElement
{   
    protected Vector position;
    protected int sizeRadius;
    protected int viewRadius;

	//coordinates
	public int x;
	public int y;
	
	//higher the level better the building
	public int level;
    
    public GameElement(Vector position, int sizeRadius, int viewRadius)
    {
        this.position = position;
        this.sizeRadius = sizeRadius;
        this.viewRadius = viewRadius;
    }
    
    public Vector getPosition()
    {
        return position;
    }
    
    public void setPosition(Vector position)
    {
        this.position = position;
    }
    
    public int getSizeRadius()
    {
        return sizeRadius;
    }
    
    public int getViewRadius()
    {
        return viewRadius;
    }
    
    public boolean isHit(Vector other)
    {
        return position.distance(other) <= sizeRadius;
    }
}
