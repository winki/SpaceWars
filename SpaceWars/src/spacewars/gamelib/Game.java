package spacewars.gamelib;

public abstract class Game implements Runnable, IUpdateable, IRenderable
{
    private static final int  TARGET_FRAMERATE  = 60;
    private static final long TARGET_CYCLE_TIME = 1000000000 / TARGET_FRAMERATE;
   
    private final GameTime    gameTime;
    
    public Game()
    {
        this.gameTime = new GameTime();
        
        Screen.getInstance().setOwner(this);
    }
    
    public final void run()
    {
        try
        {
            initialize();
            initScreen(Screen.getInstance());            
            
            long past;
            long latest = System.nanoTime();
            long difference;
            
            while (!Thread.interrupted())
            {
                // hold framerate constant
                past = latest;
                difference = TARGET_CYCLE_TIME - (System.nanoTime() - past);
                if (difference > 0) Thread.sleep(difference / 1000000, (int) (difference % 1000000));
                latest = System.nanoTime();
                gameTime.setElapsedTime(latest - past);
                
                // update game state
                Keyboard.captureState();
                Mouse.captureState();
                update(gameTime);
                
                // render graphics
                Screen.getInstance().render(gameTime.getFrameRate());
            }
            
            terminate();
        }
        catch (InterruptedException ex)
        {
            // do nothing
        }
    }
    
    protected void initialize()
    {}
    
    protected abstract void initScreen(Screen screen); 
    
    protected void terminate()
    {}
}
