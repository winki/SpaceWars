package spacewars.game;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;
import spacewars.game.model.Map;
import spacewars.game.model.planets.MineralPlanet;
import spacewars.gamelib.geometrics.Vector;
import spacewars.util.Ressources;

public class MapFactory
{
    private static final int    UNIT_SIZE                 = 10;
    
    private static final int    MINERAL_PLANET_MIN_RADIUS = 5;
    private static final int    MINERAL_PLANET_MAX_RADIUS = 30;
    
    private static final Random random                    = new Random(0);
    
    /**
     * Loads a map from a path. Example:
     * <p>
     * {@code Map map = MapFactory.loadMap("map.png");}
     * 
     * @param path the path relative to the directory "/res/maps/"
     * @return the loaded map
     */
    public static Map loadMap(String path)
    {
        Map map = null;
        final BufferedImage image = Ressources.loadBufferedImage("../maps/" + path);
        final int NUM_STARS = 500;
        final int NUM_LAYERS = 10;
        
        if (image != null)
        {
            map = new Map(image.getWidth() * UNIT_SIZE, image.getHeight() * UNIT_SIZE, NUM_STARS, NUM_LAYERS);
            for (int x = 0; x < image.getWidth(); x++)
            {
                for (int y = 0; y < image.getHeight(); y++)
                {
                    Color color = new Color(image.getRGB(x, y));
                    if (color.getRed() == 255 && color.getGreen() == 0 && color.getBlue() == 0)
                    {
                        // home planet position
                        map.getHomePlanetPositions().add(new Vector(getPixelPosition(x), getPixelPosition(y)));
                    }
                    else if (color.getGreen() > 0 && color.getRed() == 0 && color.getBlue() == 0)
                    {
                        // mineral planet
                        final int mineralReserves = (100 * color.getGreen()) / 255;
                        final int radius = random.nextInt(MINERAL_PLANET_MAX_RADIUS - MINERAL_PLANET_MIN_RADIUS + 1) + MINERAL_PLANET_MIN_RADIUS;
                        map.getMineralPlanets().add(new MineralPlanet(new Vector(getPixelPosition(x), getPixelPosition(y)), radius, mineralReserves));
                    }
                }
            }
            image.flush();
        }
        
        return map;
    }
    
    private static int getPixelPosition(int unitPosition)
    {
        return unitPosition * UNIT_SIZE + UNIT_SIZE / 2;
    }
}
