package spacewars.game.model.buildings;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import spacewars.game.SpaceWarsGame;
import spacewars.game.model.GameState;
import spacewars.game.model.Player;
import spacewars.game.model.Ship;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public class LaserCanon extends Building
{
   protected static final String name        = "Laser";
   protected static final int[]  laserRanges = new int[] { 150, 250, 400, 500, 600 };
   protected static final int[]  laserPowers = new int[] { 3, 4, 5, 6, 7 };
   
   public LaserCanon(final Vector position, final Player player)
   {
      super(position, 10, 100, player, 300);
   }
   
   @Override
   public String getName()
   {
      return name;
   }
   
   public int getLaserRange()
   {
      return laserRanges[level];
   }
   
   public int getLaserPower()
   {
      return laserPowers[level];
   }
   
   public boolean canLaser(Ship ship)
   {
      return position.distance(ship.getPosition()) < getLaserRange();
   }
   
   @Override
   public void upgrade()
   {
      super.upgrade();
      this.power += 10;
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      if (hasEnergy())
      {
         final GameState gameState = SpaceWarsGame.getInstance().getGameState();
         for (Ship ship : gameState.getShips())
         {
            // only attack enemy ships that are in range
            if (this.canLaser(ship) && ship.getPlayer() == getEnemy())
            {
               // attack ship
               ship.attack(getLaserPower());
            }
         }
      }
   }
   
   @Override
   protected void renderBuilding(Graphics2D g)
   {
      if (!isPlaced())
      {
         // draw laser range
         g.setColor(isPlaceable() ? Color.WHITE : Color.RED);
         g.drawOval(position.x - getLaserRange(), position.y - getLaserRange(), 2 * getLaserRange(), 2 * getLaserRange());
      }
      
      super.renderBuilding(g);
      
      // draw icon
      final int BORDER = 3;
      g.setColor(Color.BLACK);
      g.fill(new Polygon(new int[] { position.x, position.x + radius - BORDER, position.x, position.x - radius + BORDER }, new int[] { position.y - radius + BORDER, position.y, position.y + radius - BORDER, position.y }, 4));
   }   
}
