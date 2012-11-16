package spacewars.game;

import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import spacewars.game.model.GameElement;
import spacewars.game.model.GameState;
import spacewars.game.model.Map;
import spacewars.game.model.Player;
import spacewars.game.model.Ship;
import spacewars.game.model.buildings.*;
import spacewars.game.model.planets.*;
import spacewars.gamelib.Button;
import spacewars.gamelib.*;
import spacewars.gamelib.geometrics.Vector;

public class SpaceWars extends Game
{
    public static final boolean DEBUG = true;
    
    /**
     * Game instance
     */
    private static SpaceWars    instance;
    
    /**
     * Random object
     */
    private final Random        random;
    
    /**
     * The player
     */
    private Player              player;
    
    /**
     * The game state
     */
    private GameState           gameState;
    
    /**
     * Game element that is currently selected by players mouse
     */
    private GameElement         selected;
    
    /**
     * The type of the building that will be built
     */
    private BuildingType        buildingType;
    
    /**
     * Building object that will be built
     */
    private Building            buildingToBePlaced;
    /**
     * Can the building object object {@code toBuild} really be built? Or can't
     * it because of collision
     */
    private boolean             buildingIsPlaceable;
    
    /**
     * Current scroll position
     */
    private Vector              scrollPosition;
    
    private SpaceWars()
    {
        this.random = new Random();
        this.buildingType = BuildingType.NOTHING;
        this.scrollPosition = new Vector();
    }
    
    public static SpaceWars getInstance()
    {
        if (instance == null)
        {
            instance = new SpaceWars();
        }
        return instance;
    }
    
    public GameElement getSelected()
    {
        return selected;
    }
    
    public GameState getGameState()
    {
        return gameState;
    }
    
    @Override
    protected void initialize()
    {
        Screen screen = Screen.getInstance();
        
        screen.setTitle("Space Wars");
        screen.setIcon("icon.png");
        screen.setSize(new Dimension(800, 600));
        screen.setSize(null);
        
        // init game state
        createMap();
        createPlayers();
        createShips();
        
        returnToHomePlanet();
        
        // show screen
        screen.setVisible(true);
    }
    
    private void createMap()
    {
        Map map = MapFactory.loadMap("map1.png");
        gameState = new GameState(map);
    }
    
    private void createPlayers()
    {
        final List<Player> players = gameState.getPlayers();
        
        player = new Player(1, gameState.getMap());
        
        players.add(player);
        players.add(new Player(2, gameState.getMap()));
    }
    
    private void createShips()
    {
        final int NUM_SHIPS = 500;
        final List<Ship> ships = gameState.getShips();
        final Vector home = gameState.getMap().getHomePlanetPosition(player.getId());
        
        for (int i = 0; i < NUM_SHIPS; i++)
        {
            ships.add(new Ship(new Vector(home), random.nextDouble() * Math.PI * 2));
        }
    }
    
    @Override
    public void update(GameTime gameTime)
    {
        /*
         * TODO: winki
         * 
         * - Gebäude korrekt miteinander verbinden
         * - Korrekter Energiefluss
         */
        
        // navigate
        scroll();
        
        if (Keyboard.getState().isKeyPressed(Key.H))
        {
            returnToHomePlanet();
        }
        
        // move ships
        for (Ship ship : gameState.getShips())
        {
            // TODO: kai
            /*
            if (this.isPlaced() && Mouse.getState().getX() >= p.x - r && Mouse.getState().getX() <= p.x + r && Mouse.getState().getY() >= p.y - r && Mouse.getState().getY() <= p.y + r)
            {                
                g.drawString("Build ship with left click | attack with right click", p.x + r + 20, p.y + 20);
                if (Mouse.getState().isButtonPressed(Button.LEFT))
                {
                    if (ships <= 16)
                    {
                        ships += 1;
                        innerY = (int) (innerR * Math.sin(2 * Math.PI / 16 * ships));
                        innerX = (int) Math.sqrt(innerR * innerR - innerY * innerY);
                        if (ships <= 4)
                        {
                            ship.setPosition(new Vector(p.x - innerX, p.y - innerY));
                            gameState.getShips().add(ship);
                        }
                        else if (ships <= 8)
                        {
                            ship.setPosition(new Vector(p.x - innerX, p.y + innerY));
                            gameState.getShips().add(ship);
                        }
                        else if (ships <= 12)
                        {
                            ship.setPosition(new Vector(p.x + innerX, p.y + innerY));
                            gameState.getShips().add(ship);
                        }
                        else
                        {
                            ship.setPosition(new Vector(p.x + innerX, p.y - innerY));
                            gameState.getShips().add(ship);
                        }
                    }
                    else
                    {
                        g.drawString("no space left in hangar", p.x, p.y - 40);
                    }
                }
                else if (Mouse.getState().isButtonPressed(Button.RIGHT))
                { 
                // send ships to attack!
                }
            }
            */
            ship.update(gameTime);
        }
        
        // select
        select();
        
        // build
        setBuildMode();
        build();
        
        // TODO: kai
        // amount of res and energy
        for (Building building : gameState.getBuildings())
        {
            if (building.isPlaced())
            {
                if (building instanceof Mine)
                {
                    player.addMinerals(((Mine) building).getResPerMin());
                    player.removeEnergy(((Mine) building).getEnergyConsumPerMin());
                }
                
                if (building instanceof SolarStation)
                {
                    player.addEnergy(((SolarStation) building).getEnergyPerMin());
                }
            }
        }
    }
    
