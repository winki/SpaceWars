package spacewars.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class Ressources
{
    public static final String               PATH_RES = "res/";
    public static final String               PATH_IMG = "img/";
    
    private static final Map<String, Object> cache    = new HashMap<>();
    
    /**
     * Loads an <code>Image</code> from the path relative to the directory
     * "res/img/". The image will be cached in memory.
     * 
     * @param path the path to the image
     * @return the image
     */
    public static Image loadImage(String path)
    {
        if (!cache.containsKey(path))
        {
            Image image = Toolkit.getDefaultToolkit().getImage(PATH_RES + PATH_IMG + path);
            cache.put(path, image);
            return image;
        }
        else
        {
            return (Image) cache.get(path);
        }
    }
    
    /**
     * Loads a <code>BufferedImage</code> from the path relative to the
     * directory "res/img/". The image will be cached in memory.
     * 
     * @param path the path to the image
     * @return the image
     */
    public static BufferedImage loadBufferedImage(String path)
    {
        if (!cache.containsKey(path))
        {
            try
            {
                BufferedImage image = ImageIO.read(new File(PATH_RES + PATH_IMG + path));
                cache.put(path, image);
                return image;
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            return null;
        }
        else
        {
            return (BufferedImage) cache.get(path);
        }
    }
}
