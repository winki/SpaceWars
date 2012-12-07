package spacewars.game.model;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import spacewars.game.model.buildings.Building;
import spacewars.game.model.buildings.HomeBase;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.IUpdateable;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public class Ship extends PlayerElement implements IUpdateable
{
   /**
    * Don't serialize the exact position, it's just for the server to calculate
    */
   protected volatile double x;
   protected volatile double y;
   protected double          speed;
   protected double          angle;
   
   protected PlayerElement   attackTarget;
   
   public Ship(final Player player, final Vector position, final double angle)
   {
      super(position, 5, 100, player, 100);
      
      this.x = position.x;
      this.y = position.y;
      this.speed = 50;
      setDirectionToEnemy();
   }
   
   public int getAttackPower()
   {
      // TODO: depends on level
      return 1;
   }
   
   public PlayerElement getAttackTarget()
   {
      return attackTarget;
   }
   
   private void setDirectionToEnemy()
   {
      // direction: enemy's home planet
      final HomeBase target = getEnemy().getHomePlanet();
      final Vector direction = target.getPosition().sub(position);
      
      // set angle
      angle = Math.atan2(direction.y, direction.x);
   }
   
   private PlayerElement chooseAttackTarget()
   {
      final Player enemy = getEnemy();
      final GameState gameState = getGameState();
      
      // attack buildings
      for (Building building : gameState.getBuildings())
      {
         if (building.isReachableFrom(this) && building.getPlayer() == enemy) { return building; }
      }
      
      // attack enemys home planet
      final HomeBase planet = enemy.getHomePlanet();
      if (planet.isReachableFrom(this)) { return planet; }
      
      // no target
      return null;
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      attackTarget = chooseAttackTarget();
      if (attackTarget != null)
      {
         attackTarget.attack(getAttackPower());
      }
      
      // move only if not attacking
      if (attackTarget == null)
      {
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
      g.rotate(angle, x, y);
      g.setColor(player.getColor());
      g.fill(ship);
      
      g.setTransform(viewport);
      
      super.render(g);
   }
}
