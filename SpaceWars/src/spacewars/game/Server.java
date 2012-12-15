package spacewars.game;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import spacewars.game.model.GameElement;
import spacewars.game.model.GameState;
import spacewars.game.model.Link;
import spacewars.game.model.Map;
import spacewars.game.model.Player;
import spacewars.game.model.Ship;
import spacewars.game.model.buildings.Building;
import spacewars.game.model.buildings.LaserCanon;
import spacewars.game.model.buildings.Mine;
import spacewars.game.model.buildings.Relay;
import spacewars.game.model.buildings.Shipyard;
import spacewars.game.model.buildings.SolarStation;
import spacewars.game.model.planets.MineralPlanet;
import spacewars.gamelib.GameServer;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.Vector;
import spacewars.network.Guest;
import spacewars.network.IClient;
import spacewars.network.IServer;
import de.root1.simon.annotation.SimonRemote;

@SimonRemote(value = { IServer.class })
public class Server extends GameServer implements IServer
{
   private static int                      playerId;
   private static final int                dummyPlayers     = 1;
   private static final int                minPlayers       = 2;
   private static final Color[]            colors           = new Color[] { new Color(0, 0, 180), new Color(180, 0, 0) };
   private static int                      startingMinerals = 2000;
   
   /**
    * Server instance
    */
   private static Server                   instance;
   /**
    * All registred guests
    */
   private final List<Guest>               guests;
   /**
    * The game state
    */
   private final GameState                 gameState;
   /**
    * Starting time of the game
    */
   private long                            startingTime;
   /**
    * Is the game currently running
    */
   private boolean                         running;
   
   private final Queue<Building>           toBuild;
   private final Queue<Building>           toUpgrade;
   private final Queue<Building>           toRecycle;
   
   /**
    * The links of the building that will be built
    */
   private final List<Link<Building>>      linksToBuildings;
   /**
    * The links of a mine to reachable mineral planets
    */
   private final List<Link<MineralPlanet>> linksToMineralPlanets;
   
   private Server()
   {
      this.guests = new LinkedList<Guest>();
      this.linksToBuildings = new LinkedList<>();
      this.linksToMineralPlanets = new LinkedList<>();
      this.gameState = new GameState();
      this.toBuild = new LinkedList<>();
      this.toUpgrade = new LinkedList<>();
      this.toRecycle = new LinkedList<>();
   }
   
   public static Server getInstance()
   {
      if (instance == null)
      {
         instance = new Server();
      }
      return instance;
   }
   
   @Override
   public GameState getGameState()
   {
      return gameState;
   }
   
   @Override
   protected void initialize()
   {
      createMap();
      if (dummyPlayers > 0)
      {
         for (int i = 0; i < dummyPlayers; i++)
         {
            register(null);
         }
      }
   }
   
   private void createMap()
   {
      // create map
      final Map map = MapFactory.loadMap("map1.png");
      gameState.setMap(map);
   }
   
   private void reassignPlayer(Building building)
   {
      for (Player player : gameState.getPlayers())
      {
         if (player.equals(building.getPlayer()))
         {
            building.setPlayer(player);
            return;
         }
      }
      
      // no player object found
      Logger.getGlobal().log(Level.SEVERE, "No user object found that corresponds with the building.");
   }
   
   private Building getBuilding(Building building)
   {
      for (Building b : gameState.getBuildings())
      {
         if (b.equals(building)) { return b; }
      }
      
      // no player object found
      Logger.getGlobal().log(Level.SEVERE, "No user object found that corresponds with the building.");
      return null;
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      if (running)
      {
         // process all build, upgrade and recycle requests
         buildAll();
         upgradeAll();
         recycleAll();
         
         // update world (game state)
         updateWorld(gameTime);
      }
      else if (guests.size() == minPlayers)
      {
         // start game if minimal number of players are there
         startingTime = System.nanoTime();
         running = true;
      }
   }
   
