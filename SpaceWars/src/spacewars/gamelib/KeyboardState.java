package spacewars.gamelib;

import java.util.HashSet;
import java.util.Set;

public class KeyboardState
{    
    protected final Set<Integer> downKeys;
    protected final Set<Integer> lastDownKeys;
    
    protected KeyboardState()
    {
        this.downKeys = new HashSet<>(10);
        this.lastDownKeys = new HashSet<>(10);
    }
    
    protected KeyboardState(KeyboardState buffer, KeyboardState lastState)
    {
        this();
        
        this.downKeys.addAll(buffer.downKeys);
        this.lastDownKeys.addAll(lastState.downKeys);
    }
    
    public boolean isKeyDown(Key key)
    {
        return downKeys.contains(key.keyCode());
    }
    
    public boolean isKeyUp(Key key)
    {
        return !downKeys.contains(key.keyCode());
    }
    
    public boolean isKeyPressed(Key key)
    {
        return !lastDownKeys.contains(key.keyCode()) && downKeys.contains(key.keyCode());
    }
    
    public boolean isKeyReleased(Key key)
    {
        return lastDownKeys.contains(key.keyCode()) && !downKeys.contains(key.keyCode());
    }
}
