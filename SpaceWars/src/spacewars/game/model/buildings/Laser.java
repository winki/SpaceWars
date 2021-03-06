package spacewars.game.model.buildings;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import spacewars.game.model.Player;
import spacewars.game.model.PlayerElement;
import spacewars.game.model.Ship;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.Vector;
import spacewars.util.Config;

@SuppressWarnings("serial")
public class Laser extends Building
{
   /**
    * The current attacking element.
    */
   protected PlayerElement attackTarget;
   
   public Laser(final Vector position, final Player player)
   {
      super(position, player);
   }
   
   @Override
   protected String getConfigName()
   {
      return Laser.class.getSimpleName();
   }
   
   public int getLaserRange()
   {
      return Config.getIntArray("buildings/" + getConfigName() + "/laserRange")[level];
   }
   
   public int getLaserPower()
   {
      return Config.getIntArray("buildings/" + getConfigName() + "/laserPower")[level];
   }
   
   public int getLaserFrequency()
   {
      return Config.getIntArray("buildings/" + getConfigName() + "/laserFrequency")[level];
   }
   
   public PlayerElement getAttackTarget()
   {
      return attackTarget;
   }
   
   public boolean canLaser(Ship ship)
   {
      return position.distance(ship.getPosition()) < getLaserRange();
   }
   
   private PlayerElement chooseAttackTarget()
   {
      for (Ship ship : getServerGameState().getShips())
      {
         // only attack enemy ships that are in range
         if (ship.getPlayer().isEnemy(this.getPlayer()) && this.canLaser(ship)) { return ship; }
      }
      
      // no target
      return null;
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      if (hasEnergy())
      {
         attackTarget = chooseAttackTarget();
         if (attackTarget != null)
         {
            // attack in specified frequency
            if (gameTime.timesPerSecond(getLaserFrequency(), getPosition().x))
            {
               attackTarget.attack(getLaserPower());
            }
            else
            {
               attackTarget = null;
            }
         }
      }
   }
   
   @Override
   protected void renderBuilding(Graphics2D g)
   {
      final int radius = getSizeRadius();
      
      if (!isPlaced())
      {
         // draw laser range
         g.setColor(isPlaceable() ? Color.WHITE : Color.RED);
         g.drawOval(position.x - getLaserRange(), position.y - getLaserRange(), 2 * getLaserRange(), 2 * getLaserRange());
      }
      
      super.renderBuilding(g);
      
      if (isBuilt() || !isPlaced())
      {
         // draw icon
         final int BORDER = 3;
         g.setColor(Color.BLACK);
         g.fill(new Polygon(new int[] { position.x, position.x + radius - BORDER, position.x, position.x - radius + BORDER }, new int[] { position.y - radius + BORDER, position.y, position.y + radius - BORDER, position.y }, 4));
      }
      
      // draw attack line
      if (attackTarget != null)
      {
         final int STROKE_WIDTH = 2;
         final float TRANSPARENCY = 0.6f;
         
         final Composite composite = g.getComposite();
         final Stroke stroke = g.getStroke();
         g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY));
         g.setStroke(new BasicStroke(STROKE_WIDTH));
         g.setColor(getPlayer().getColor());
         
         final Vector p1 = getPosition();
         final Vector p2 = attackTarget.getPosition();
         final Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
         g.draw(line);
         
         g.setComposite(composite);
         g.setStroke(stroke);
      }
   }
}
