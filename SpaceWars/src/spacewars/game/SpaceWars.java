package spacewars.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import spacewars.game.model.GameElement;
import spacewars.game.model.Map;
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
    private static SpaceWars instance;
    private Map              map;
    
    private GameElement      selected;
    
    private SpaceWars()
    {}
    
    public static SpaceWars getInstance()
    {
        if (instance == null)
        {
            instance = new SpaceWars();
        }
        return instance;
    }
    
    @Override
    protected void initialize()
    {
        Screen screen = Screen.getInstance();
        
        screen.setTitle("Space Wars");
        screen.setIcon("icon.png");
        // screen.setSize(null);
        screen.setSize(new Dimension(800, 600));
        screen.setDebug(true);
        
        // create map
        map = MapFactory.loadMap("map1.png");
        
        screen.setVisible(true);
    }
    
    @Override
    public void update(GameTime gameTime)
    {
        // return to home planet
        if (Keyboard.getState().isKeyPressed(Key.H))
        {
            Point home = map.getHomePlanetPosition(0);
            Screen.getInstance().getViewport().setCentralPosition(home.x, home.y);
        }
        
        // drag map
        if (Mouse.getState().isButtonDragged(Button.LEFT))
        {
            final int dx = Mouse.getState().getDeltaX();
            final int dy = Mouse.getState().getDeltaY();
            final int x = Screen.getInstance().getViewport().getOriginVector().x + dx;
            final int y = Screen.getInstance().getViewport().getOriginVector().y + dy;
            
            Screen.getInstance().getViewport().setOriginPosition(x, y);
        }
        
        if (Mouse.getState().isButtonPressed(Button.LEFT))
        {
            for (GameElement element : map.getMineralPlanets())
            {
                final Vector origin = Screen.getInstance().getViewport().getOriginVector();
                final Vector mouse = Mouse.getState().getVector();
                
                if (element.isHit(mouse.sub(origin)))
                {
                    selected = element;
                }
            }
        }
    }
    
    @Override
    public void render(Graphics2D g)
    {
        map.render(g);
        
        // TODO: render hud
        
        // render debug info
        final int LINE_DELTA = 25;
        final int LINE_HEIGHT = 14;
        
        g.setColor(Color.WHITE);
        g.drawString("Mouse: " + Mouse.getState().getX() + ", " + Mouse.getState().getX(), 10, 1 * LINE_HEIGHT + LINE_DELTA);
        g.drawString("Mouse delta: " + Mouse.getState().getDeltaX() + ", " + Mouse.getState().getDeltaX(), 10, 2 * LINE_HEIGHT + LINE_DELTA);
        g.drawString("Viewport origin: " + Screen.getInstance().getViewport().getOriginVector().x + ", " + Screen.getInstance().getViewport().getOriginVector().y, 10, 3 * LINE_HEIGHT + LINE_DELTA);
        g.drawString("Viewport central: " + Screen.getInstance().getViewport().getCentralVector().x + ", " + Screen.getInstance().getViewport().getCentralVector().y, 10, 4 * LINE_HEIGHT + LINE_DELTA);
        
    }
    
    public Map getMap()
    {
        return map;
    }
    
    public GameElement getSelected()
    {
        return selected;
    }
}
