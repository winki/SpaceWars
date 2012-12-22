package spacewars.network;

import spacewars.game.model.Player;

public class Guest
{
   private final IClient callback;
   private final Player  player;
   
   public Guest(final IClient callback, final Player player)
   {
      this.callback = callback;
      this.player = player;
   }
   
   public IClient getCallback()
   {
      return callback;
   }
   
   public Player getPlayer()
   {
      return player;
   }
}
