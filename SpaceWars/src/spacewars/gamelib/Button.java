package spacewars.gamelib;

public enum Button
{   
    LEFT(1),
    MIDDLE(2),
    RIGHT(3);
    
    private int buttonCode;
    
    private Button(int buttonCode)
    {
        this.buttonCode = buttonCode;
    }
    
    public int buttonCode()
    {
        return buttonCode;
    }
}
