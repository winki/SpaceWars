package spacewars.game.model.planets;

import spacewars.game.model.GameElement;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public abstract class Planet extends GameElement
{
   /**
    * The size radius.
    */
   private short size;
   
   public Planet(final Vector position, final int size)
   {
      super(position);
      
      this.size = (short) size;
   }
   
   @Override
   public int getSizeRadius()
   {
      return size;
   }
   
   @Override
   public int getViewRadius()
   {
      // planet don't have a view radius
      return 0;
   }
}
