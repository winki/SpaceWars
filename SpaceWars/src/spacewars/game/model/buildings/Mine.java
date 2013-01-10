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
      super(position, player);
      this.reachableMineralPlanets = new LinkedList<>();
   }
   
   @Override
   protected String getConfigName()
   {
      return Mine.class.getSimpleName();
   }
   
   public double getMiningFrequency()
   {
      return Config.getDouble("buildings/" + getConfigName() + "/miningFrequency");
   }
   
   public int getMiningAmount()
   {
      return (int) (getMiningRate() / 60.0 / getMiningFrequency());
   }
   
   /**
    * Gets the number of minerals that are mined per minute.
    * 
    * @return mined minerals per minute
    */
   public int getMiningRate()
   {
      return Config.getIntArray("buildings/" + getConfigName() + "/miningRate")[level];
   }
   
   public int getEnergyConsum()
   {
      return Config.getInt("buildings/" + getConfigName() + "/energyConsum");
   }
   
   /**
    * Gets the range in which the mine can collect minerals.
    */
   public int getMiningRange()
   {
      return getViewRadius() / 2;
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
         if (planet.getMinerals() > 0) { return planet; }
      }
      return null;
   }
   
   public boolean canMine(MineralPlanet planet)
   {
      return position.distance(planet.getPosition()) - planet.getSizeRadius() < getMiningRange();
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
            miningTarget.mine(getMiningAmount());
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
         g.drawOval(position.x - getMiningRange(), position.y - getMiningRange(), 2 * getMiningRange(), 2 * getMiningRange());
      }
      
      super.renderBuilding(g);
      
      final int radius = getSizeRadius();
      
      if (isBuilt() || !isPlaced())
      {
         // draw icon
         final int BORDER = 4;
         g.setColor(Color.GREEN);
         g.fillOval(position.x - radius + BORDER, position.y - radius + BORDER, 2 * (radius - BORDER), 2 * (radius - BORDER));
      }
   }
}
