package spacewars.gamelib;

import java.awt.event.MouseEvent;

public class MouseState
{
    protected boolean   cursorInScreen;    
    protected boolean[] buttonStates;
    protected int       wheelRotation;
    protected int       x;
    protected int       y;
    
    protected MouseState()
    {
        this.buttonStates = new boolean[4];
    }
    
    protected MouseState(MouseState source)
    {
        this();

        this.cursorInScreen = source.cursorInScreen;
        this.buttonStates[MouseEvent.BUTTON1] = source.buttonStates[MouseEvent.BUTTON1];
        this.buttonStates[MouseEvent.BUTTON2] = source.buttonStates[MouseEvent.BUTTON2];
        this.buttonStates[MouseEvent.BUTTON3] = source.buttonStates[MouseEvent.BUTTON3];
        this.wheelRotation = source.wheelRotation;
        this.x = source.x;
        this.y = source.y;
    }
    
    public boolean isCursorInScreen()
    {
        return cursorInScreen;
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
    
    public int getWheelRotation()
    {
        return wheelRotation;
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
