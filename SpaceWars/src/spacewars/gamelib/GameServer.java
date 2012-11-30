package spacewars.gamelib;

import java.awt.Graphics2D;

public abstract class GameServer extends Game
{
   @Override
   protected void load()
   {
      // don't register screen on server
   }
   
   @Override
   protected void process()
   {
      // update game state
      update(getGameTime());
      
      // sync
      sync();
   }
   
   @Override
   public final void render(Graphics2D g)
   {
      // no rendering on server
   }   
   
   abstract protected void sync();
}
