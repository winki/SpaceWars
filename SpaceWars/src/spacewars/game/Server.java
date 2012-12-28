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
import spacewars.game.model.buildings.Laser;
import spacewars.game.model.buildings.Mine;
import spacewars.game.model.buildings.Relay;
import spacewars.game.model.buildings.Shipyard;
import spacewars.game.model.buildings.Solar;
import spacewars.game.model.planets.MineralPlanet;
import spacewars.gamelib.GameServer;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.Vector;
import spacewars.network.Guest;
import spacewars.network.IClient;
import spacewars.network.IServer;
import spacewars.util.Config;
import spacewars.util.Helpers;
import de.root1.simon.annotation.SimonRemote;

@SimonRemote(value = { IServer.class })
public class Server extends GameServer implements IServer
{
   private static int                      playerId   = 0;
   private static final int                minPlayers = Config.getInt("server/minPlayers");
   private static final Color[]            colors     = new Color[] { Config.getColor("players/color1"), Config.getColor("players/color2"), Config.getColor("players/color3"), Config.getColor("players/color4") };
   
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
   private GameState                       gameState;
   /**
    * The buffered copy of the game state TODO: built in correct
    */
   private GameState                       gameStateBuffer;
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
      if (running) return gameStateBuffer;
      return null;
   }
      
   public GameState getUnbufferedGameState()
   {
      return gameState;
   }
   
   @Override
   protected void initialize()
   {
      createMap();
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
      Logger.getGlobal().log(Level.SEVERE, "No building found that corresponds with the building.");
      return null;
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      if (running)
      {
         // buffer game state
         bufferGameState();
         
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
         final Building building = toBuild.poll();
         reassignPlayer(building);
         final Player player = building.getPlayer();
         final int costs = building.getCosts();
         
         if (player.getMinerals() < costs)
         {
            Logger.getGlobal().info("Not enough minerals to build building.");
            continue;
         }
         player.removeMinerals(costs);
         
         // check collisions
         boolean buildingIsPlaceable = !checkCollision(building);
         building.setPlaceable(buildingIsPlaceable);
         
         if (buildingIsPlaceable)
         {
            // calculate connections
            computeLinksToBuildings(building);
            
            // can build mines only if there is minimum one mineral planet
            // reachable
            if (building instanceof Mine && linksToMineralPlanets.isEmpty())
            {
               buildingIsPlaceable = false;
               building.setPlaceable(buildingIsPlaceable);
               return;
            }
            
            // place this building
            building.place();
            gameState.getBuildings().add(building);
            
            // add links
            for (Link<Building> link : linksToBuildings)
            {
               if (!link.isCollision())
               {
                  final Building b = link.getLinkedElement();
                  building.getLinks().add(b);
                  b.getLinks().add(building);
               }
            }
            
            // add reachable mineral planets to mine
            if (building instanceof Mine)
            {
               Mine mine = (Mine) building;
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
         final Building building = getBuilding(toUpgrade.poll());
         if (building.isUpgradeable())
         {
            final Player player = building.getPlayer();
            final int costs = building.getCosts();
            
            if (player.getMinerals() < costs)
            {
               Logger.getGlobal().info("Not enough minerals to upgrade building.");
               continue;
            }
            player.removeMinerals(costs);
            
            building.upgrade();
         }
      }
   }
   
   private void recycleAll()
   {
      while (!toRecycle.isEmpty())
      {
         final Building building = getBuilding(toRecycle.poll());
         final Player player = building.getPlayer();
         
         player.addMinerals(building.getRecycleReward());
         
         for (Building linked : building.getLinks())
         {
            linked.getLinks().remove(building);
         }
         gameState.getBuildings().remove(building);
      }
   }
   
   /**
    * Buffers a copy of the game state.
    */
   private void bufferGameState()
   {
      gameStateBuffer = Helpers.deepCopy(gameState);
   }
   
   private void updateWorld(GameTime gameTime)
   {
      // check which buildings are on the energy net
      checkEnergyAvailability();
      
      refreshIndicators();
      
      // economy
      produceEnergy();
      mineMinerals();
      establishBuildings();
      
      // military
      fight();
      removeDeadElements();
      
      // update game time
      int seconds = (int) ((System.nanoTime() - startingTime) / 1000000000);
      gameState.setDuration(seconds);
   }
   
   private void refreshIndicators()
   {
      for (Player player : gameState.getPlayers())
      {
         // reset indicators to make the new current sum
         player.resetEnergyCapacity();
         player.resetMineralsPerMinute();
      }
      
      for (Building building : gameState.getBuildings())
      {
         if (building instanceof Solar)
         {
            final Solar solar = (Solar) building;
            final Player player = solar.getPlayer();
            
            // count energy capacity to player total
            player.addEnergyCapacity(solar.getEnergyCapacity());
         }
         else if (building instanceof Mine)
         {
            final Mine mine = (Mine) building;
            final Player player = mine.getPlayer();
            
            // count mining amount capacity to player total
            player.addMineralsPerMinute(mine.getMiningAmount());
         }
      }
   }
   
   /**
    * Finds the connected buildings to the <code>buildingToBePlaced</code> and
    * computes the lines between.
    */
   private void computeLinksToBuildings(final Building buildingToBePlaced)
   {
      linksToBuildings.clear();
      linksToMineralPlanets.clear();
      
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
               if (building instanceof Relay || building instanceof Solar)
               {
                  removeRest = !link.isCollision();
                  continue;
               }
            }
            
            iterator.remove();
         }
      }
      else if (buildingToBePlaced instanceof Relay || buildingToBePlaced instanceof Solar)
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
               if (building instanceof Solar) continue;
               if (building.getLinks().isEmpty()) continue;
            }
            
            iterator.remove();
         }
      }
      else if (buildingToBePlaced instanceof Laser || buildingToBePlaced instanceof Shipyard)
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
               if (building instanceof Relay || building instanceof Solar)
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
      final List<Solar> solars = new LinkedList<>();
      
      // reset checked for energy flag
      for (Building building : gameState.getBuildings())
      {
         building.setCheckedForEngery(false);
         if (!(building instanceof Solar))
         {
            building.setHasEnergy(false);
         }
         
         // collect solars
         if (building instanceof Solar)
         {
            final Solar solar = (Solar) building;
            solars.add(solar);
         }
      }
      
      // checks recursively
      for (Solar solar : solars)
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
   
   /**
    * Produces energy and counts the energy capacity of every solar of a player.
    */
   protected void produceEnergy()
   {
      for (Building building : gameState.getBuildings())
      {
         if (building instanceof Solar)
         {
            final Solar solar = (Solar) building;
            if (getGameTime().timesPerSecond(solar.getProductionFrequency(), solar.getPosition().x) && solar.isBuilt())
            {
               final Player player = solar.getPlayer();
               
               // produce
               player.addEnergy(solar.getEnergyProduction());
            }
         }
      }
   }
   
   /**
    * Mine minerals.
    */
   protected void mineMinerals()
   {
      for (Building building : gameState.getBuildings())
      {
         if (building instanceof Mine)
         {
            final Mine mine = (Mine) building;
            mine.update(getGameTime());
            
            if (getGameTime().timesPerSecond(mine.getMiningFrequency(), mine.getPosition().x) && mine.isBuilt())
            {
               final Player player = mine.getPlayer();
               if (player.getEnergy() >= mine.getEnergyConsum())
               {
                  mine.mine();
                  final int amount = mine.getMiningAmount();
                  mine.getPlayer().addMinerals(amount);
                  player.removeEnergy(mine.getEnergyConsum());
               }
            }
         }
      }
   }
   
   /**
    * Establish every building by 10 percent 3 times a second.
    */
   protected void establishBuildings()
   {
      for (Building building : gameState.getBuildings())
      {
         if (getGameTime().timesPerSecond(3) && !building.isBuilt() && building.hasEnergy())
         {
            final int BUILD_STEP = 10;
            
            final int buildEnergy = building.getBuildEnergyConsum();
            final int buildEnergyStep = BUILD_STEP / buildEnergy;
            final int currentStep = building.getBuildState() / BUILD_STEP;
            
            if (currentStep % buildEnergyStep == 0)
            {
               // establishing need energy
               final Player player = building.getPlayer();
               if (player.getEnergy() < 1)
               {
                  Logger.getGlobal().info("Not enough energy to establish building.");
                  continue;
               }
               
               player.removeEnergy(1);
            }
            
            building.establish(BUILD_STEP);
         }
      }
   }
   
   /**
    * Attack and defense.
    */
   protected void fight()
   {
      for (Building building : gameState.getBuildings())
      {
         if (building instanceof Laser || building instanceof Shipyard)
         {
            // defend, attack
            building.update(getGameTime());
         }
      }
      
      for (Ship ship : gameState.getShips())
      {
         // move
         ship.update(getGameTime());
      }
   }
   
   /**
    * Removes dead buildings and ships.
    */
   protected void removeDeadElements()
   {
      for (Iterator<Building> iterator = gameState.getBuildings().iterator(); iterator.hasNext();)
      {
         final Building building = (Building) iterator.next();
         if (building.isDead())
         {
            // remove all links
            for (Iterator<Building> iteratorLinked = building.getLinks().iterator(); iteratorLinked.hasNext();)
            {
               final Building linked = (Building) iteratorLinked.next();
               linked.getLinks().remove(building);
            }
            
            // remove from list
            iterator.remove();
         }
      }
      
      for (Iterator<Ship> iterator = gameState.getShips().iterator(); iterator.hasNext();)
      {
         final Ship ship = (Ship) iterator.next();
         if (ship.isDead())
         {
            // remove from list
            iterator.remove();
         }
      }
   }
   
   @Override
   public synchronized int register(final IClient client, final String name)
   {
      // can only register if game is not running
      if (!running)
      {
         final int id = playerId++;
         final Color color = colors[guests.size()];
         final Vector position = gameState.getMap().getHomePlanetPositions().get(guests.size());
         
         final Player player = new Player(id, name, color, position, gameState.getMap().getStartingMinerals());
         final Guest guest = new Guest(client, player);
         
         gameState.getPlayers().add(player);
         gameState.getBuildings().add(player.getHomePlanet());
         guests.add(guest);
         
         return id;
      }
      
      return -1;
   }
   
   @Override
   public synchronized void build(Building building)
   {
      toBuild.offer(building);
   }
   
   @Override
   public synchronized void upgrade(Building building)
   {
      toUpgrade.offer(building);
   }
   
   @Override
   public synchronized void recycle(Building building)
   {
      toRecycle.offer(building);
   }
}
