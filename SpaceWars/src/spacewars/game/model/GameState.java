package spacewars.game.model;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import spacewars.game.model.buildings.Building;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public class GameState implements IRenderable, Serializable
{
   // TODO winkler: GameState minimieren
   
   private int                  duration;
   private Map                  map;
   private final List<Player>   players;
   private final List<Building> buildings;
   private final List<Ship>     ships;
   
   public GameState()
   {
      this.players = new LinkedList<>();
      this.buildings = new LinkedList<>();
      this.ships = new LinkedList<>();
   }
   
   public int getDuration()
   {
      return duration;
   }
   
   public void setDuration(int duration)
   {
      this.duration = duration;
   }
   
   public Map getMap()
   {
      return map;
   }
   
   public void setMap(Map map)
   {
      this.map = map;
   }
   
   public List<Building> getBuildings()
   {
      return buildings;
   }
   
   public List<Ship> getShips()
   {
      return ships;
   }
   
   public List<Player> getPlayers()
   {
      return players;
   }
   
   @Override
   public void render(Graphics2D g)
   {
      // render map
      map.render(g);
      
      // render connection lines
      for (Building building : buildings)
      {
         final Player player = building.getPlayer();
         g.setColor(player.getColor());
         
         for (Building linked : building.getLinks())
         {
            final Vector p1 = building.getPosition();
            final Vector p2 = linked.getPosition();
            final Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
            
            g.draw(line);
         }
      }
      
      /*
       * These are now rendered as buildings
      // render players
      for (Player player : players)
      {
         player.getHomePlanet().render(g);
      }
      */
      
      // render building elements
      for (Building element : buildings)
      {
         element.render(g);
      }
      
      // render ships
      for (Ship ship : ships)
      {
         ship.render(g);
         
         // render attack line
         final PlayerElement attackTarget = ship.getAttackTarget();
         if (attackTarget != null)
         {
            final int STROKE_WIDTH = 1;
            final float TRANSPARENCY = 0.5f;
            
            final Composite composite = g.getComposite();
            final Stroke stroke = g.getStroke();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY));
            g.setStroke(new BasicStroke(STROKE_WIDTH));
            g.setColor(ship.getPlayer().getColor());
            
            final Vector p1 = ship.getPosition();
            final Vector p2 = attackTarget.getPosition();
            final Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
            g.draw(line);
            
            g.setComposite(composite);
            g.setStroke(stroke);
         }
      }
   }
}
