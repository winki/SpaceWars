package spacewars.gamelib;

public abstract class Game implements Runnable, IUpdateable, IRenderable
{
    private static final int  TARGET_FRAMERATE  = 60;
    private static final long TARGET_CYCLE_TIME = 1000000000 / TARGET_FRAMERATE;
    
    private final GameTime    gameTime;
    private boolean           running;
    
    public Game()
    {
        this.gameTime = new GameTime();
        this.running = true;
        
        Screen.getInstance().register(this);
    }
    
    /**
     * Starts the game loop.
     */
    @Override
    public final void run()
    {
        try
        {
            initialize();
            initializeScreen(Screen.getInstance());
            
            long past;
            long latest = System.nanoTime();
            long difference;
            
            while (running && !Thread.interrupted())
            {
                // handle game time
                {
                    past = latest;
                    difference = TARGET_CYCLE_TIME - (System.nanoTime() - past);
                    if (difference > 0)
                    {
                        // wait if update performs too fast
                        Thread.sleep((difference + 500000) / 1000000);
                        
                        gameTime.setRunningSlowly(false);
                    }
                    else
                    {
                        gameTime.setRunningSlowly(true);
                    }
                    latest = System.nanoTime();
                    gameTime.setElapsedGameTime(latest - past);
                }
                
                // get user inputs
                Keyboard.captureState();
                Mouse.captureState();
                
                // update game state
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
    
    /**
     * Stops the game loop.
     */
    public void stop()
    {
        running = false;
    }
    
    protected void initialize()
    {}
    
    protected abstract void initializeScreen(Screen screen);
    
    protected void terminate()
    {}
}
