package spacewars.game.model.buildings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import spacewars.game.Client;
import spacewars.game.model.Player;
import spacewars.game.model.planets.MineralPlanet;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.Vector;
import spacewars.util.Config;

@SuppressWarnings("serial")
public class Mine extends Building
{
   protected static final int[]  mineralsPerMin = { 60, 70, 80, 90, 100 };
   /**
    * List of mineral planets that are reachable from this mine
    */
   protected List<MineralPlanet> reachableMineralPlanets;
   /**
    * The mineral planet which this mine is mining in this moment
    */
   protected MineralPlanet       miningTarget;
   
   public Mine(final Vector position, final Player player)
   {
      super(position, 10, 100, player);
      this.reachableMineralPlanets = new LinkedList<>();
   }
   
   @Override
   protected String getConfigName()
   {
      return Mine.class.getSimpleName();
   }
   
   public int getResPerMin()
   {
      return mineralsPerMin[level];
   }
   
   public int getEnergyConsumPerMin()
   {
      int[] energyConsum = Config.getIntArrayValue("buildings/Mine/energyConsum");
      return energyConsum[level];
   }
   
   /**
    * Gets the range in which the mine can collect minerals.
    */
   public int getMineRange()
   {
      return sight / 2;
   }
   
   public int getMineAmount()
   {
      // TODO: depends on level
      return 1;
   }
   
   public List<MineralPlanet> getReachableMineralPlanets()
   {
      return reachableMineralPlanets;
   }
   
   public MineralPlanet getMiningTarget()
   {
      return miningTarget;
   }
   
   private MineralPlanet chooseMiningTarget()
   {
      Collections.shuffle(reachableMineralPlanets);
      for (MineralPlanet planet : reachableMineralPlanets)
      {
         // take first random planet which has mineral reserves
         if (planet.getMineralReserves() > 0) { return planet; }
      }
      return null;
   }
   
   public boolean canMine(MineralPlanet planet)
   {
      return position.distance(planet.getPosition()) - planet.getSizeRadius() < getMineRange();
   }
   
   public void mine()
   {
      // mine minerals if there is energy
      if (isBuilt() && hasEnergy())
      {
         miningTarget = chooseMiningTarget();
         
         // mine if there's a planet with minerals
         if (miningTarget != null)
         {
            miningTarget.mine(getMineAmount());
         }
      }
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      miningTarget = null;
   }
   
   @Override
   protected void renderBuilding(Graphics2D g)
   {
      if (Client.isDebug())
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
      
      if (miningTarget != null)
      {
         final int STROKE_WIDTH = 2;
         final Stroke stroke = g.getStroke();
         g.setStroke(new BasicStroke(STROKE_WIDTH));
         g.setColor(Color.GREEN);
         
         final Vector p1 = getPosition();
         final Vector p2 = miningTarget.getPosition();
         final Line2D line = new Line2D.Float(p1.x, p1.y, p2.x, p2.y);
         g.draw(line);
         
         g.setStroke(stroke);
      }
      
      if (!isPlaced())
      {
         // draw mine range
         g.setColor(isPlaceable() ? Color.WHITE : Color.RED);
         g.drawOval(position.x - getMineRange(), position.y - getMineRange(), 2 * getMineRange(), 2 * getMineRange());
      }
      
      super.renderBuilding(g);
      
      if (isBuilt() || !isPlaced())
      {
         // draw icon
         final int BORDER = 4;
         g.setColor(Color.GREEN);
         g.fillOval(position.x - radius + BORDER, position.y - radius + BORDER, 2 * (radius - BORDER), 2 * (radius - BORDER));
      }
   }
}