    private boolean checkCollision(GameElement element)
    {
        // check collision with buildings
        for (Building b : gameState.getBuildings())
        {
            if (element.doesCollideWith(b)) { return true; }
            
            // check collision with links
            for (GameElement linked : b.getLinks())
            {
                // TODO: only calculate the lines once, reuse them in
                // rendering part
                final Vector p1 = b.getPosition();
                final Vector p2 = linked.getPosition();
                final Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
                
                if (element.doesCollideWith(line)) { return true; }
            }
        }
        
        // check collision with mineral planets
        for (MineralPlanet m : gameState.getMap().getMineralPlanets())
        {
            if (element.doesCollideWith(m)) { return true; }
        }
        
        // check collision with home planets
        for (Player p : gameState.getPlayers())
        {
            if (element.doesCollideWith(p.getHomePlanet())) { return true; }
        }
        
        // no collision
        return false;
    }
    
    private boolean checkCollision(Line2D line, Building reachableBuilding)
    {
        // check collision with buildings
        for (Building b : gameState.getBuildings())
        {
            if (b != reachableBuilding && b.doesCollideWith(line)) { return true; }
        }
        
        // check collision with planets
        for (MineralPlanet m : gameState.getMap().getMineralPlanets())
        {
            if (m.doesCollideWith(line)) { return true; }
        }
        
        // check collision with home planets
        for (Player p : gameState.getPlayers())
        {
            if (p.getHomePlanet().doesCollideWith(line)) { return true; }
        }
        
        // no collision
        return false;
    }
    
    private void returnToHomePlanet()
    {
        Vector p = gameState.getMap().getHomePlanetPosition(player.getId());
        Screen.getInstance().getViewport().setCentralPosition(p.x, p.y);
    }
    
