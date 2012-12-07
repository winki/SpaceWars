package spacewars.game;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
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
   
   // !!! correct placed? need them in rendering and upgrading
   final int                               FONT_LINE = 15;
   final int                               DX = 10;
   final int                               DY = 24;
   final int                               DY_SELECTED = 200;
   final int                               HUD_WIDTH = 160;
   final int                               BAR_HEIGHT = 5;
   final Dimension                         screen = Screen.getInstance().getSize();
   
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
   
   private void returnToHomePlanet()
   {
      Vector p = gameState.getMap().getHomePlanetPositions().get(player.getId());
      Screen.getInstance().getViewport().setCentralPosition(p.x, p.y);
   }
   
   /**
    * This method is only to test multiplayer behaviour
    */
   private void changePlayer()
   {
      if (Keyboard.getState().isKeyPressed(Key.F1))
      {
         player = gameState.getPlayers().get(0);
      }
      else if (Keyboard.getState().isKeyPressed(Key.F2))
      {
         player = gameState.getPlayers().get(1);
      }
   }
   
   private void setBuildMode()
   {
      if (Keyboard.getState().isKeyPressed(Key.R) || Keyboard.getState().isKeyPressed(Key.D1))
      {
         buildingType = BuildingType.RELAY;
      }
      else if (Keyboard.getState().isKeyPressed(Key.M) || Keyboard.getState().isKeyPressed(Key.D2))
      {
         buildingType = BuildingType.MINE;
      }
      else if (Keyboard.getState().isKeyPressed(Key.S) || Keyboard.getState().isKeyPressed(Key.D3))
      {
         buildingType = BuildingType.SOLAR;
      }
      else if (Keyboard.getState().isKeyPressed(Key.L) || Keyboard.getState().isKeyPressed(Key.D4))
      {
         buildingType = BuildingType.LASER_CANON;
      }
      else if (Keyboard.getState().isKeyPressed(Key.Y) || Keyboard.getState().isKeyPressed(Key.D5))
      {
         buildingType = BuildingType.SHIPYARD;
      }
      else if (Keyboard.getState().isKeyPressed(Key.ESCAPE))
      {
         buildingType = BuildingType.NOTHING;
         buildingToBePlaced = null;
         selected = null;
      }
   }
   
   private void select()
   {
      // check if mouse is on building/planet and building/planet is not under the HUD
      if (Mouse.getState().isButtonPressed(Button.LEFT) && Mouse.getState().getX() <= screen.getWidth() - HUD_WIDTH)
      {
         final Vector mousescreen = Mouse.getState().getVector();
         final Vector mouseworld = Screen.getInstance().getViewport().transformScreenToWorld(mousescreen);
         
         for (GameElement element : gameState.getBuildings())
         {
            if (element.collidesWith(mouseworld))
            {
               selected = element;
               return;
            }
         }
         
         for (GameElement element : gameState.getMap().getMineralPlanets())
         {
            if (element.collidesWith(mouseworld))
            {
               selected = element;
               return;
            }
         }
         
         HomeBase homePlanet = player.getHomePlanet();
         if (homePlanet.collidesWith(mouseworld))
         {
            selected = homePlanet;
            return;
         }
         
         // select nothing
         selected = null;
      }
   }
   
   private void build()
   {
      if (buildingType != BuildingType.NOTHING)
      {
         // deselct
         selected = null;
         
         final Vector mouse = Mouse.getState().getVector();
         final Vector position = Screen.getInstance().getViewport().transformScreenToWorld(mouse);
         
         // prebuild object that should be placed
         switch (buildingType)
         {
            case RELAY:
               buildingToBePlaced = new Relay(position, player);
               break;
            
            case MINE:
               buildingToBePlaced = new Mine(position, player);
               break;
            
            case SOLAR:
               buildingToBePlaced = new SolarStation(position, player);
               break;
            
            case LASER_CANON:
               buildingToBePlaced = new LaserCanon(position, player);
               break;
            
            case SHIPYARD:
               buildingToBePlaced = new Shipyard(position, player);
               break;
            
            default:
               break;
         }
         
         // check collisions
         buildingIsPlaceable = !checkCollision(buildingToBePlaced);
         buildingToBePlaced.setPlaceable(buildingIsPlaceable);
         
         if (buildingIsPlaceable)
         {
            // calculate connections
            computeLinksToBuildings();
            
            // can build mines only if there is minimum one mineral planet
            // reachable
            if (buildingToBePlaced instanceof Mine && linksToMineralPlanets.isEmpty())
            {
               buildingIsPlaceable = false;
               buildingToBePlaced.setPlaceable(buildingIsPlaceable);
               return;
            }
            
            // effectively build
            if (Mouse.getState().isButtonReleased(Button.LEFT))
            {
               // place this building
               buildingToBePlaced.place();
               gameState.getBuildings().add(buildingToBePlaced);
               
               // add links
               for (Link<Building> link : linksToBuildings)
               {
                  if (!link.isCollision())
                  {
                     final Building building = link.getLinkedElement();
                     buildingToBePlaced.getLinks().add(building);
                     building.getLinks().add(buildingToBePlaced);
                  }
               }
               
               // add reachable mineral planets to mine
               if (buildingToBePlaced instanceof Mine)
               {
                  Mine mine = (Mine) buildingToBePlaced;
                  for (Link<MineralPlanet> link : linksToMineralPlanets)
                  {
                     mine.getReachableMineralPlanets().add(link.getLinkedElement());
                  }
               }
               
               buildingToBePlaced = null;
               
               /*
               // TODO: uncomment if shift down function is wished
               if (!Keyboard.getState().isKeyDown(Key.SHIFT))
               {
                  // lose building type if shift is not pressed
                  buildingType = BuildingType.NOTHING;               
               }
               */
            }
         }
      }
   }
   
   /**
    * Delete or upgrade selected building
    */
   private void deleteOrUpgrade()
   {
      if (selected != null && selected instanceof Building && !(selected instanceof HomeBase))
      {
         Building selectedBuilding = (Building) selected;
         
         if (Keyboard.getState().isKeyPressed(Key.DELETE))
         {
            // delete
            for (Building linked : selectedBuilding.getLinks())
            {
               linked.getLinks().remove(selectedBuilding);
            }
            getGameState().getBuildings().remove(selectedBuilding);
         }
         
         
         //
         if (Keyboard.getState().isKeyPressed(Key.PAGE_UP) || (Mouse.getState().getX() >= screen.width - HUD_WIDTH + HUD_WIDTH/2 -30 && Mouse.getState().getX() <= screen.width - HUD_WIDTH + HUD_WIDTH/2 + 30 && Mouse.getState().getY() >= 4 * FONT_LINE + DY_SELECTED && Mouse.getState().getY() <= 4 * FONT_LINE + DY_SELECTED - BAR_HEIGHT + 60 && Mouse.getState().isButtonPressed(Button.LEFT)) )
         {
            // upgrade
            if(selectedBuilding.getLevel() <= 3)
               selectedBuilding.upgrade();
         }
      }
   }
   
   /**
    * Enables scrolling on the map.
    */
   private void scroll()
   {
      final Vector m = Mouse.getState().getVector();
      
      // scrolling with left mouse button
      if (buildingType == BuildingType.NOTHING && Mouse.getState().isButtonDragged(Button.LEFT))
      {
         final int dx = Mouse.getState().getDeltaX();
         final int dy = Mouse.getState().getDeltaY();
         
         Screen.getInstance().getViewport().move(dx, dy);
      }
      // scrolling with right mouse button
      else if (Mouse.getState().isButtonDown(Button.RIGHT))
      {
         if (Mouse.getState().isButtonPressed(Button.RIGHT))
         {
            scrollPosition.set(m.x, m.y);
         }
         
         final int DIVISOR = 10;
         final Vector delta = m.sub(scrollPosition);
         final int dx = delta.x / DIVISOR;
         final int dy = delta.y / DIVISOR;
         
         Screen.getInstance().getViewport().move(-dx, -dy);
      }
   }
   
   // TODO: make a copy of the gamestate before rendering. If not, there can
   // appear ConcurrentModificationExceptions because two threads (game thread
