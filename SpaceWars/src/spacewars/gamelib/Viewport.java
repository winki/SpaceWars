package spacewars.gamelib;

import java.awt.Rectangle;

public class Viewport
{
    public final Rectangle viewport;
    
    public Viewport(int width, int height)
    {
        this.viewport = new Rectangle(0, 0, width, height);
    }
    
    public Rectangle getViewport()
    {
        return viewport;
    }
    
    public void setPosition(int x, int y)
    {
        viewport.x = x;
        viewport.y = y;
    }
    
    public void setDimension(int width, int height)
    {
        viewport.width = width;
        viewport.height = height;
    }
}
