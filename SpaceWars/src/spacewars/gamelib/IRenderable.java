package spacewars.gamelib;

import java.awt.Graphics2D;

public interface IRenderable
{
    /**
     * Is called when the game element has to be rendered.
     * 
     * @param g graphics object
     */
    void render(Graphics2D g);
}
