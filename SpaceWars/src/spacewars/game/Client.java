package spacewars.game;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import spacewars.game.model.GameElement;
import spacewars.game.model.GameState;
import spacewars.game.model.Link;
import spacewars.game.model.Player;
import spacewars.game.model.Star;
import spacewars.game.model.buildings.Building;
import spacewars.game.model.buildings.BuildingType;
import spacewars.game.model.buildings.Homebase;
import spacewars.game.model.buildings.Laser;
import spacewars.game.model.buildings.Mine;
import spacewars.game.model.buildings.Relay;
import spacewars.game.model.buildings.Shipyard;
import spacewars.game.model.buildings.Solar;
import spacewars.game.model.planets.MineralPlanet;
import spacewars.gamelib.Button;
import spacewars.gamelib.GameClient;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.Key;
import spacewars.gamelib.Keyboard;
import spacewars.gamelib.Mouse;
import spacewars.gamelib.Screen;
import spacewars.gamelib.Vector;
import spacewars.network.IClient;
import spacewars.network.IServer;
import spacewars.util.Config;
import spacewars.util.Ressources;
import de.root1.simon.annotation.SimonRemote;

@SimonRemote(value = { IClient.class })
public class Client extends GameClient implements IClient
{
   /**
    * Game instance
    */
   private static Client                   instance;
   /**
    * Server proxy
    */
   private IServer                         server;
   /**
    * The stars in the background
    */
   private final List<Star>                stars;
   private static final int                STARS_NUM          = 600;
   private static final int                STARS_NUM_LAYERS   = 10;
   private static final float              STARS_TRANSPARENCY = 0.3f;
   /**
    * The player
    */
   private int                             playerId           = -1;
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
   /**
    * Intro screen before the game starts
    */
   private final IntroScreen               intro;
   
   final int HUD_WIDTH = 160;
   final Dimension screen = Screen.getInstance().getSize();
   final int FONT_LINE = 15;
   final int DY_SELECTED = 200;
   Vector p = new Vector(screen.width - HUD_WIDTH + HUD_WIDTH / 2 - 30, 4 * FONT_LINE + DY_SELECTED);
   
   
   /**
    * Default constructor.
    */
   private Client()
   {
      this.buildingType = BuildingType.NOTHING;
      this.scrollPosition = new Vector();
      this.stars = new LinkedList<Star>();
      this.linksToBuildings = new LinkedList<>();
      this.linksToMineralPlanets = new LinkedList<>();
      this.intro = new IntroScreen();
   }
   
   public static Client getInstance()
   {
      if (instance == null)
      {
         instance = new Client();
      }
      return instance;
   }
   
   public static boolean isDebug()
   {
      return Config.getBool("client/debug");
   }
   
   public GameElement getSelected()
   {
      return selected;
   }
   
   public GameState getGameState()
   {
      return gameState;
   }
   
   public void setServer(IServer server)
   {
      this.server = server;
      
   }
   
   protected void registerAtServer(final String name)
   {
      try
      {
         playerId = server.register(this, name);
         
         // create dummy players
         if (Config.getBool("server/dummyPlayers"))
         {
            for (int i = 0; i < Config.getInt("server/minPlayers") - 1; i++)
            {
               server.register(null, "Dummy");
            }
         }
      }
      catch (Exception ex)
      {
         Logger.getGlobal().log(Level.SEVERE, "Couldn't register at server.", ex);
      }
   }
   
   @Override
   protected void initialize()
   {
      // init intro
      intro.setVisible(true);
      
      Screen screen = Screen.getInstance();
      
      screen.setTitle("Space Wars");
      screen.setIcon("icon.png");
      screen.setSize(Config.getBool("client/fullscreen") ? null : new Dimension(800, 600));
      screen.setResizable(false);
      
      // show screen
      screen.setVisible(true);
   }
   
   private void startGame()
   {
      createStars();
      
      // TODO: is this done now in intro screen?
      returnToHomePlanet();
   }
   
