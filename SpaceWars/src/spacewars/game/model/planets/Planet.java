package spacewars.game.model.planets;

import spacewars.game.model.GameElement;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public abstract class Planet extends GameElement
{
   public Planet(final Vector position, final int radius, final int sight)
   {
      super(position, radius, sight);
   }
}
