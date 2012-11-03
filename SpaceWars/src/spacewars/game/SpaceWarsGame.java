package spacewars.game;

import java.awt.Graphics2D;
import spacewars.game.model.Map;
import spacewars.gamelib.Game;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.Screen;

public class SpaceWarsGame extends Game
{    
    private Map map;
    
    @Override
    protected void initialize()
    {
        map = MapFactory.loadMap("map1.png");
    }
    
    @Override
    protected void initScreen(Screen screen)
    {
        screen.setTitle("Space Wars");
        screen.setIcon("icon.png");
        screen.setSize(null);
        screen.setVisible(true);
    }
    
    @Override
    public void update(GameTime gameTime)
    { 
    }
    
    @Override
    public void render(Graphics2D g)
    { 
        map.render(g);
    }    
}