    private void setBuildMode()
    {
        if (Keyboard.getState().isKeyPressed(Key.M))
        {
            buildingType = BuildingType.MINE;
        }
        else if (Keyboard.getState().isKeyPressed(Key.S))
        {
            buildingType = BuildingType.SOLAR;
        }
        else if (Keyboard.getState().isKeyPressed(Key.R))
        {
            buildingType = BuildingType.RELAY;
        }
        else if (Keyboard.getState().isKeyPressed(Key.Y))
        {
            buildingType = BuildingType.SHIPYARD;
        }
        else if (Keyboard.getState().isKeyPressed(Key.L))
        {
            buildingType = BuildingType.LASER_CANON;
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
        if (Mouse.getState().isButtonPressed(Button.LEFT))
        {
            final Vector mousescreen = Mouse.getState().getVector();
            final Vector mouseworld = Screen.getInstance().getViewport().transformScreenToWorld(mousescreen);
            
            for (GameElement element : gameState.getBuildings())
            {
                if (element.isHit(mouseworld))
                {
                    selected = element;
                    return;
                }
            }
            
            for (GameElement element : gameState.getMap().getMineralPlanets())
            {
                if (element.isHit(mouseworld))
                {
                    selected = element;
                    return;
                }
            }
            
            HomePlanet homePlanet = player.getHomePlanet();
            if (homePlanet.isHit(mouseworld))
            {
                selected = homePlanet;
                return;
            }
        }
    }
    
    private void build()
    {
        if (buildingType != BuildingType.NOTHING)
        {
            // deselect current selected game element
            selected = null;
            
            final Vector origin = Screen.getInstance().getViewport().getOriginPosition();
            final Vector mouse = Mouse.getState().getVector();
            final Vector p = mouse.sub(origin);
            
            switch (buildingType)
            {
                case RELAY:
                    buildingToBePlaced = new Relay(p);
                    break;
                
                case MINE:
                    buildingToBePlaced = new Mine(p);
                    break;
                
                case SOLAR:
                    buildingToBePlaced = new SolarStation(p);
                    break;
                
                case LASER_CANON:
                    buildingToBePlaced = new LaserCanon(p);
                    break;
                
                case SHIPYARD:
                    buildingToBePlaced = new Shipyard(p);
                    break;
                
                default:
                    break;
            }
            
            // check collisions
            buildingIsPlaceable = !checkCollision(buildingToBePlaced);
            buildingToBePlaced.setPlaceable(buildingIsPlaceable);
            
            if (buildingIsPlaceable && Mouse.getState().isButtonReleased(Button.LEFT))
            {
                // create links to other buildings that are reachable
                for (Building building : gameState.getBuildings())
                {
                    // TODO: only calculate the lines once, reuse them in
                    // rendering part
                    
                    final Vector p1 = buildingToBePlaced.getPosition();
                    final Vector p2 = building.getPosition();
                    Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
                    
                    if (building.isReachableFrom(buildingToBePlaced) && !checkCollision(line, building))
                    {
                        // connect them
                        buildingToBePlaced.getLinks().add(building);
                        building.getLinks().add(buildingToBePlaced);
                    }
                }
                
                // place this building
                buildingToBePlaced.place();
                gameState.getBuildings().add(buildingToBePlaced);
                buildingToBePlaced = null;
            }
        }
    }
    
    private void scroll()
    {
        final Vector m = Mouse.getState().getVector();
        
        if (buildingType == BuildingType.NOTHING && Mouse.getState().isButtonDragged(Button.LEFT))
        {
            final int dx = Mouse.getState().getDeltaX();
            final int dy = Mouse.getState().getDeltaY();
            
            Screen.getInstance().getViewport().move(dx, dy);
        }
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
    // and awt thread) iterate over the same list at the same time
    @Override
    public void render(Graphics2D g)
    {
        final AffineTransform original = g.getTransform();
        
        // add viewport translation and scale to the world rendering
        final AffineTransform viewport = Screen.getInstance().getViewport().getWorldToScreenTransform();
        
        g.setTransform(viewport);
        
        // render world relative to viewport
        renderWorld(g);
        
        // reset transform
        g.setTransform(original);
        
        renderHud(g);
        
        if (DEBUG)
        {
            renderDebug(g);
        }
    }
    
    private void renderWorld(Graphics2D g)
    {
        // render connection lines between the buildings
        if (buildingToBePlaced != null && buildingIsPlaceable)
        {
            g.setColor(Color.RED);
            for (Building building : gameState.getBuildings())
            {
                if (building.isReachableFrom(buildingToBePlaced))
                {
                    final Vector p1 = buildingToBePlaced.getPosition();
                    final Vector p2 = building.getPosition();
                    Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
                    g.setColor(checkCollision(line, building) ? Color.red : Color.WHITE);
                    g.draw(line);
                }
            }
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
        Dimension dimension = Screen.getInstance().getSize();
       // int energy = player.getEnergy();
        int energy = 4;
        int minerals = player.getMinerals();
        int score = player.getScore();
        
        int maxEnergy = 10;
        
        g.setColor(Color.white);
        int hudX = (int) dimension.getWidth() - 501;
        int hudY = (int) dimension.getHeight() - 101;
       
        g.drawRect(hudX, hudY, 500, 100);
        
        g.drawRect(hudX + 100, hudY + 10, 100, 20);
        g.fillRect(hudX + 100, hudY + 10, 100/maxEnergy*energy, 20);
        
        
        
        /*
         *  TODO: kai
         *  
         *  - Anzeige der Ressourcen: Energie, Mineralien
         *  - Anzeige des aktuell ausgewählten GameElements (Tipp: getSelected())
         *  - Siehe The Space Game
         *  
         */
        
    }
    
    /**
     * Renders the debug information.
     * 
     * @param g the {@code Graphics2D} object
     */
    private void renderDebug(Graphics2D g)
    {
        // render debug info
        final int DX = 10;
        final int DY = 22;
        final int LINE_HEIGHT = 14;
        
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + getGameTime().getFrameRate(), DX, 0 * LINE_HEIGHT + DY);
        g.drawString("Mouse: " + Mouse.getState().getX() + ", " + Mouse.getState().getY(), DX, 1 * LINE_HEIGHT + DY);
        g.drawString("Mouse delta: " + Mouse.getState().getDeltaX() + ", " + Mouse.getState().getDeltaY(), DX, 2 * LINE_HEIGHT + DY);
        g.drawString("Viewport origin: " + Screen.getInstance().getViewport().getOriginPosition().x + ", " + Screen.getInstance().getViewport().getOriginPosition().y, DX, 3 * LINE_HEIGHT + DY);
        g.drawString("Viewport central: " + Screen.getInstance().getViewport().getCentralPosition().x + ", " + Screen.getInstance().getViewport().getCentralPosition().y, DX, 4 * LINE_HEIGHT + DY);
        g.drawString("Buildingtype: " + buildingType, 10, 5 * LINE_HEIGHT + DY);
        g.drawString("Running slowly: " + getGameTime().isRunningSlowly(), DX, 6 * LINE_HEIGHT + DY);
        g.drawString("Ticks: " + getGameTime().getTicks(), DX, 7 * LINE_HEIGHT + DY);
        g.drawString("Amount of Minerals: " + player.getMinerals(), 10, 8 * LINE_HEIGHT + DY);
        g.drawString("Amount of Energy: " + player.getEnergy(), 10, 9 * LINE_HEIGHT + DY);
    }
}
