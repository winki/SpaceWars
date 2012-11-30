package spacewars.gamelib;

public abstract class Game implements Runnable, IUpdateable, IRenderable
{
   private final GameTime gameTime;
   private boolean        running;
   
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
         
         running = true;
         while (running && !Thread.interrupted())
         {
            // handle game time
            {
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
            }
            
            process();
         }
      }
      catch (InterruptedException ex)
      {
         // do nothing
      }
      finally
      {
         unload();
      }
   }
   
   /**
    * Stops the game loop.
    */
   public void stop()
   {
      running = false;
   }
   
   /**
    * Loads all content that has to be available before the game loop.
    */
   protected void load()
   {
      Screen.getInstance().register(this);
   }
   
   /**
    * Initializes everything that has to be available in the game loop.
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
    * Loads all content.
    */
   protected void unload()
   {}
   
   /**
    * Gets the game time object.
    * 
    * @return game time object
    */
   protected GameTime getGameTime()
   {
      return gameTime;
   }
}
