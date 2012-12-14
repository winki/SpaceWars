package spacewars.network;

import java.io.Serializable;
import spacewars.game.model.Player;

@SuppressWarnings("serial")
public class Guest implements Serializable
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
