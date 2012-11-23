package spacewars.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.io.Serializable;
import spacewars.game.ClientGame;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class GameElement implements IRenderable, Serializable
{
   /**
    * The position
    */
   protected final Vector position;
   /**
    * The size radius
    */
   protected int          radius;
   /**
    * The sight radius
    */
   protected int          sight;
   /**
    * The health
    */
   protected int          health;
   /**
    * The power
    */
   protected int          power;
   
   /*
    * TODO: kai
    * - Leben
    */
   
   public GameElement(final Vector position, final int radius, final int sight)
   {
      this.position = position;
      this.radius = radius;
      this.sight = sight;
   }
   
   public Vector getPosition()
   {
      return position;
   }
   
   public int getSizeRadius()
   {
      return radius;
   }
   
   public int getViewRadius()
   {
      return sight;
   }
   
   public int getHealth()
   {
      return health;
   }
   
   public boolean isHit(Vector other)
   {
      return position.distance(other) < radius;
   }
   
   public boolean isReachableFrom(GameElement element)
   {
      return position.distance(element.getPosition()) < element.getViewRadius();
   }
   
   public boolean doesCollideWith(GameElement element)
   {
      return position.distance(element.getPosition()) < getSizeRadius() + element.getSizeRadius();
   }
   
   public boolean doesCollideWith(Line2D line)
   {
      return line.ptSegDist(position.x, position.y) < radius;
   }
   
   @Override
   public void render(Graphics2D g)
   {
      if (ClientGame.DEBUG)
      {
         g.setColor(Color.RED);
         g.drawOval(position.x - radius, position.y - radius, 2 * radius, 2 * radius);
      }
      
      // draw selection
      if (ClientGame.getInstance().getSelected() == this)
      {
         final int OVERLAY = 3;
         final int SERIF = 5;
         
         g.setColor(Color.WHITE);
         g.drawOval(position.x - radius - OVERLAY, position.y - radius - OVERLAY, 2 * (radius + OVERLAY), 2 * (radius + OVERLAY));
         g.drawLine(position.x, position.y - radius - OVERLAY - SERIF, position.x, position.y - radius - OVERLAY + SERIF);
         g.drawLine(position.x + radius + OVERLAY - SERIF, position.y, position.x + radius + OVERLAY + SERIF, position.y);
         g.drawLine(position.x, position.y + radius + OVERLAY - SERIF, position.x, position.y + radius + OVERLAY + SERIF);
         g.drawLine(position.x - radius - OVERLAY - SERIF, position.y, position.x - radius - OVERLAY + SERIF, position.y);
      }
   }
}