   private void createStars()
   {
      final Random random = new Random();
      
      for (int i = 0; i < STARS_NUM; i++)
      {
         final int layer = random.nextInt(STARS_NUM_LAYERS);
         stars.add(new Star(random.nextFloat(), random.nextFloat(), layer));
      }
   }
   
   /**
    * Updates the game state with the newest version from server.
    */
   private void updateGameState()
   {
      try
      {
         // get gamestate
         long start = System.currentTimeMillis();
         final GameState newGameState = server.getGameState();
         long time = System.currentTimeMillis() - start;
         if (isDebug())
         {
            Logger.getGlobal().info(String.format("Time to get game state: %d ms\n", time));
         }
         
         // update client objects
         if (newGameState != null)
         {
            final boolean startGame = gameState == null;
            gameState = newGameState;
            
            // update player
            if (playerId != -1)
            {
               player = gameState.getPlayers().get(playerId);
            }
            
            // update selected building/mineral planet
            if (selected != null)
            {
               if (selected instanceof Building)
               {
                  final int index = gameState.getBuildings().indexOf(selected);
                  selected = gameState.getBuildings().get(index);
               }
               else if (selected instanceof MineralPlanet)
               {
                  final int index = gameState.getMap().getMineralPlanets().indexOf(selected);
                  selected = gameState.getMap().getMineralPlanets().get(index);
               }
            }
            
            // begin of game
            if (startGame)
            {
               startGame();
            }
         }
      }
      catch (Exception ex)
      {
         Logger.getGlobal().log(Level.WARNING, "Couldn't get game state from server.", ex);
      }
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      // update intro
      if (intro.isVisible()) intro.update(gameTime);
      
      if (gameState != null)
      {
         // else
         {
            // Zum homeplanet fliegen
            // if (i<100){
            // if (i == 1)
            // Screen.getInstance().getViewport().move(-10,-15);
            // i++;
            // }
            // else{
            
            // if activated, user can switch player with F1 - F4
            if (Config.getBool("client/changePlayer"))
            {
               final int numPlayers = gameState.getPlayers().size();
               if (numPlayers >= 1 && Keyboard.getState().isKeyPressed(Key.F1))
               {
                  playerId = 0;
               }
               else if (numPlayers >= 2 && Keyboard.getState().isKeyPressed(Key.F2))
               {
                  playerId = 1;
               }
               else if (numPlayers >= 3 && Keyboard.getState().isKeyPressed(Key.F3))
               {
                  playerId = 2;
               }
               else if (numPlayers >= 4 && Keyboard.getState().isKeyPressed(Key.F4))
               {
                  playerId = 3;
               }
            }
            
            scroll();
            if (Keyboard.getState().isKeyPressed(Key.HOME))
            {
               returnToHomePlanet();
            }
            
            // select
            select();
            
            // upgrade or recycle buildings
            upgradeOrRecycle();
            
            // build
            setBuildMode();
            build();
            // }
         }
      }
   }
   
   @Override
   protected void sync()
   {
      updateGameState();
   }
   