<<<<<<< HEAD
   // and awt thread) iterate over the same list at the same time
=======
   // and awt thread) iterate over the same list at the same time

>>>>>>> branch 'master' of https://github.com/winki/SpaceWars.git
   @Override
   public void render(Graphics2D g)
<<<<<<< HEAD
   {
=======
   {
      // TODO Auto-generated method stub
      

>>>>>>> branch 'master' of https://github.com/winki/SpaceWars.git
      // render game state
      gameState.render(g);
      
      // render building thats should be built
      if (buildingToBePlaced != null)
      {
         final float TRANSPARENCY = 0.4f;
         
         Composite original = g.getComposite();
         g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY));
         buildingToBePlaced.render(g);
         g.setComposite(original);
      }
   }
   
   /**
    * Renders the heads up display.
    * 
    * @param g the {@code Graphics2D} object
    */
   private void renderHud(Graphics2D g)
   {
      /*
       *  TODO: kai
       *  
       *  - Anzeige der Ressourcen: Energie, Mineralien
       *  - Anzeige des aktuell ausgewählten GameElements (Tipp: getSelected())
       *  - Siehe The Space Game
       *  
       */
      
      final int seconds = (int) (getGameTime().getTotalGameTime() / 1000000000);
      
      // vars now declared in class
      final float TRANSPARENCY = 0.8f;
      
      
      final Composite original = g.getComposite();
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY));
      g.setColor(Color.BLACK);
      g.fillRect(screen.width - HUD_WIDTH, 0, HUD_WIDTH, screen.height);
      g.setComposite(original);
      
      // player informations
      g.setColor(Color.WHITE);
      g.drawString(String.format("\"%s\"", getGameState().getMap().getName()), screen.width - HUD_WIDTH + DX, 0 * FONT_LINE + DY);
      g.drawString(String.format("Time taken: %02d:%02d", seconds / 60, seconds % 60), screen.width - HUD_WIDTH + DX, 1 * FONT_LINE + DY);
      g.drawString(String.format("Score: %d", player.getScore()), screen.width - HUD_WIDTH + DX, 2 * FONT_LINE + DY);
      
      // minerals
      g.setColor(Color.GREEN);
      g.drawString(String.format("%d minerals", player.getMinerals()), screen.width - HUD_WIDTH + DX, 4 * FONT_LINE + DY);
      g.drawString(String.format("%d minerals per minute", player.getMineralsPerMinute()), screen.width - HUD_WIDTH + DX, 5 * FONT_LINE + DY);
      // energy
      g.setColor(Color.DARK_GRAY);
      g.fillRect(screen.width - HUD_WIDTH + DX, 8 * FONT_LINE + DY - BAR_HEIGHT, HUD_WIDTH - 2 * DX, BAR_HEIGHT);
      g.setColor(Color.CYAN);
      g.drawString(String.format("%d energy (%d%%)", player.getEnergy(), player.getEnergyEfficency()), screen.width - HUD_WIDTH + DX, 7 * FONT_LINE + DY);
      g.fillRect(screen.width - HUD_WIDTH + DX, 8 * FONT_LINE + DY - BAR_HEIGHT, (int) (player.getEnergy() / (double) player.getMaxEnergy() * (HUD_WIDTH - 2 * DX)), BAR_HEIGHT);
      
      // selected object
      if (selected != null)
      {
         if (selected instanceof MineralPlanet)
         {
            final MineralPlanet mineral = (MineralPlanet) selected;
            g.setColor(Color.WHITE);
            g.drawString(String.format("%d ton mineral planet", mineral.getMineralReservesMax()), screen.width - HUD_WIDTH + DX, 0 * FONT_LINE + DY_SELECTED);
            g.setColor(Color.DARK_GRAY);
            g.fillRect(screen.width - HUD_WIDTH + DX, 3 * FONT_LINE + DY_SELECTED - BAR_HEIGHT, HUD_WIDTH - 2 * DX, BAR_HEIGHT);
            g.setColor(Color.GREEN);
            g.drawString(String.format("%d minerals", mineral.getMineralReserves()), screen.width - HUD_WIDTH + DX, 2 * FONT_LINE + DY_SELECTED);
            g.fillRect(screen.width - HUD_WIDTH + DX, 3 * FONT_LINE + DY_SELECTED - BAR_HEIGHT, (int) (mineral.getMineralReserves() / (double) mineral.getMineralReservesMax() * (HUD_WIDTH - 2 * DX)), BAR_HEIGHT);
         }
         
         if (selected instanceof HomeBase)
         {
            final HomeBase home = (HomeBase) selected;
            // TODO: draw homeplanet relevant stuff
         }
         
         if (selected instanceof Building)
         {
            final Building building = (Building) selected;
            // TODO: draw building relevant stuff
            
            // type
            g.setColor(Color.WHITE);
            g.drawString(String.format("%s", building.getName()), screen.width - HUD_WIDTH + DX, 0 * FONT_LINE + DY_SELECTED);
            
            // level
            g.setColor(Color.WHITE);
            g.drawString(String.format("Level %s of 4", building.getLevel()), screen.width - HUD_WIDTH + DX, 1 * FONT_LINE + DY_SELECTED);
            
            // upgrade
            g.setColor(Color.GREEN);
            if (building.getLevel() <= 3){
               g.drawString(String.format("upgrade to level %s of 4", building.getLevel() ), screen.width - HUD_WIDTH + DX, 3 * FONT_LINE + DY_SELECTED);
            }else{
               g.drawString("Maximalstufe erreicht", screen.width - HUD_WIDTH + DX, 3 * FONT_LINE + DY_SELECTED);
            }
            
            g.setColor(Color.GREEN);
            g.fillRect(screen.width - HUD_WIDTH + HUD_WIDTH/2, 4 * FONT_LINE + DY_SELECTED, 1, 50);
            g.drawLine(screen.width - HUD_WIDTH + HUD_WIDTH/2 - 20, 4 * FONT_LINE + DY_SELECTED+20, screen.width - HUD_WIDTH + HUD_WIDTH/2, 4 * FONT_LINE + DY_SELECTED);
            g.drawLine(screen.width - HUD_WIDTH + HUD_WIDTH/2 + 20, 4 * FONT_LINE + DY_SELECTED+20, screen.width - HUD_WIDTH + HUD_WIDTH/2, 4 * FONT_LINE + DY_SELECTED);
            g.draw(new Arc2D.Double(screen.width - HUD_WIDTH + HUD_WIDTH/2 -30, 4 * FONT_LINE + DY_SELECTED - 5,60,60,100,340,Arc2D.OPEN));
            
           
            if (selected instanceof Relay)
            {
               final Relay relay = (Relay) building;
               // TODO: draw relay relevant stuff
               // number of connections
            }
            
            if (selected instanceof SolarStation)
            {
               final SolarStation solar = (SolarStation) building;
               // TODO: draw solar relevant stuff
               // energy capazity
            }
            
            if (selected instanceof Mine)
            {
               final Mine mine = (Mine) building;
               // TODO: draw mine relevant stuff
            }
            
            if (selected instanceof LaserCanon)
            {
               final LaserCanon laser = (LaserCanon) building;
               // TODO: draw laser relevant stuff
            }
            
            if (selected instanceof Shipyard)
            {
               final Shipyard shipyard = (Shipyard) building;
               // TODO: draw shipyard relevant stuff
            }
         }
      }
      /* 
      Dimension dimension = Screen.getInstance().getSize();
      @SuppressWarnings("unused")
      int minerals = player.getMinerals();
      @SuppressWarnings("unused")
      int score = player.getScore();
      int hudX = (int) dimension.getWidth() - 501;
      int hudY = (int) dimension.getHeight() - 101;
      int maxEnergy = 0;
      
      for (Building building : getGameState().getBuildings())
      {
         if (building instanceof SolarStation)
         {
            maxEnergy += ((SolarStation) building).getMaxEnergy();
            // Logger.getGlobal().info("Energy: " + player.getEnergy());
            // Logger.getGlobal().info("player max ener " +
            // player.getMaxEnergy());
         }
      }
      
      player.setMaxEnergy(maxEnergy);
      
      g.setColor(Color.WHITE);
      
      g.drawRect(hudX, hudY, 500, 100);
      g.drawRect(hudX + 10, hudY + 10, 95, 20);
      if (maxEnergy != 0)
      {
         g.fillRect(hudX + 10, hudY + 10, (int) (100.0 / maxEnergy * player.getEnergy()), 20);
      }
      */
   }
   
   /**
    * Renders the debug information.
    * 
    * @param g the <code>Graphics2D</code> object
    */
   private void renderDebug(Graphics2D g)
   {
      // render debug info
      final int DX = 10;
      final int DY = 22;
      final int LINE_HEIGHT = 14;
      
      g.setColor(Color.RED);
      
      g.drawString("FPS: " + getGameTime().getFrameRate(), DX, 0 * LINE_HEIGHT + DY);
      g.drawString("Mouse: " + Mouse.getState().getX() + ", " + Mouse.getState().getY(), DX, 1 * LINE_HEIGHT + DY);
      g.drawString("Mouse delta: " + Mouse.getState().getDeltaX() + ", " + Mouse.getState().getDeltaY(), DX, 2 * LINE_HEIGHT + DY);
      g.drawString("Viewport origin: " + Screen.getInstance().getViewport().getOriginPosition().x + ", " + Screen.getInstance().getViewport().getOriginPosition().y, DX, 3 * LINE_HEIGHT + DY);
      g.drawString("Viewport central: " + Screen.getInstance().getViewport().getCentralPosition().x + ", " + Screen.getInstance().getViewport().getCentralPosition().y, DX, 4 * LINE_HEIGHT + DY);
      g.drawString("Building type: " + buildingType, 10, 5 * LINE_HEIGHT + DY);
      g.drawString("Running slowly: " + getGameTime().isRunningSlowly(), DX, 6 * LINE_HEIGHT + DY);
      g.drawString("Ticks: " + getGameTime().getTicks(), DX, 7 * LINE_HEIGHT + DY);
   }
}
