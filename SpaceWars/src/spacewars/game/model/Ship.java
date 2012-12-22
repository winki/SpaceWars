package spacewars.game.model;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import spacewars.game.model.buildings.Building;
import spacewars.game.model.buildings.Shipyard;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.IUpdateable;
import spacewars.gamelib.Vector;
import spacewars.util.Config;

@SuppressWarnings("serial")
public class Ship extends PlayerElement implements IUpdateable
{
   /**
    * Don't serialize the exact position, it's just for the server to calculate
    */
   protected transient double x;
   protected transient double y;
   protected transient double speed;
   protected PlayerElement    attackTarget;
   
   protected double           angle;
   
   public Ship(final Player player, final Vector position, final double angle)
   {
      super(position, player, 100);
      
      this.x = position.x;
      this.y = position.y;
      this.speed = 50;
      setDirectionToEnemy();
   }
   
   @Override
   public int getSizeRadius()
   {
      return Config.getInt("buildings/" + Shipyard.class.getSimpleName() + "/Ship/size");
   }
   
   @Override
   public int getViewRadius()
   {
      return Config.getInt("buildings/" + Shipyard.class.getSimpleName() + "/Ship/view");
   }
   
   public int getAttackPower()
   {
      // TODO: depends on level
      return Config.getInt("buildings/" + Shipyard.class.getSimpleName() + "/Ship/attackPower");
   }
   
   public int getAttackFrequency()
   {
      // TODO: depends on level
      return Config.getInt("buildings/" + Shipyard.class.getSimpleName() + "/Ship/attackFrequency");
   }
   
   public PlayerElement getAttackTarget()
   {
      return attackTarget;
   }
   
   private void setDirectionToEnemy()
   {
      // direction: enemy's home planet
      final Vector target = getEnemy().getHomePlanet().getPosition();
      final Vector direction = target.sub(position);
      
      // set angle
      angle = Math.atan2(direction.y, direction.x);
   }
   
   private PlayerElement chooseAttackTarget()
   {
      // attack buildings
      for (Building building : getServerGameState().getBuildings())
      {
         if (building.getPlayer().isEnemy(this.getPlayer()) && building.isReachableFrom(this)) { return building; }
      }
      
      // no target
      return null;
   }
   
   private PlayerElement chooseFlightTarget()
   {
      Building flightTarget = null;
      for (Building b : getServerGameState().getBuildings())
      {
         if (flightTarget == null || (b.getPlayer().isEnemy(this.getPlayer()) && b.getPosition().distance(this.getPosition()) < flightTarget.getPosition().distance(this.getPosition())))
         {
            flightTarget = b;
         }
      }
      return flightTarget;
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      attackTarget = chooseAttackTarget();
      
      if (attackTarget != null)
      {
         // attack in specified frequency
         if (gameTime.timesPerSecond(getAttackFrequency(), getPosition().x))
         {
            attackTarget.attack(getAttackPower());
         }
         else
         {
            attackTarget = null;
         }
      }
      else
      {
         // move only if not attacking
         final PlayerElement flightTarget = chooseFlightTarget();
         if (flightTarget != null)
         {
            // direction of nearest enemy building
            final Vector dir = flightTarget.getPosition().sub(position);
            
            /*
            // follow the mouse:
            final Vector mp =
            Screen.getInstance().getViewport().transformScreenToWorld(Mouse.getState().getVector());
            final Vector dir = mp.sub(position);
            */
            
            if (dir.x != 0)
            {
               // flight in the direction of the vector
               double targetangle = Math.atan((double) dir.y / (double) dir.x);
               if (dir.x * dir.y < 0) targetangle += Math.PI;
               if (dir.y < 0) targetangle += Math.PI;
               
               // angle difference
               double anglediff = targetangle - angle;
               while (anglediff < 0)
                  anglediff += (2 * Math.PI);
               
               // turn
               final int TURN_SLOWMO = 10;
               if (anglediff > Math.PI)
               {
                  // turn left
                  angle -= (2 * Math.PI - anglediff) / TURN_SLOWMO;
               }
               else
               {
                  // turn right
                  angle += anglediff / TURN_SLOWMO;
               }
            }
         }
         
         x += speed * Math.cos(angle) * gameTime.getElapsedGameTime() / 1000000000;
         y += speed * Math.sin(angle) * gameTime.getElapsedGameTime() / 1000000000;
         position.x = (int) x;
         position.y = (int) y;
      }
   }
   
   @Override
   public void render(Graphics2D g)
   {
      final AffineTransform viewport = g.getTransform();
      
      Shape ship = new Polygon(new int[] { position.x + 8, position.x - 4, position.x - 4 }, new int[] { position.y, position.y + 4, position.y - 4 }, 3);
      g.rotate(angle, position.x, position.y);
      g.setColor(player.getColor());
      g.fill(ship);
      
      g.setTransform(viewport);
      
      super.render(g);
   }
}
