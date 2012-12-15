package spacewars.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import spacewars.game.Client;
import spacewars.game.model.planets.MineralPlanet;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.Vector;

@SuppressWarnings("serial")
public class Map implements IRenderable, Serializable
{
   private String              name;
   private int                 width;
   private int                 height;
   private int                 numStars;
   private int                 numLayers;
   private List<MineralPlanet> mineralPlanets;
   private List<Vector>        homePlanetPositions;
   
   public Map(final String name, final int width, final int height, final int numStars, final int numLayers)
   {
      this.name = name;
      this.width = width;
      this.height = height;
      this.numStars = numStars;
      this.numLayers = numLayers;
      this.mineralPlanets = new LinkedList<>();
      this.homePlanetPositions = new LinkedList<>();
   }
   
   public String getName()
   {
      return name;
   }
   
   public int getWidth()
   {
      return width;
   }
   
   public int getHeight()
   {
      return height;
   }
   
   public int getNumStars()
   {
      return numStars;
   }
   
   public int getNumLayers()
   {
      return numLayers;
   }
   
   public List<MineralPlanet> getMineralPlanets()
   {
      return mineralPlanets;
   }
   
   public List<Vector> getHomePlanetPositions()
   {
      return homePlanetPositions;
   }
   
   @Override
   public void render(Graphics2D g)
   {
      // render mineral planets
      for (MineralPlanet planet : mineralPlanets)
      {
         planet.render(g);
      }
      
      if (Client.isDebug())
      {
         g.setColor(Color.RED);
         
         // render map bounds
         g.drawRect(0, 0, width, height);
         
         // render home planet positions
         for (Vector position : homePlanetPositions)
         {
            final int radius = 39;
            g.drawOval(position.x - radius, position.y - radius, 2 * radius, 2 * radius);
         }
      }
   }
}
