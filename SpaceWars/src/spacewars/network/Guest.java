package spacewars.network;

import java.awt.Color;
import java.io.Serializable;
import spacewars.game.model.Player;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public class Guest implements Serializable
{
   private static int    counter;
   
   private int           id;
   private final IClient callback;
   private final Player  player;
   
   public Guest(final IClient callback, final Color color, final Vector homePosition)
   {
      this.id = counter++;
      this.callback = callback;
      this.player = new Player(id, color, homePosition);
   }
   
   public int getId()
   {
      return id;
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
