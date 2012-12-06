package spacewars.game;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import spacewars.game.model.GameElement;
import spacewars.game.model.GameState;
import spacewars.game.model.Link;
import spacewars.game.model.Map;
import spacewars.game.model.Player;
import spacewars.game.model.Ship;
import spacewars.game.model.Star;
import spacewars.game.model.buildings.Building;
import spacewars.game.model.buildings.BuildingType;
import spacewars.game.model.buildings.HomeBase;
import spacewars.game.model.buildings.LaserCanon;
import spacewars.game.model.buildings.Mine;
import spacewars.game.model.buildings.Relay;
import spacewars.game.model.buildings.Shipyard;
import spacewars.game.model.buildings.SolarStation;
import spacewars.game.model.planets.MineralPlanet;
import spacewars.gamelib.Button;
import spacewars.gamelib.Game;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.Key;
import spacewars.gamelib.Keyboard;
import spacewars.gamelib.Mouse;
import spacewars.gamelib.Screen;
import spacewars.gamelib.Vector;

public class SpaceWarsGame extends Game
{
   public static final boolean             DEBUG             = false;
   public static final boolean             CAN_CHANGE_PLAYER = true;
   /**
    * Game instance
    */
   private static SpaceWarsGame            instance;
   /**
    * Random object
    */
   private final Random                    random;
   /**
    * The stars in the background
    */
   private final List<Star>                stars;
   /**
    * The player
    */
   private Player                          player;
   /**
    * The game state
    */
   private GameState                       gameState;
   /**
    * Game element that is currently selected by players mouse
    */
   private GameElement                     selected;
   /**
    * The type of the building that will be built
    */
   private BuildingType                    buildingType;
   /**
    * Building object that will be built
    */
   private Building                        buildingToBePlaced;
   /**
    * Can the building object <code>toBuild</code> really be built? Or can't it
    * because of collision
    */
   private boolean                         buildingIsPlaceable;
   /**
    * The links of the building that will be built
    */
   private final List<Link<Building>>      linksToBuildings;
   /**
    * The links of a mine to reachable mineral planets
    */
   private final List<Link<MineralPlanet>> linksToMineralPlanets;
   /**
    * Current scroll position
    */
   private Vector                          scrollPosition;
   
   private SpaceWarsGame()
   {
      this.random = new Random();
      this.buildingType = BuildingType.NOTHING;
      this.scrollPosition = new Vector();
      this.stars = new LinkedList<Star>();
      this.linksToBuildings = new LinkedList<>();
      this.linksToMineralPlanets = new LinkedList<>();
   }
   
   public static SpaceWarsGame getInstance()
   {
      if (instance == null)
      {
         instance = new SpaceWarsGame();
      }
      return instance;
   }

   public GameState getGameState()
   {
      return gameState;
   }
   
   /**
    * Finds the connected buildings to the <code>buildingToBePlaced</code> and
    * computes the lines between.
    */
   private void computeLinksToBuildings()
   {
      linksToBuildings.clear();
      linksToMineralPlanets.clear();
      
      // treat home planet as a building/solar station
      Building home = (SolarStation) player.getHomePlanet();
      if (home.isReachableFrom(buildingToBePlaced))
      {
         final Vector p1 = buildingToBePlaced.getPosition();
         final Vector p2 = home.getPosition();
         final Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
         final boolean collision = checkCollision(line, home);
         
         linksToBuildings.add(new Link<>(home, line, collision));
      }
      
      // create links to other buildings that are reachable
      for (Building building : gameState.getBuildings())
      {
         if (building.isReachableFrom(buildingToBePlaced))
         {
            final Vector p1 = buildingToBePlaced.getPosition();
            final Vector p2 = building.getPosition();
            final Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
            final boolean collision = checkCollision(line, building);
            
            linksToBuildings.add(new Link<>(building, line, collision));
         }
      }
      
      // sort links by by lenght
      Collections.sort(linksToBuildings, new Comparator<Link<Building>>()
      {
         @Override
         public int compare(Link<Building> l1, Link<Building> l2)
         {
            final double distance1 = l1.getLinkedElement().getPosition().distance(buildingToBePlaced.getPosition());
            final double distance2 = l2.getLinkedElement().getPosition().distance(buildingToBePlaced.getPosition());
            if (distance1 > distance2) return 1;
            if (distance1 < distance2) return -1;
            return 0;
         }
      });
      
      // filter links
      if (buildingToBePlaced instanceof Mine)
      {
         // compute links to reachable mineral planets
         computeLinksToMineralPlanets();
         
         // remove too long links
         boolean removeRest = false;
         for (Iterator<Link<Building>> iterator = linksToBuildings.iterator(); iterator.hasNext();)
         {
            final Link<Building> link = iterator.next();
            final Building building = link.getLinkedElement();
            
            // only treat the players building
            if (!removeRest && building.getPlayer() == player)
            {
               // take every link to relays, solar stations or buildings
               // with no links
               if (building instanceof Relay || building instanceof SolarStation)
               {
                  removeRest = !link.isCollision();
                  continue;
               }
            }
            
            iterator.remove();
         }
      }
      else if (buildingToBePlaced instanceof Relay || buildingToBePlaced instanceof SolarStation)
      {
         for (Iterator<Link<Building>> iterator = linksToBuildings.iterator(); iterator.hasNext();)
         {
            final Building building = iterator.next().getLinkedElement();
            
            // only treat the players building
            if (building.getPlayer() == player)
            {
               // take every link to relays, solar stations or buildings
               // with no links
               if (building instanceof Relay) continue;
               if (building instanceof SolarStation) continue;
               if (building.getLinks().isEmpty()) continue;
            }
            
            iterator.remove();
         }
      }
      else if (buildingToBePlaced instanceof LaserCanon || buildingToBePlaced instanceof Shipyard)
      {
         // remove too long links
         boolean removeRest = false;
         for (Iterator<Link<Building>> iterator = linksToBuildings.iterator(); iterator.hasNext();)
         {
            final Link<Building> link = iterator.next();
            final Building building = link.getLinkedElement();
            
            // only treat the players building
            if (!removeRest && building.getPlayer() == player)
            {
               // take every link to relays, solar stations or buildings
               // with no links
               if (building instanceof Relay || building instanceof SolarStation)
               {
                  removeRest = !link.isCollision();
                  continue;
               }
            }
            
            iterator.remove();
         }
      }
   }
   