   /**
    * Finds the connected buildings to the <code>buildingToBePlaced</code> and
    * computes the lines between.
    */
   private void computeLinksToBuildings()
   {
      linksToBuildings.clear();
      linksToMineralPlanets.clear();
      
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
   
   private void returnToHomePlanet()
   {
      Vector p = gameState.getMap().getHomePlanetPositions().get(player.getId());
      Screen.getInstance().getViewport().setCentralPosition(p.x, p.y);
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
      // check if mouse is on building/planet
      if (Mouse.getState().isButtonDown(Button.LEFT) && Mouse.getState().getX() <= screen.width - HUD_WIDTH)
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
               buildingToBePlaced = new Solar(position, player);
               break;
            case LASER_CANON:
               buildingToBePlaced = new Laser(position, player);
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
               try
               {
                  server.build(buildingToBePlaced);
                  buildingToBePlaced = null;
               }
               catch (Exception ex)
               {
                  Logger.getGlobal().log(Level.WARNING, "Couldn't build building on server.", ex);
               }
               
               /*
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
    * Upgrade or recycle selected building
    */
   private void upgradeOrRecycle()
   {
      if (selected != null && selected instanceof Building && !(selected instanceof Homebase))
      {
         final Building selectedBuilding = (Building) selected;
         
         //calculate distance from mouse to center of upgrade circle
         Vector p2 = Mouse.getState().getVector();
         int distance = (int) Math.sqrt(Math.pow(Math.abs(p.x+30 - p2.x),2) + Math.pow(Math.abs(p.y+30 - p2.y),2));
         if (Keyboard.getState().isKeyPressed(Key.PAGE_UP) || Mouse.getState().isButtonPressed(Button.LEFT) && distance <= 30)
         {
            // upgrade
            try
            {
               server.upgrade(selectedBuilding);
            }
            catch (Exception ex)
            {
               Logger.getGlobal().log(Level.WARNING, "Couldn't upgrade building on server.", ex);
            }
         }
         else if (Keyboard.getState().isKeyPressed(Key.DELETE))
         {
            // recycle
            try
            {
               server.recycle(selectedBuilding);
               selected = null;
            }
            catch (Exception ex)
            {
               Logger.getGlobal().log(Level.WARNING, "Couldn't recycle building on server.", ex);
            }
         }
      }
   }
   
   /**
    * Enables scrolling on the map.
    */
   private void scroll()
   {
      final Vector mouse = Mouse.getState().getVector();
      
      // scrolling by dragging right mouse button
      if (Mouse.getState().isButtonDragged(Button.RIGHT))
      {
         final int dx = Mouse.getState().getDeltaX();
         final int dy = Mouse.getState().getDeltaY();
         
         Screen.getInstance().getViewport().move(dx, dy);
         Screen.getInstance().setCursor(Cursor.MOVE_CURSOR);
      }
      // scrolling by holding right mouse button and shift key
      else if (Keyboard.getState().isKeyDown(Key.SHIFT))
      {
         if (Keyboard.getState().isKeyPressed(Key.SHIFT))
         {
            scrollPosition.set(mouse.x, mouse.y);
         }
         
         final int scrollSlowing = Config.getInt("client/scrollSlowing");
         final Vector delta = mouse.sub(scrollPosition);
         final int dx = delta.x / scrollSlowing;
         final int dy = delta.y / scrollSlowing;
         
         Screen.getInstance().getViewport().move(-dx, -dy);
         Screen.getInstance().setCursor(Cursor.MOVE_CURSOR);
      }
      else
      {
         // reset cursor
         Screen.getInstance().setCursor(Cursor.DEFAULT_CURSOR);
      }
   }
   
   @Override
   public void render(Graphics2D g)
   {
      // render stars in the background
      if (gameState != null) renderStars(g);
      
      // add viewport translation and scale to the world rendering
      final AffineTransform viewport = Screen.getInstance().getViewport().getWorldToScreenTransform();
      final AffineTransform original = g.getTransform();
      g.setTransform(viewport); // render world relative to viewport
      {
         // render world (game state, map...)
         if (gameState != null) renderWorld(g);
      }
      g.setTransform(original); // reset transform
      {
         // render heads up display
         if (gameState != null) renderHud(g);
         
         // debug
         if (isDebug()) renderDebug(g);
         
         // render intro only if there's no gamestate
         if (intro.isVisible()) intro.render(g);
      }
   }
   
   private void renderStars(Graphics2D g)
   {
      final Vector view = Screen.getInstance().getViewport().getCentralPosition();
      final int screenw = Screen.getInstance().getSize().width;
      final int screenh = Screen.getInstance().getSize().height;
      
      // background image
      final Image img = Ressources.loadImage("../maps/map1.jpg");
      final Dimension screen = Screen.getInstance().getSize();
      final int bx = (int) (view.x / 500) + (int) (screen.getWidth() - img.getWidth(null)) / 2;
      final int by = (int) (view.y / 500) + (int) (screen.getHeight() - img.getHeight(null)) / 2;
      g.drawImage(img, bx, by, null);
      
      final Composite original = g.getComposite();
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, STARS_TRANSPARENCY));
      g.setColor(Color.WHITE);
      
      final int DEEP_DELTA = 2;
      final int DEEP_FACTOR = 1;
      final double FACTOR = 0.5;
      
      // render stars
      for (Star star : stars)
      {
         final int SIZE = (int) ((STARS_NUM_LAYERS - star.getLayer()) * FACTOR);
         
         int x = (int) (view.x / (1 + DEEP_DELTA + star.getLayer() * DEEP_FACTOR) + star.getX() * screenw - SIZE / 2) % screenw;
         int y = (int) (view.y / (1 + DEEP_DELTA + star.getLayer() * DEEP_FACTOR) + star.getY() * screenh - SIZE / 2) % screenh;
         if (x < 0) x += screenw;
         if (y < 0) y += screenh;
         
         g.fillOval(x, y, SIZE, SIZE);
      }
      g.setComposite(original);
   }
   
