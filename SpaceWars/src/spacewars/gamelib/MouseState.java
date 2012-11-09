package spacewars.gamelib;

import java.awt.Button;

public class MouseState
{
    protected boolean[] buttonStates;
    protected boolean[] lastButtonStates;
    
    protected boolean   cursorInScreen;
    protected int       wheelRotation;
    protected int       x;
    protected int       y;
    protected int       deltaX;
    protected int       deltaY;
    
    protected MouseState()
    {
        this.buttonStates = new boolean[4];
        this.lastButtonStates = new boolean[4];
    }
    
    protected MouseState(MouseState buffer, MouseState lastState)
    {
        this();
        
        System.arraycopy(buffer.buttonStates, 0, this.buttonStates, 0, this.buttonStates.length);
        System.arraycopy(lastState.buttonStates, 0, this.lastButtonStates, 0, this.lastButtonStates.length);        
        this.cursorInScreen = buffer.cursorInScreen;
        this.wheelRotation = buffer.wheelRotation;
        this.x = buffer.x;
        this.y = buffer.y;
        this.deltaX = buffer.x - lastState.x;
        this.deltaY = buffer.y - lastState.y;
    }
    
    public boolean isCursorInScreen()
    {
        return cursorInScreen;
    }
    
    public boolean isButtonDown(Button button)
    {
        return buttonStates[button.buttonCode()];
    }
    
    public boolean isButtonUp(Button button)
    {
        return !buttonStates[button.buttonCode()];
    }
    
    public boolean isButtonPressed(Button button)
    {
        return !lastButtonStates[button.buttonCode()] && buttonStates[button.buttonCode()];
    }
    
    public boolean isButtonReleased(Button button)
    {
        return lastButtonStates[button.buttonCode()] && !buttonStates[button.buttonCode()];
    }
    
    public boolean isButtonDragged(Button button)
    {
        return lastButtonStates[button.buttonCode()] && buttonStates[button.buttonCode()];
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
    
    public int getDeltaX()
    {
        return deltaX;
    }
    
    public int getDeltaY()
    {
        return deltaY;
    }

    protected void clean()
    {
        wheelRotation = 0;        
    }
}
