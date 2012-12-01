package spacewars.gamelib;

public class GameTime
{
   public static final int  TARGET_FRAMERATE  = 25;
   public static final long TARGET_CYCLE_TIME = 1000000000 / TARGET_FRAMERATE;
   
   private long             ticks;
   private long             totalGameTime;
   private long             elapsedGameTime;
   private int              frameRate;
   private boolean          runningSlowly;
   
   protected GameTime()
   {}
   
   /**
    * Gets the number of game cycles since the start of the game.
    * 
    * @return number of game "ticks"
    */
   public long getTicks()
   {
      return ticks;
   }
   
   /**
    * The amount of game time since the start of the game.
    * 
    * @return total game time in nano seconds
    */
   public long getTotalGameTime()
   {
      return totalGameTime;
   }
   
   /**
    * The amount of elapsed game time since the last update.
    * 
    * @return elapsed game time in nano seconds
    */
   public long getElapsedGameTime()
   {
      return elapsedGameTime;
   }
   
   /**
    * Gets the frame rate how it is calculated with the elapsed game time. This
    * frame rate isn't the same as the effective rendering frequency.
    * 
    * TODO: Implement the effective rendering frequency.
    * 
    * @return the frame rate
    */
   public int getFrameRate()
   {
      return frameRate;
   }
   
   /**
    * Gets a value indicating that the game loop is taking longer than its
    * <code>TARGET_CYCLE_TIME</code>. In this case, the game loop can be
    * considered to be running too slowly and should do something to "catch up."
    * 
    * @return <code>true</code> if the game loop takes too long
    */
   public boolean isRunningSlowly()
   {
      return runningSlowly;
   }
   
   /**
    * Checks whether an action is in time. The parameter specifies a frequency
    * in which this function return <code>true</code>.
    * 
    * @param hertz the wished frequency
    * @return <code>true</code> if clock is in time
    */
   public boolean timesPerSecond(double hertz)
   {
      // TODO: maybe take nanosecons instead of ticks
      return ticks % (int) (TARGET_FRAMERATE / hertz) == 0;
   }
   
   protected void setElapsedGameTime(long elapsedGameTime)
   {
      this.ticks++;
      this.totalGameTime += elapsedGameTime;
      this.elapsedGameTime = elapsedGameTime;
      this.frameRate = (int) (1000000000 / elapsedGameTime);
   }
   
   protected boolean setRunningSlowly(boolean runningSlowly)
   {
      return this.runningSlowly = runningSlowly;
   }
}
