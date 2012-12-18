package spacewars.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.io.Serializable;
import spacewars.game.Client;
import spacewars.game.Server;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public abstract class GameElement implements IRenderable, Serializable
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
    * The power
    */
   protected int          power;
   
   public GameElement(final Vector position, final int radius, final int sight)
   {
      this.position = position;
      this.radius = radius;
      this.sight = sight;
   }
   
   /**
    * Gets the game state from the client game instance.
    * 
    * @return game state
    */
   public GameState getClientGameState()
   {
      return Client.getInstance().getGameState();
   }
   
   /**
    * Gets the game state from the client game instance.
    * 
    * @return game state
    */
   public GameState getServerGameState()
   {
      return Server.getInstance().getGameState();
   }
   
   /**
    * Gets the position.
    * 
    * @return position vector
    */
   public Vector getPosition()
   {
      return position;
   }
   
   /**
    * Gets the size radius.
    * 
    * @return size radius in pixel
    */
   public int getSizeRadius()
   {
      return radius;
   }
   
   /**
    * Gets the view radius.
    * 
    * @return view radius in pixel
    */
   public int getViewRadius()
   {
      return sight;
   }
   
   /**
    * Checks, whether a point collides with this element.
    * 
    * @param point point to check
    * @return <code>true</code> if point collides with this element
    */
   public boolean collidesWith(Vector point)
   {
      final double dx = position.x - point.x;
      final double dy = position.y - point.y;
      final double dr = getSizeRadius();   
      return dx * dx + dy * dy < dr * dr;
   }
   
   /**
    * Checks, whether a line collides with this element.
    * 
    * @param line line to check
    * @return <code>true</code> if line collides with this element
    */
   public boolean collidesWith(Line2D line)
   {
      return line.ptSegDist(position.x, position.y) < radius;
   }
   
   /**
    * Checks, whether another element collides with this element.
    * 
    * @param element another element
    * @return <code>true</code> if element collides with this element
    */
   public boolean collidesWith(GameElement element)
   {
      final double dx = position.x - element.position.x;
      final double dy = position.y - element.position.y;
      final double dr = getSizeRadius() + element.getSizeRadius();      
      return dx * dx + dy * dy < dr * dr;
   }
   
   /**
    * Checks, whether another element is in the view radius of this element.
    * 
    * @param element another element
    * @return <code>true</code> if element is in the view radius of this element
    */
   public boolean isReachableFrom(GameElement element)
   {
      final double dx = position.x - element.position.x;
      final double dy = position.y - element.position.y;
      final double dr = element.getViewRadius();      
      return dx * dx + dy * dy < dr * dr;      
   }
   
   @Override
   public void render(Graphics2D g)
   {
      if (Client.isDebug())
      {
         g.setColor(Color.RED);
         g.drawOval(position.x - radius, position.y - radius, 2 * radius, 2 * radius);
      }
      
      // draw selection
      if (this.equals(Client.getInstance().getSelected()))
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
   
   @Override
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (obj instanceof GameElement)
      {
         GameElement gameElement = (GameElement) obj;
         if (this.hashCode() == gameElement.hashCode()) { return position.x == gameElement.position.x && position.y == gameElement.position.y; }
      }
      return false;
   }
   
   @Override
   public int hashCode()
   {
      return position.x ^ position.y;
   }
}
