package spacewars.game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.Random;
import spacewars.game.model.GameElement;
import spacewars.game.model.GameState;
import spacewars.game.model.Map;
import spacewars.game.model.Player;
import spacewars.game.model.Ship;
import spacewars.game.model.buildings.Building;
import spacewars.game.model.buildings.BuildingType;
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
import spacewars.gamelib.geometrics.Vector;

public class SpaceWars extends Game
{
    public static final boolean DEBUG = true;
    
    private static SpaceWars    instance;
    
    private Player              player;
    private GameState           gameState;
    private BuildingType        buildingType;
    private Building            toBuild;
    private GameElement         selected;    
    private Vector              scrollPosition;    
    private final Random        random;
    
    private SpaceWars()
    {
        this.buildingType = BuildingType.NOTHING;
        this.scrollPosition = new Vector();
        this.random = new Random();
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
        final int NUM_SHIPS = 100;
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
        // navigate
        scroll();
        if (Keyboard.getState().isKeyPressed(Key.H))
        {
            returnToHomePlanet();
        }
        
        // move ships
        for (Ship ship : gameState.getShips())
        {
            ship.update(gameTime);
        }
        
        // select
        select();
        
        // build
        setBuildMode();
        build();
    }
    
    private boolean checkCollision(GameElement element)
    {
        // check collision with buildings
        for (Building b : gameState.getBuildings())
        {
            if (element.doesCollideWith(b)) { return true; }
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
            toBuild = null;
        }
    }
    
    private void select()
    {
        // check if mouse is on building/planet
        if (Mouse.getState().isButtonPressed(Button.LEFT))
        {
            for (GameElement element : gameState.getMap().getMineralPlanets())
            {
                final Vector origin = Screen.getInstance().getViewport().getOriginPosition();
                final Vector mouse = Mouse.getState().getVector();
                
                if (element.isHit(mouse.sub(origin)))
                {
                    selected = element;
                }
            }
        }
    }
    
    private void build()
    {
        if (buildingType != BuildingType.NOTHING)
        {
            final Vector origin = Screen.getInstance().getViewport().getOriginPosition();
            final Vector mouse = Mouse.getState().getVector();
            final Vector p = mouse.sub(origin);
            
            switch (buildingType)
            {
                case RELAY:
                    toBuild = new Relay(p);
                    break;
                
                case MINE:
                    toBuild = new Mine(p);
                    break;
                
                case SOLAR:
                    toBuild = new SolarStation(p);
                    break;
                
                case LASER_CANON:
                    toBuild = new LaserCanon(p);
                    break;
                
                case SHIPYARD:
                    toBuild = new Shipyard(p);
                    break;
                
                default:
                    break;
            }
            
            final boolean placeable = !checkCollision(toBuild);
            toBuild.setPlaceable(placeable);
            
            if (toBuild.isPlaceable() && Mouse.getState().isButtonReleased(Button.LEFT))
            {
                toBuild.place();
                gameState.getBuildings().add(toBuild);
                toBuild = null;
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
            final int x = Screen.getInstance().getViewport().getOriginPosition().x + dx;
            final int y = Screen.getInstance().getViewport().getOriginPosition().y + dy;
            
            Screen.getInstance().getViewport().setOriginPosition(x, y);
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
            final int x = Screen.getInstance().getViewport().getOriginPosition().x - dx;
            final int y = Screen.getInstance().getViewport().getOriginPosition().y - dy;
            
            Screen.getInstance().getViewport().setOriginPosition(x, y);
        }
    }
    
    @Override
    public void render(Graphics2D g)
    {
        final AffineTransform transform = g.getTransform();
        
        // add viewport translation to the whole rendering
        final Vector origin = Screen.getInstance().getViewport().getOriginPosition();
        final AffineTransform viewport = AffineTransform.getTranslateInstance(origin.x, origin.y);
        g.setTransform(viewport);
        
        // render world relative to viewport
        renderWorld(g);
        
        // reset transform
        g.setTransform(transform);
        
        renderHud(g);
        
        if (DEBUG)
        {
            renderDebug(g);
        }
    }
    
    private void renderWorld(Graphics2D g)
    {
        // render connection lines between the buildings
        if (toBuild != null && DEBUG)
        {
            g.setColor(Color.RED);
            for (Building building : gameState.getBuildings())
            {
                if (building.isReachableFrom(toBuild))
                {
                    final Vector p1 = toBuild.getPosition();
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
        if (toBuild != null)
        {
            final float TRANSPARENCY = 0.4f;
            
            Composite original = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY));
            toBuild.render(g);
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
        g.drawString("Mouse: " + Mouse.getState().getX() + ", " + Mouse.getState().getX(), DX, 1 * LINE_HEIGHT + DY);
        g.drawString("Mouse delta: " + Mouse.getState().getDeltaX() + ", " + Mouse.getState().getDeltaY(), DX, 2 * LINE_HEIGHT + DY);
        g.drawString("Viewport origin: " + Screen.getInstance().getViewport().getOriginPosition().x + ", " + Screen.getInstance().getViewport().getOriginPosition().y, DX, 3 * LINE_HEIGHT + DY);
        g.drawString("Viewport central: " + Screen.getInstance().getViewport().getCentralPosition().x + ", " + Screen.getInstance().getViewport().getCentralPosition().y, DX, 4 * LINE_HEIGHT + DY);
        g.drawString("Buildingtype: " + buildingType, 10, 5 * LINE_HEIGHT + DY);
        g.drawString("Running slowly: " + getGameTime().isRunningSlowly(), DX, 6 * LINE_HEIGHT + DY);
        g.drawString("Ticks: " + getGameTime().getTicks(), DX, 7 * LINE_HEIGHT + DY);
    }
}
