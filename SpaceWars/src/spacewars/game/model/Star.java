package spacewars.game.model;

import java.awt.Point;
import java.io.Serializable;

public class Star implements Serializable
{
    /**
     * Id for serialization
     */
    private static final long serialVersionUID = 1L;
    
    private Point             posititon;
    private int               layer;
    
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
