package spacewars.gamelib;

public abstract class GameClient extends Game
{
   @Override
   protected void process()
   { 
      // get user inputs
      Keyboard.captureState();
      Mouse.captureState();
      
      // update game state
      update(getGameTime());
      
      // render graphics
      Screen.getInstance().render();
      
      // sync
      sync();
   }
  
   abstract protected void sync();
}