   private void buildAll()
   {
      while (!toBuild.isEmpty())
      {
         final Building buildingToBuild = toBuild.poll();
         reassignPlayer(buildingToBuild);
         final Player player = buildingToBuild.getPlayer();
         final int costs = buildingToBuild.getCosts();
         
         if (player.getMinerals() < costs)
         {
            Logger.getGlobal().info("Not enough minerals to build building.");
            continue;
         }
         player.removeMinerals(costs);
         
         // check collisions
         boolean buildingIsPlaceable = !checkCollision(buildingToBuild);
         buildingToBuild.setPlaceable(buildingIsPlaceable);
         
         if (buildingIsPlaceable)
         {
            // calculate connections
            computeLinksToBuildings(buildingToBuild);
            
            // can build mines only if there is minimum one mineral planet
            // reachable
            if (buildingToBuild instanceof Mine && linksToMineralPlanets.isEmpty())
            {
               buildingIsPlaceable = false;
               buildingToBuild.setPlaceable(buildingIsPlaceable);
               return;
            }
            
            // place this building
            buildingToBuild.place();
            gameState.getBuildings().add(buildingToBuild);
            
            // add links
            for (Link<Building> link : linksToBuildings)
            {
               if (!link.isCollision())
               {
                  final Building building = link.getLinkedElement();
                  buildingToBuild.getLinks().add(building);
                  building.getLinks().add(buildingToBuild);
               }
            }
            
            // add reachable mineral planets to mine
            if (buildingToBuild instanceof Mine)
            {
               Mine mine = (Mine) buildingToBuild;
               for (Link<MineralPlanet> link : linksToMineralPlanets)
               {
                  mine.getReachableMineralPlanets().add(link.getLinkedElement());
               }
            }
         }
      }
   }
   
   private void upgradeAll()
   {
      while (!toUpgrade.isEmpty())
      {
         final Building buildingToUpgrade = getBuilding(toUpgrade.poll());
         final Player player = buildingToUpgrade.getPlayer();
         final int costs = buildingToUpgrade.getCosts();
         
         if (player.getMinerals() < costs)
         {
            Logger.getGlobal().info("Not enough minerals to upgrade building.");
            continue;
         }
         player.removeMinerals(costs);
         
         buildingToUpgrade.upgrade();
      }
   }
   
   private void recycleAll()
   {
      while (!toRecycle.isEmpty())
      {
         final Building buildingToRecycle = getBuilding(toRecycle.poll());
         final Player player = buildingToRecycle.getPlayer();
         
         player.addMinerals(buildingToRecycle.getRecycleReward());
         
         for (Building linked : buildingToRecycle.getLinks())
         {
            linked.getLinks().remove(buildingToRecycle);
         }
         getGameState().getBuildings().remove(buildingToRecycle);
      }
   }
   
   private void updateWorld(GameTime gameTime)
   {
      // check which buildings are on the energy net
      checkEnergyAvailability();
      
      // update energy and mineral flow flow
      updateEnergyAndMineralFlow();
      
      establishBuildings();
      
      // war: defend, attack
      for (Iterator<Building> iterator = getGameState().getBuildings().iterator(); iterator.hasNext();)
      {
         Building building = (Building) iterator.next();
         
         if (building instanceof LaserCanon || building instanceof Shipyard)
         {
            // defend, attack
            building.update(gameTime);
         }
         
         if (building.isDead())
         {
            // TODO: implement method for removing building clean
            
            // remove all links
            for (Iterator<Building> iteratorLinked = building.getLinks().iterator(); iteratorLinked.hasNext();)
            {
               Building linked = (Building) iteratorLinked.next();
               linked.getLinks().remove(building);
            }
            
            // remove from list
            iterator.remove();
         }
      }
      
      // update ships
      for (Iterator<Ship> iterator = getGameState().getShips().iterator(); iterator.hasNext();)
      {
         Ship ship = (Ship) iterator.next();
         ship.update(gameTime); // move
         
         if (ship.isDead())
         {
            // TODO: implement method for removing ship clean
            
            // remove remove from list
            iterator.remove();
         }
      }
      
      // update game time
      int seconds = (int) ((System.nanoTime() - startingTime) / 1000000000);
      gameState.setDuration(seconds);
   }
   
