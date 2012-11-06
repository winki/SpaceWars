package spacewars.gamelib;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class Mouse
{
    private static MouseState         buffer   = new MouseState();
    private static MouseState         state    = new MouseState();
    private static final MouseAdapter listener = new MouseAdapter()
                                               {
                                                   @Override
                                                   public void mouseWheelMoved(MouseWheelEvent e)
                                                   {
                                                       synchronized (buffer)
                                                       {
                                                           buffer.wheelRotation = e.getWheelRotation();
                                                       }
                                                   }
                                                   
                                                   @Override
                                                   public void mouseDragged(MouseEvent e)
                                                   {
                                                       synchronized (buffer)
                                                       {
                                                           buffer.x = e.getX();
                                                           buffer.y = e.getY();
                                                       }
                                                   }
                                                   
                                                   @Override
                                                   public void mouseMoved(MouseEvent e)
                                                   {
                                                       synchronized (buffer)
                                                       {
                                                           buffer.x = e.getX();
                                                           buffer.y = e.getY();
                                                       }
                                                   }
                                                   
                                                   @Override
                                                   public void mouseClicked(MouseEvent e)
                                                   {}
                                                   
                                                   @Override
                                                   public void mousePressed(MouseEvent e)
                                                   {
                                                       synchronized (buffer)
                                                       {
                                                           buffer.buttonStates[e.getButton()] = true;
                                                       }
                                                   }
                                                   
                                                   @Override
                                                   public void mouseReleased(MouseEvent e)
                                                   {
                                                       synchronized (buffer)
                                                       {
                                                           buffer.buttonStates[e.getButton()] = false;
                                                       }
                                                   }
                                                   
                                                   @Override
                                                   public void mouseEntered(MouseEvent e)
                                                   {
                                                       synchronized (buffer)
                                                       {
                                                           buffer.cursorInScreen = true;
                                                       }
                                                   }
                                                   
                                                   @Override
                                                   public void mouseExited(MouseEvent e)
                                                   {
                                                       synchronized (buffer)
                                                       {
                                                           buffer.cursorInScreen = false;
                                                       }
                                                   }
                                               };
    
    protected static void captureState()
    {
        synchronized (state)
        {
            state = new MouseState(buffer);
            buffer.wheelRotation = 0;
        }
    }
    
    protected static MouseAdapter getListener()
    {
        return listener;
    }
    
    /**
     * Gets the current mouse state.
     * 
     * @return the mouse state
     */
    public static MouseState getState()
    {
        return state;
    }
}
