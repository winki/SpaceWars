package spacewars.gamelib;

import java.util.HashSet;
import java.util.Set;

public class KeyboardState
{
    private final Set<Integer> pressedKeys;
    
    KeyboardState()
    {
        this.pressedKeys = new HashSet<>(10);        
    }
    
    KeyboardState(KeyboardState source)
    {
        this();
        
        this.pressedKeys.addAll(source.pressedKeys);
    }
    
    void addKeyCode(int keyCode)
    {
        pressedKeys.add(keyCode);
    }
    
    void removeKeyCode(int keyCode)
    {
        pressedKeys.remove(keyCode);
    }
    
    public boolean isKeyDown(Key key)
    {
        return pressedKeys.contains(key.keyCode());
    }
    
    public boolean isKeyUp(Key key)
    {
        return !pressedKeys.contains(key.keyCode());
    }
    
    public Key[] getKeys()
    {
        return pressedKeys.toArray(new Key[pressedKeys.size()]);
    }
}
