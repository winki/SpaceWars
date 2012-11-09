package spacewars.game;

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
        //screen.setSize(null);
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
            final int x = Screen.getInstance().getViewport().getOriginPosition().x + dx;
            final int y = Screen.getInstance().getViewport().getOriginPosition().y + dy;
            
            Screen.getInstance().getViewport().setOriginPosition(x, y);
        }
        
        if (Mouse.getState().isButtonPressed(Button.LEFT))
        {
            for (GameElement element : map.getMineralPlanets())
            {
                final Vector origin = Screen.getInstance().getViewport().getOriginVector();
                final Vector mouse = Mouse.getState().getVector();
                
                if (element.isHit(mouse))
                {
                    System.out.println("Origin: "+origin);
                    System.out.println("Mouse: "+mouse);
                    System.out.println("Klick: "+origin.add(mouse));
                    System.out.println("Planet POS: "+element.getPosition());
                }
            }            
        }
        
        System.out.println(Screen.getInstance().getViewport().getOriginVector());
    }
    
    @Override
    public void render(Graphics2D g)
    {
        map.render(g);
        
        // render hud
           
       
    }
    
    public Map getMap()
    {
        return map;
    }
}
