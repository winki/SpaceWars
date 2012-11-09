package spacewars.game;

import java.awt.Graphics2D;
import java.awt.Point;
import spacewars.game.model.Map;
import spacewars.gamelib.Button;
import spacewars.gamelib.Game;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.Key;
import spacewars.gamelib.Keyboard;
import spacewars.gamelib.Mouse;
import spacewars.gamelib.Screen;

public class SpaceWarsGame extends Game
{
    private static SpaceWarsGame instance;
    private Map              map;
    
    private SpaceWarsGame()
    {}
    
    public static SpaceWarsGame getInstance()
    {
        if (instance == null)
        {
            instance = new SpaceWarsGame();
        }
        return instance;
    }
    
    @Override
    protected void initialize()
    {
        Screen screen = Screen.getInstance();
        
        screen.setTitle("Space Wars");
        screen.setIcon("icon.png");
        screen.setSize(null);
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
            final int x = Screen.getInstance().getViewport().getOriginPosition().x + dx;
            final int y = Screen.getInstance().getViewport().getOriginPosition().y + dy;
            
            Screen.getInstance().getViewport().setOriginPosition(x, y);
        }
    }
    
    @Override
    public void render(Graphics2D g)
    {
        map.render(g);
    }
    
    public Map getMap()
    {
        return map;
    }
}
