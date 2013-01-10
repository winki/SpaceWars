package spacewars.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public abstract class PlayerElement extends GameElement implements Serializable
{
   /**
    * The player that owns this element
    */
   protected Player player;
   /**
    * The actual health
    */
   protected int    health;
   
   public PlayerElement(final Vector position, final Player player)
   {
      super(position);
      
      this.player = player;
      this.health = getMaxHealth();
   }
   
   /**
    * Gets the owner of this element.
    * 
    * @return the player that owns this element
    */
   public Player getPlayer()
   {
      return player;
   }
   
   /**
    * Sets the owner of this element new (server function).
    * 
    * @param player
    */
   public void setPlayer(Player player)
   {
      this.player = player;
   }
   
   /**
    * Gets the enemy of this player
    * 
    * @return enemy
    */
   public Player getEnemy()
   {
      for (Player player : getServerGameState().getPlayers())
      {
         // TODO: don't take first enemy but nearest enemy?
         if (player.isEnemy(this.getPlayer())) { return player; }
      }
      return null;
   }
   
   /**
    * Gets the current health.
    * 
    * @return current health
    */
   public int getHealth()
   {
      return health;
   }
   
   /**
    * Gets the maximum health.
    * 
    * @return maximum health
    */
   public abstract int getMaxHealth();
   
   /**
    * Attacks this game element and take the specified number of health points.
    * 
    * @param healthPoints health points to be taken
    */
   public void attack(int healthPoints)
   {
      if (healthPoints > health) health = 0;
      else health -= healthPoints;
   }
   
   /**
    * Checks, whether a game element is dead.
    * 
    * @return <code>true</code> if element is dead
    */
   public boolean isDead()
   {
      return health <= 0;
   }
   
   @Override
   public void render(Graphics2D g)
   {
      final int radius = getSizeRadius();
      
      super.render(g);
      
      // health
      if (renderHealth())
      {
         final int WIDTH = 20;
         final int HEIGHT = 2;
         final int DY = 5;
         
         g.setColor(new Color(0, 90, 0));
         g.fillRect(position.x - WIDTH / 2, position.y - radius - DY - HEIGHT, WIDTH, HEIGHT);
         g.setColor(new Color(181, 230, 29));
         g.fillRect(position.x - WIDTH / 2, position.y - radius - DY - HEIGHT, (int) ((double) WIDTH * health / getMaxHealth()), HEIGHT);
      }
   }
   
   /**
    * Checks wheter the health bar should be rendered or not. Can be overwritten
    * by sub class.
    * 
    * @return <code>true</code> if health bar should be rendered
    */
   protected boolean renderHealth()
   {
      return true;
   }
   
   @Override
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (obj instanceof PlayerElement)
      {
         PlayerElement element = (PlayerElement) obj;
         if (this.hashCode() == element.hashCode()) { return position.x == element.position.x && position.y == element.position.y && player.id == element.player.id; }
      }
      return false;
   }
   
   @Override
   public int hashCode()
   {
      return position.x ^ position.y ^ player.id;
   }
}
