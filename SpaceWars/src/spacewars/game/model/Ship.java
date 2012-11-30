package spacewars.game.model;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import spacewars.game.SpaceWarsGame;
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
   
   public Ship(final Player player, final Vector position, final double angle)
   {
      super(position, 5, 100, player, 100);
      
      this.x = position.x;
      this.y = position.y;
      this.speed = 50;
      setDirectionToEnemy();
   }
   
   private void setDirectionToEnemy()
   {
      // direction: enemy's home planet
      final HomeBase target = getEnemy().getHomePlanet();
      final Vector direction = target.getPosition().sub(position);
      
      // set angle
      angle = Math.atan2(direction.y, direction.x);
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      final Player enemy = getEnemy();
      final GameState gameState = SpaceWarsGame.getInstance().getGameState();
      boolean attack = false;
      final int ATTACK_POINTS = 1;
      
      // attack buildings
      for (Building b : gameState.getBuildings())
      {
         if (b.isReachableFrom(this) && b.getPlayer() == enemy)
         {
            attack = true;
            b.attack(ATTACK_POINTS);
         }
      }
      
      // attack enemys home planet
      final HomeBase planet = enemy.getHomePlanet();
      if (planet.isReachableFrom(this))
      {
         attack = true;         
         planet.attack(ATTACK_POINTS);
      }
      
      if (!attack)
      {
         // move only if not attacked
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