   /**
    * Finds the connected buildings to the <code>buildingToBePlaced</code> and
    * computes the lines between.
    */
   private void computeLinksToBuildings(final Building buildingToBePlaced)
   {
      linksToBuildings.clear();
      linksToMineralPlanets.clear();
      
      // get player object
      // final Player player = getPlayerOfBuilding(buildingToBePlaced)// TODO
      final Player player = buildingToBePlaced.getPlayer();
      
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
         computeLinksToMineralPlanets(buildingToBePlaced);
         
         // remove too long links
         boolean removeRest = false;
         for (Iterator<Link<Building>> iterator = linksToBuildings.iterator(); iterator.hasNext();)
         {
            final Link<Building> link = iterator.next();
            final Building building = link.getLinkedElement();
            
            // only treat the players building
            if (!removeRest && building.getPlayer().equals(player))
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
            if (building.getPlayer().equals(player))
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
            if (!removeRest && building.getPlayer().equals(player))
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
   
   private void computeLinksToMineralPlanets(Building buildingToBePlaced)
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
         if (!b.equals(reachableElement) && b.collidesWith(line)) { return true; }
      }
      
      // check collision with planets
      for (MineralPlanet m : gameState.getMap().getMineralPlanets())
      {
         if (m.collidesWith(line)) { return true; }
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
      
      /*
      // add home planet as energy source
      for (Player player : gameState.getPlayers())
      {
         final SolarStation home = player.getHomePlanet();
         home.setCheckedForEngery(false);
         solars.add(home);
      }
      */
      
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
         if (check.isBuilt())
         {
            for (Building linked : check.getLinks())
            {
               linked.setHasEnergy(true);
               checkEnergyAvailability(linked);
            }
         }
      }
   }
   
   private void updateEnergyAndMineralFlow()
   {
      /*
       * TODO: winki
       * - Korrekter Energiefluss
       */
      
      // produce energy
      for (Building building : getGameState().getBuildings())
      {
         if (building instanceof SolarStation)
         {
            final SolarStation solar = (SolarStation) building;
            solar.update(getGameTime());
            
            if (getGameTime().timesPerSecond(1) && solar.isBuilt())
            {
               // produce energy once a second
               
               // TODO
               solar.getPlayer().addEnergy(1);
            }
         }
      }
      
      // mine minerals
      for (Building building : getGameState().getBuildings())
      {
         if (building instanceof Mine)
         {
            final Mine mine = (Mine) building;
            mine.update(getGameTime());
            
            if (getGameTime().timesPerSecond(1))
            {
               final Player player = mine.getPlayer();
               if (player.getEnergy() >= mine.getEnergyConsumPerMin())
               {
                  mine.mine();
                  final int amount = mine.getMineAmount();
                  mine.getPlayer().addMinerals(amount);
                  player.removeEnergy(mine.getEnergyConsumPerMin());
               }
            }
         }
      }
   }
   
   /**
    * Establish every building by 10 percent 3 times a second.
    */
   private void establishBuildings()
   {
      if (getGameTime().timesPerSecond(3))
      {
         for (Building building : getGameState().getBuildings())
         {
            if (!building.isBuilt() && building.hasEnergy())
            {
               building.establishBy(10);
            }
         }
      }
   }
   
   @Override
   public int register(IClient client)
   {
      // can only register if game is not running
      if (!running)
      {
         final int id = playerId++;
         final Color color = colors[guests.size()];
         final Vector position = gameState.getMap().getHomePlanetPositions().get(guests.size());
         
         final Player player = new Player(id, color, position, startingMinerals);
         final Guest guest = new Guest(client, player);
         
         gameState.getPlayers().add(player);
         gameState.getBuildings().add(player.getHomePlanet());
         guests.add(guest);
         
         return id;
      }
      
      return -1;
   }
   
   @Override
   public void build(Building building)
   {
      toBuild.offer(building);
   }
   
   @Override
   public void upgrade(Building building)
   {
      toUpgrade.offer(building);
   }
   
   @Override
   public void recycle(Building building)
   {
      toRecycle.offer(building);
   }
}