   private void computeLinksToMineralPlanets()
   {
      final Mine mine = (Mine) buildingToBePlaced;
      
      // create links to mineral planets that are reachable
      for (MineralPlanet planet : gameState.getMap().getMineralPlanets())
      {
         if (mine.canMine(planet))
         {
            final Vector p1 = buildingToBePlaced.getPosition();
            final Vector p2 = planet.getPosition();
            final Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
            final boolean collision = checkCollision(line, planet);
            
            linksToMineralPlanets.add(new Link<>(planet, line, collision));
         }
      }
   }
   
   /**
    * Checks whether a specified element collides with another game element.
    * 
    * @param element element that should be tested on collisions
    * @return <code>true</code> if there was a collision
    */
   private boolean checkCollision(GameElement element)
   {
      // check collision with mineral planets
      for (MineralPlanet m : gameState.getMap().getMineralPlanets())
      {
         if (element.collidesWith(m)) { return true; }
      }
      
      // check collision with home planets
      for (Player p : gameState.getPlayers())
      {
         if (element.collidesWith(p.getHomePlanet())) { return true; }
      }
      
      // check collision with buildings
      for (Building b : gameState.getBuildings())
      {
         if (element.collidesWith(b)) { return true; }
         
         // check collision with links
         for (GameElement linked : b.getLinks())
         {
            final Vector p1 = b.getPosition();
            final Vector p2 = linked.getPosition();
            final Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
            
            if (element.collidesWith(line)) { return true; }
         }
      }
      
      // no collision
      return false;
   }
   
   /**
    * Checks whether a specified line collides with another game element. The
    * game element the line is connected with is an exception.
    * 
    * @param line the line
    * @param reachableElement the game element the line is connected with
    * @return <code>true</code> if there was a collision
    */
   private boolean checkCollision(Line2D line, GameElement reachableElement)
   {
      // check collision with buildings
      for (Building b : gameState.getBuildings())
      {
         if (b != reachableElement && b.collidesWith(line)) { return true; }
      }
      
      // check collision with planets
      for (MineralPlanet m : gameState.getMap().getMineralPlanets())
      {
         if (m.collidesWith(line)) { return true; }
      }
      
      // check collision with home planets
      for (Player p : gameState.getPlayers())
      {
         // TODO: add home planet to buildings?
         final HomeBase planet = p.getHomePlanet();
         if (planet != reachableElement && planet.collidesWith(line)) { return true; }
      }
      
      // no collision
      return false;
   }
   
   /**
    * Check for every building if it is on the energy net
    */
   private void checkEnergyAvailability()
   {
      final List<SolarStation> solars = new LinkedList<>();
      
      // reset checked for energy flag
      for (Building building : getGameState().getBuildings())
      {
         building.setCheckedForEngery(false);
         if (!(building instanceof SolarStation))
         {
            building.setHasEnergy(false);
         }
         
         // collect solars
         if (building instanceof SolarStation)
         {
            final SolarStation solar = (SolarStation) building;
            solars.add(solar);
         }
      }
      
      // add home planet as energy source
      // TODO: add home planet to buildings?
      final HomeBase home = player.getHomePlanet();
      home.setCheckedForEngery(false);
      solars.add(home);
      
      // checks recursively
      for (SolarStation solar : solars)
      {
         checkEnergyAvailability(solar);
      }
   }
   
   /**
    * Checks recursively if a building is somehow connected to a solar station.
    * 
    * @param check the building to check
    */
   private void checkEnergyAvailability(Building check)
   {
      if (!check.isCheckedForEngery())
      {
         check.setCheckedForEngery(true);
         for (Building linked : check.getLinks())
         {
            linked.setHasEnergy(true);
            checkEnergyAvailability(linked);
         }
      }
   }

   @Override
   public void update(GameTime gameTime)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void render(Graphics2D g)
   {
      // TODO Auto-generated method stub
      
   }
}
