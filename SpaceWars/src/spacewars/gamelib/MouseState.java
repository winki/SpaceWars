package spacewars.gamelib;

import java.awt.event.MouseEvent;

public class MouseState
{
    protected boolean[] buttonStates;
    protected int       wheelRotation;
    protected int       x;
    protected int       y;
    protected int       modifiers;
    protected boolean   isInScreen;
    
    MouseState()
    {
        this.buttonStates = new boolean[4];
    }
    
    MouseState(MouseState source)
    {
        this();
        
        this.buttonStates[MouseEvent.BUTTON1] = source.buttonStates[MouseEvent.BUTTON1];
        this.buttonStates[MouseEvent.BUTTON2] = source.buttonStates[MouseEvent.BUTTON2];
        this.buttonStates[MouseEvent.BUTTON3] = source.buttonStates[MouseEvent.BUTTON3];
        this.wheelRotation = source.wheelRotation;
        this.x = source.x;
        this.y = source.y;
        this.modifiers = source.modifiers;
        this.isInScreen = source.isInScreen;        
    }
    
    public boolean isLeftButtonPressed()
    {
        return buttonStates[MouseEvent.BUTTON1];
    }
    
    public boolean isMiddleButtonPressed()
    {
        return buttonStates[MouseEvent.BUTTON2];
    }
    
    public boolean isRightButtonPressed()
    {
        return buttonStates[MouseEvent.BUTTON3];
    }
    
    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
}
