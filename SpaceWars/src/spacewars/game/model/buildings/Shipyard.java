package spacewars.game.model.buildings;

import spacewars.game.ClientGame;
import spacewars.game.model.Player;
import spacewars.game.model.Ship;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class Shipyard extends Building
{
   private static final String NAME = "Shipyard";
   
   public Shipyard(final Player player, final Vector position)
   {
      super(player, position, 15, 200, 500);
   }
   
   @Override
   public String getName()
   {
      return NAME;
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      // TODO: update super class?
      // super.update(gameTime);
      
      if (hasEnergy())
      {         
         // TODO: take nanoseconds instead of ticks
         if (gameTime.getTicks() % 120 == 0)
         {
            final Ship ship = new Ship(player, new Vector(position), 0);
            ClientGame.getInstance().getGameState().getShips().add(ship);
         }
      }
   }
}
