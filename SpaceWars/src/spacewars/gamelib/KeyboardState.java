package spacewars.gamelib;

import java.util.HashSet;
import java.util.Set;

public class KeyboardState
{
    protected final Set<Integer> pressedKeys;
    
    protected KeyboardState()
    {
        this.pressedKeys = new HashSet<>(10);
    }
    
    protected KeyboardState(KeyboardState source)
    {
        this();
        
        this.pressedKeys.addAll(source.pressedKeys);
    }
    
    public boolean isKeyDown(Key key)
    {
        return pressedKeys.contains(key.keyCode());
    }
    
    public boolean isKeyUp(Key key)
    {
        return !pressedKeys.contains(key.keyCode());
    }
}
