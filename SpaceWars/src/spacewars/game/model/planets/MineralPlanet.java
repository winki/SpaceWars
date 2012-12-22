package spacewars.game.model.planets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.Vector;
import spacewars.util.Config;

@SuppressWarnings("serial")
public class MineralPlanet extends Planet implements IRenderable
{
   /**
    * The current amount of minerals
    */
   protected short minerals;
   /**
    * The maximum mineral capacity
    */
   protected short mineralCapacity;
   
   public MineralPlanet(final Vector position, final int size, int minerals)
   {
      super(position, size);
      
      if (minerals > getMaximalMineralCapacity()) minerals = getMaximalMineralCapacity();
      this.minerals = (short) minerals;
      this.mineralCapacity = (short) minerals;
   }
   
   /**
    * Gets the current amount of minerals of this planet.
    * 
    * @return the current amount of minerals
    */
   public int getMinerals()
   {
      return minerals;
   }
   
   /**
    * Gets the maximum mineral capacity of this planet.
    * 
    * @return the maximum mineral capacity
    */
   public int getMineralCapacity()
   {
      return mineralCapacity;
   }
   
   /**
    * Gets the absolute mineral capacity maximum of a mineral planet.
    * 
    * @return the maximum mineral capacity
    */
   public static int getMaximalMineralCapacity()
   {
      return Config.getInt("planets/MineralPlanet/maxMineralCapacity");
   }
   
   /**
    * Mines a specified amount of minerals. If there aren't enought minerals
    * left, simply the rest will be mined.
    * 
    * @param minerals the amount of minerals to mine
    */
   public void mine(int minerals)
   {
      if (minerals > this.minerals) minerals = this.minerals;
      this.minerals -= minerals;
   }
   
   @Override
   public void render(Graphics2D g)
   {
      final int radius = getSizeRadius();
      
      // draw planet without minerals
      g.setColor(new Color(44, 40, 28));
      g.fillOval(position.x - radius, position.y - radius, 2 * radius, 2 * radius);
      
      // draw minerals
      final Random random = new Random(position.x ^ position.y);
      final int numOvals = 20;
      g.setColor(Color.GREEN);
      for (int i = 0; i < numOvals; i++)
      {
         final int rmax = random.nextInt(radius);
         final double a = random.nextDouble() * 2 * Math.PI;
         final double dist = random.nextDouble() * (radius - rmax);
         final int x = (int) (position.x + Math.cos(a) * dist);
         final int y = (int) (position.y + Math.sin(a) * dist);
         final int r = (int) (rmax * getMinerals() / (double) getMaximalMineralCapacity());
         
         g.fillOval(x - r, y - r, 2 * r, 2 * r);
      }
      
      super.render(g);
   }
}
