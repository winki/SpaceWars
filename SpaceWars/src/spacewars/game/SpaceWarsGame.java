package spacewars.game;

import java.awt.Dimension;
import java.awt.Graphics2D;
import spacewars.game.model.Map;
import spacewars.gamelib.Game;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.Mouse;
import spacewars.gamelib.Screen;
import spacewars.gamelib.Viewport;

public class SpaceWarsGame extends Game
{
    public static SpaceWarsGame game;
    
    public Viewport             viewport;
    protected Map               map;
    
    public SpaceWarsGame()
    {
        game = this;
    }
    
    @Override
    protected void initialize()
    {
        viewport = new Viewport(Screen.getInstance().getSize().width, Screen.getInstance().getSize().height);
        map = MapFactory.loadMap("map1.png");
    }
    
    @Override
    protected void initializeScreen(Screen screen)
    {
        screen.setTitle("Space Wars");
        screen.setIcon("icon.png");
        //screen.setSize(null);
        screen.setSize(new Dimension(1280, 800));
        screen.setVisible(true);
    }
    
    @Override
    public void update(GameTime gameTime)
    {
        if (Mouse.getState().isCursorInScreen())
        {
            final int x = Screen.getInstance().getSize().width / 2 - Mouse.getState().getX();
            final int y = Screen.getInstance().getSize().height / 2 - Mouse.getState().getY();
            
            viewport.setPosition(x, y);
        }
    }
    
    @Override
    public void render(Graphics2D g)
    {
        map.render(g);
    }
}
