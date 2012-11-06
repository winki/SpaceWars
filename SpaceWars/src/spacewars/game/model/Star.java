package spacewars.game.model;

import java.awt.Point;

public class Star
{   
    private Point posititon;
    private int layer;
    
    public Star(int x, int y, int layer)
    {
        this.posititon = new Point(x, y);
        this.layer = layer;
    }
    
    public Point getPosititon()
    {
        return posititon;
    }
    
    public int getLayer()
    {
        return layer;
    }
}
