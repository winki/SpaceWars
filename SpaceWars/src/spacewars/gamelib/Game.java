package spacewars.gamelib;

public abstract class Game implements Runnable, IRenderable
{
   private final GameTime gameTime;
   
   public Game()
   {
      this.gameTime = new GameTime();
      
      load();
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
         
         long past;
         long latest = System.nanoTime();
         long difference;
         
         while (!Thread.interrupted())
         {
            // handle game time
            past = latest;
            difference = GameTime.TARGET_CYCLE_TIME - (System.nanoTime() - past);
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
            
            process();
         }
      }
      catch (InterruptedException ex)
      {
         // interrupted while thread was sleeping, do nothing
      }
      finally
      {
         unload();
      }
   }
   
   /**
    * Gets the game time object.
    * 
    * @return game time object
    */
   public GameTime getGameTime()
   {
      return gameTime;
   }
   
   /**
    * Loads all content that has to be available before the game loop.
    */
   protected void load()
   {
      Screen.getInstance().register(this);
   }
   
   /**
    * Initializes everything that has to be available short before the game loop
    * starts.
    */
   protected void initialize()
   {}
   
   /**
    * Processes all that has to be repeated in the game loop.
    */
   protected void process()
   {
      // get user inputs
      Keyboard.captureState();
      Mouse.captureState();
      
      // update game state
      update(gameTime);
      
      // render graphics
      Screen.getInstance().render();
   }
   
   /**
    * Is called, when the game has to be updated.
    * 
    * @param gameTime elapsed game time
    */
   protected abstract void update(GameTime gameTime);
   
   /**
    * Loads all content.
    */
   protected void unload()
   {}
}
