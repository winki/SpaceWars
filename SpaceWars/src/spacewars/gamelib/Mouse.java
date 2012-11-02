package spacewars.gamelib;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class Mouse
{
    private static MouseState stateBuffer = new MouseState();
    private static MouseState frozenState;
    private static MouseAdapter listener = new MouseAdapter()
    {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e)
        {
            stateBuffer.buttonStates = new boolean[4];
            stateBuffer.wheelRotation = e.getWheelRotation();
        }
        
        @Override
        public void mouseDragged(MouseEvent e)
        {
            stateBuffer.x = e.getX();
            stateBuffer.y = e.getY();
        }
        
        @Override
        public void mouseMoved(MouseEvent e)
        {
            stateBuffer.x = e.getX();
            stateBuffer.y = e.getY();
        }
        
        @Override
        public void mouseClicked(MouseEvent e)
        {}
        
        @Override
        public void mousePressed(MouseEvent e)
        {
            stateBuffer.buttonStates[e.getButton()] = true;
            stateBuffer.modifiers = e.getModifiers();
        }
        
        @Override
        public void mouseReleased(MouseEvent e)
        {
            stateBuffer.buttonStates[e.getButton()] = false;
            stateBuffer.modifiers = e.getModifiers();
        }
        
        @Override
        public void mouseEntered(MouseEvent e)
        {
            stateBuffer.isInScreen = true;
        }
        
        @Override
        public void mouseExited(MouseEvent e)
        {
            stateBuffer.isInScreen = false;
        }
    };
    
    static void captureState()
    {
        frozenState = new MouseState(stateBuffer);
    }

    static MouseAdapter getListener()
    {
        return listener;
    }
    
    public static MouseState getState()
    {
        return frozenState;
    }
}
