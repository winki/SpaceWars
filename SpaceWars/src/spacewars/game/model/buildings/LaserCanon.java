package spacewars.game.model.buildings;

import spacewars.game.model.Player;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class LaserCanon extends Building
{
   private static final String NAME = "Laser";
   
   public int                  power;
   public int                  health;
   
   public LaserCanon(final Player player, final Vector position)
   {
      super(player, position, 15, 200, 300);
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
         
      }
   }
   
   @Override
   public void upgrade()
   {
      super.upgrade();
      this.power += 10;
   }
}
