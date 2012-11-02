package spacewars.gamelib;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard
{
    private static KeyboardState stateBuffer = new KeyboardState();
    private static KeyboardState frozenState;
    private static KeyListener listener = new KeyListener()
    {
        @Override
        public void keyTyped(KeyEvent e)
        {}
        
        @Override
        public synchronized void keyPressed(KeyEvent e)
        {
            stateBuffer.addKeyCode(e.getKeyCode());
        }
        
        @Override
        public synchronized void keyReleased(KeyEvent e)
        {
            stateBuffer.removeKeyCode(e.getKeyCode());
        }
    };
    
    static void captureState()
    {
        frozenState = new KeyboardState(stateBuffer);
    }

    static KeyListener getListener()
    {
        return listener;
    }
    
    public static KeyboardState getState()
    {
        return frozenState;
    }
}
