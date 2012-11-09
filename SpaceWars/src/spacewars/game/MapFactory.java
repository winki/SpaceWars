package spacewars.game;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;
import spacewars.game.model.Map;
import spacewars.game.model.MineralPlanet;
import spacewars.game.model.Star;
import spacewars.gamelib.Screen;
import spacewars.gamelib.geometrics.Vector;
import spacewars.util.Ressources;

public class MapFactory
{
    private static final int NUMBER_OF_STARS  = 500;
    public static final int  NUMBER_OF_LAYERS = 10;
    private static final int UNIT_SIZE        = 4;
    
    /**
     * Loads a map from a path. Example:
     * 
     * <pre>
     * Map map = MapFactory.loadMap(&quot;map.png&quot;);
     * </pre>
     * 
     * @param path the path relative to the directory "/res/maps/"
     * @return the loaded map
     */
    public static Map loadMap(String path)
    {
        Map map = null;
        
        BufferedImage image = Ressources.loadBufferedImage("../maps/" + path);
        if (image != null)
        {
            map = new Map(image.getWidth() * UNIT_SIZE, image.getHeight() * UNIT_SIZE);
            for (int x = 0; x < image.getWidth(); x++)
            {
                for (int y = 0; y < image.getHeight(); y++)
                {
                    Color color = new Color(image.getRGB(x, y));
                    if (color.getRed() == 255 && color.getGreen() == 0 && color.getBlue() == 0)
                    {
                        // home planet position
                        map.addHomePlanetPosition(new Point(getPixelPosition(x), getPixelPosition(y)));
                    }
                    else if (color.getGreen() > 0 && color.getRed() == 0 && color.getBlue() == 0)
                    {
                        // mineral planet
                        int mineralReserves = (100 * color.getGreen()) / 255;
                        map.addMineralPlanet(new MineralPlanet(new Vector(getPixelPosition(x), getPixelPosition(y)), mineralReserves));
                    }
                }
            }
            image.flush();
        }
        
        final int OVERLAP = 0;
        final Random random = new Random();
        int x, y, layer;
        for (int i = 0; i < NUMBER_OF_STARS; i++)
        {
            x = random.nextInt(Screen.getInstance().getSize().width + 2 * OVERLAP) - OVERLAP;
            y = random.nextInt(Screen.getInstance().getSize().height + 2 * OVERLAP) - OVERLAP;
            layer = random.nextInt(NUMBER_OF_LAYERS);
            
            map.addStar(new Star(x, y, layer));
        }
        
        return map;
    }
    
    private static int getPixelPosition(int unitPosition)
    {
        return unitPosition * UNIT_SIZE + UNIT_SIZE / 2;
    }
}
