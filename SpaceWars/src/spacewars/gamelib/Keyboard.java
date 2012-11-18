package spacewars.gamelib;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard
{
    private static KeyboardState     buffer    = new KeyboardState();
    private static KeyboardState     state     = new KeyboardState();
    private static KeyboardState     lastState = new KeyboardState();
    private static final KeyListener listener;
    
    static
    {
        listener = new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {}
            
            @Override
            public void keyPressed(KeyEvent e)
            {
                synchronized (buffer)
                {
                    buffer.downKeys.add(e.getKeyCode());
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e)
            {
                synchronized (buffer)
                {
                    buffer.downKeys.remove(e.getKeyCode());
                }
            }
        };
    }
    
    protected static void captureState()
    {
        synchronized (state)
        {
            // TODO: test synchronization side effects
            lastState = state;
            state = new KeyboardState(buffer, lastState);
        }
    }
    
    protected static KeyListener getListener()
    {
        return listener;
    }
    
    /**
     * Gets the current keyboard state.
     * 
     * @return the keyboard state
     */
    public static KeyboardState getState()
    {
        return state;
    }
}
