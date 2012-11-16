package spacewars.gamelib;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import spacewars.gamelib.geometrics.Vector;

public class Viewport
{
    private final Rectangle       viewport;
    
    public Viewport()
    {
        this.viewport = new Rectangle();
    }
    
    /**
     * Gets the position of the top left point of the viewport as a
     * <code>Vector</code> object.
     * 
     * @return origin position
     */
    public Vector getOriginPosition()
    {
        return new Vector(viewport.x, viewport.y);
    }
    
    public void setOriginPosition(int x, int y)
    {
        viewport.setLocation(x, y);
    }
    
    /**
     * Gets the position of the central point of the viewport as a
     * <code>Vector</code> object.
     * 
     * @return central position
     */
    public Vector getCentralPosition()
    {
        return new Vector(viewport.x + viewport.width / 2, viewport.y + viewport.height / 2);
    }
    
    public void setCentralPosition(int x, int y)
    {
        viewport.setLocation(viewport.width / 2 - x, viewport.height / 2 - y);
    }
    
    public Dimension getSize()
    {
        return viewport.getSize();
    }
    
    public void setSize(int width, int height)
    {
        viewport.setSize(new Dimension(width, height));
    }    
   
    public void move(int dx, int dy)
    {
        viewport.x += dx;
        viewport.y += dy;
    }
    
    /**
     * Gets the geometric transform object to get screen coordinates from user
     * space.
     * 
     * @return transform object
     */
    public AffineTransform getWorldToScreenTransform()
    {
        return AffineTransform.getTranslateInstance(viewport.x, viewport.y);
    }
    
    /**
     * Gets the geometric transform object to get world coordinates from screen
     * space.
     * 
     * @return transform object
     */
    public AffineTransform getScreenToWorldTransform()
    {
        return AffineTransform.getTranslateInstance(-viewport.x, -viewport.y);
    }
    
    public Vector transformScreenToWorld(Vector vector)
    {
        Point2D screen = new Point2D.Double(vector.x, vector.y);
        Point2D world = new Point2D.Double();   

        getScreenToWorldTransform().transform(screen, world);
        return new Vector((int) world.getX(), (int) world.getY());
    }    
}
