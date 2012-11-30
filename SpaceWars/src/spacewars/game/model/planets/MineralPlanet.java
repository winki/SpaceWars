package spacewars.game.model.planets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public class MineralPlanet extends Planet implements IRenderable
{
   protected int mineralReserves;
   protected int mineralReservesMax;
   
   public MineralPlanet(final Vector position, final int size, int minerals)
   {
      super(position, size, 0);
      
      this.mineralReserves = minerals;
      this.mineralReservesMax = minerals;
   }
   
   public int getMineralReserves()
   {
      return mineralReserves;
   }
   
   public int getMineralReservesMax()
   {
      return mineralReservesMax;
   }
   
   /**
    * Mines a specified amount of minerals. If there aren't enought minerals
    * left, simply the rest will be mined.
    * 
    * @param minerals the amount of minerals to mine
    */
   public void mine(int minerals)
   {
      if (minerals > mineralReserves) minerals = mineralReserves;
      mineralReserves -= minerals;
   }
   
   @Override
   public void render(Graphics2D g)
   {
      // draw planet without minerals
      g.setColor(new Color(185, 122, 87));
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
         // TODO: regard biggest possible mineral reserves here (instead of 100)
         final int r = (int) (rmax * 0.01 * getMineralReserves());
         
         g.fillOval(x - r, y - r, 2 * r, 2 * r);
      }
      
      super.render(g);
   }
}