   private void renderWorld(Graphics2D g)
   {
      // render connection lines between the buildings
      if (buildingToBePlaced != null && buildingIsPlaceable)
      {
         final float TRANSPARENCY = 0.4f;
         
         Composite original = g.getComposite();
         g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY));
         
         g.setColor(Color.RED);
         for (Link<Building> link : linksToBuildings)
         {
            final Line2D line = link.getLine();
            
            g.setColor(link.isCollision() ? Color.RED : Color.WHITE);
            g.draw(line);
         }
         
         if (!linksToMineralPlanets.isEmpty())
         {
            final int STROKE_WIDTH = 2;
            final Stroke stroke = g.getStroke();
            g.setStroke(new BasicStroke(STROKE_WIDTH));
            g.setColor(Color.GREEN);
            for (Link<MineralPlanet> link : linksToMineralPlanets)
            {
               final Line2D line = link.getLine();
               
               g.draw(line);
            }
            g.setStroke(stroke);
         }
         
         g.setComposite(original);
      }
      
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
      if (player == null) return;
      
      /*
       *  TODO: kai
       *  
       *  - Anzeige der Ressourcen: Energie, Mineralien
       *  - Anzeige des aktuell ausgewählten GameElements (Tipp: getSelected())
       *  - Siehe The Space Game
       *  
       */
      
      final int DX = 10;
      final int DY = 24;

      
      final int BAR_HEIGHT = 5;
      
      final int seconds = gameState.getDuration();
      final float TRANSPARENCY = 0.8f;
      
      final Composite original = g.getComposite();
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY));
      g.setColor(Color.BLACK);
      g.fillRect(screen.width - HUD_WIDTH, 0, HUD_WIDTH, screen.height);
      g.setComposite(original);
      
      // map informations
      g.setColor(Color.WHITE);
      g.drawString(String.format("\"%s\"", getGameState().getMap().getName()), screen.width - HUD_WIDTH + DX, 0 * FONT_LINE + DY);
      g.drawString(String.format("Time taken: %02d:%02d", seconds / 60, seconds % 60), screen.width - HUD_WIDTH + DX, 1 * FONT_LINE + DY);
      
      // player informations
      g.setColor(player.getColor());
      g.drawString(String.format("%s", player.getName()), screen.width - HUD_WIDTH + DX, 3 * FONT_LINE + DY);
      g.drawString(String.format("Score: %d", player.getScore()), screen.width - HUD_WIDTH + DX, 4 * FONT_LINE + DY);
      
      // minerals
      g.setColor(Color.GREEN);
      g.drawString(String.format("%d minerals", player.getMinerals()), screen.width - HUD_WIDTH + DX, 6 * FONT_LINE + DY);
      g.drawString(String.format("%d minerals per minute", player.getMineralsPerMinute()), screen.width - HUD_WIDTH + DX, 7 * FONT_LINE + DY);
      
      // energy
      g.setColor(Color.DARK_GRAY);
      g.fillRect(screen.width - HUD_WIDTH + DX, 10 * FONT_LINE + DY - BAR_HEIGHT, HUD_WIDTH - 2 * DX, BAR_HEIGHT);
      g.setColor(Color.CYAN);
      g.drawString(String.format("%d energy (%d%%)", player.getEnergy(), player.getEnergyEfficency()), screen.width - HUD_WIDTH + DX, 9 * FONT_LINE + DY);
      g.fillRect(screen.width - HUD_WIDTH + DX, 10 * FONT_LINE + DY - BAR_HEIGHT, (int) (player.getEnergy() / (double) player.getMaxEnergy() * (HUD_WIDTH - 2 * DX)), BAR_HEIGHT);
      
      // selected object
      if (selected != null)
      {
         if (selected instanceof MineralPlanet)
         {
            final MineralPlanet mineral = (MineralPlanet) selected;
            g.setColor(Color.WHITE);
            g.drawString(String.format("%d ton mineral planet", mineral.getMineralCapacity()), screen.width - HUD_WIDTH + DX, 0 * FONT_LINE + DY_SELECTED);
            g.setColor(Color.DARK_GRAY);
            g.fillRect(screen.width - HUD_WIDTH + DX, 3 * FONT_LINE + DY_SELECTED - BAR_HEIGHT, HUD_WIDTH - 2 * DX, BAR_HEIGHT);
            g.setColor(Color.GREEN);
            g.drawString(String.format("%d minerals", mineral.getMinerals()), screen.width - HUD_WIDTH + DX, 2 * FONT_LINE + DY_SELECTED);
            g.fillRect(screen.width - HUD_WIDTH + DX, 3 * FONT_LINE + DY_SELECTED - BAR_HEIGHT, (int) (mineral.getMinerals() / (double) mineral.getMineralCapacity() * (HUD_WIDTH - 2 * DX)), BAR_HEIGHT);
         }
         
         if (selected instanceof Homebase)
         {
            final Homebase home = (Homebase) selected;
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
            g.drawString(String.format("Level %d of %d", building.getLevel(), building.getHighestLevel()), screen.width - HUD_WIDTH + DX, 1 * FONT_LINE + DY_SELECTED);
            
            // upgrade
            if (building.isUpgradeable())
            {
               g.setColor(Color.GREEN);
               if (building.getLevel() <= 3)
               {
                  g.drawString(String.format("Upgrade to level %d of %d", building.getLevel(), building.getHighestLevel()), screen.width - HUD_WIDTH + DX, 3 * FONT_LINE + DY_SELECTED);
               }
               else
               {
                  g.drawString("Maximalstufe erreicht", screen.width - HUD_WIDTH + DX, 3 * FONT_LINE + DY_SELECTED);
               }
               
               g.setColor(Color.GREEN);
              
               
               final Image img = Ressources.loadImage("../img/upgrade.png");
               g.drawImage(img, p.x, p.y, 60, 60, null);
               //g.fillRect(screen.width - HUD_WIDTH + HUD_WIDTH / 2, 4 * FONT_LINE + DY_SELECTED, 1, 50);
//               g.drawLine(screen.width - HUD_WIDTH + HUD_WIDTH / 2 - 20, 4 * FONT_LINE + DY_SELECTED + 20, screen.width - HUD_WIDTH + HUD_WIDTH / 2, 4 * FONT_LINE + DY_SELECTED);
//               g.drawLine(screen.width - HUD_WIDTH + HUD_WIDTH / 2 + 20, 4 * FONT_LINE + DY_SELECTED + 20, screen.width - HUD_WIDTH + HUD_WIDTH / 2, 4 * FONT_LINE + DY_SELECTED);
//               g.draw(new Arc2D.Double(screen.width - HUD_WIDTH + HUD_WIDTH / 2 - 30, 4 * FONT_LINE + DY_SELECTED - 5, 60, 60, 100, 340, Arc2D.OPEN));
            }
            
            if (selected instanceof Relay)
            {
               final Relay relay = (Relay) building;
               // TODO: draw relay relevant stuff
               // number of connections
            }
            
            if (selected instanceof Solar)
            {
               final Solar solar = (Solar) building;
               // TODO: draw solar relevant stuff
               // energy capazity
            }
            
            if (selected instanceof Mine)
            {
               final Mine mine = (Mine) building;
               // TODO: draw mine relevant stuff
            }
            
            if (selected instanceof Laser)
            {
               final Laser laser = (Laser) building;
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
