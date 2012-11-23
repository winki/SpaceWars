package spacewars.game.model.buildings;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.LinkedList;
import java.util.List;
import spacewars.game.ClientGame;
import spacewars.game.model.Player;
import spacewars.game.model.planets.MineralPlanet;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class Mine extends Building
{
   private static final String   NAME           = "Mine";
   /**
    * The range in which the mine can collect minerals
    */
   protected int                 mineRange      = sight / 2;
   protected int                 mineralsPerMin = 60;
   protected int                 energyConsum   = 6;
   /**
    * List of mineral planets that are reachable from this mine
    */
   protected List<MineralPlanet> reachableMineralPlanets;
   
   public Mine(final Player player, final Vector position)
   {
      super(player, position, 10, 100, 100);
      this.reachableMineralPlanets = new LinkedList<>();
   }
   
   @Override
   public String getName()
   {
      return NAME;
   }
   
   public int getResPerMin()
   {
      return super.level * mineralsPerMin;
   }
   
   public int getEnergyConsumPerMin()
   {
      return energyConsum;
   }
   
   public int getMineRange()
   {
      return mineRange;
   }
   
   public boolean canMine(MineralPlanet planet)
   {
      return position.distance(planet.getPosition()) - planet.getSizeRadius() < getMineRange();
   }
   
   public List<MineralPlanet> getReachableMineralPlanets()
   {
      return reachableMineralPlanets;
   }
   
   @Override
   public void render(Graphics2D g)
   {
      if (ClientGame.DEBUG)
      {
         g.setColor(Color.RED);
         for (MineralPlanet planet : reachableMineralPlanets)
         {
            final Vector p1 = getPosition();
            final Vector p2 = planet.getPosition();
            final Line2D line = new Line2D.Float(p1.x, p1.y, p2.x, p2.y);            
           
            g.draw(line);
         }
      }
      
      super.render(g);
   }
}
