package spacewars.game;

import java.awt.Color;
import java.awt.image.BufferedImage;
import spacewars.game.model.Map;
import spacewars.util.Ressources;

public class MapFactory
{
    /**
     * Loads a map from a path.
     * 
     * @param path
     *        the path relative to the directory "/res/maps/"
     * @return the loaded map
     */
    public static Map loadMap(String path)
    {
        Map map = null;
        BufferedImage image = Ressources.loadBufferedImage("../maps/" + path);
        if (image != null)
        {
            map = new Map(image.getWidth(), image.getHeight());
            for (int x = 0; x < image.getWidth(); x++)
            {
                for (int y = 0; y < image.getHeight(); y++)
                {
                    Color color = new Color(image.getRGB(x, y));
                    if (color.getRed() == 255 && color.getGreen() == 0 && color.getBlue() == 0)
                    {
                        // home planet position
                        map.addHomePlanetPosition(x, y);
                    }
                    else if (color.getGreen() > 0 && color.getRed() == 0 && color.getBlue() == 0)
                    {
                        // mineral planet
                        int mineralReserves = (100 * color.getGreen()) / 255;
                        map.addMineralPlanet(x, y, mineralReserves);
                    }
                }
            }
            image.flush();
        }
        return map;
    }
}
