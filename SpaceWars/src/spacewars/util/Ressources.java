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
    public static final String                     PATH_RES       = "res/";
    public static final String                     PATH_IMG       = "img/";
    
    public static final Map<String, Image>         images         = new HashMap<>();
    public static final Map<String, BufferedImage> bufferedImages = new HashMap<>();
    
    /**
     * Loads an <code>Image</code> from the path relative to the directory
     * "res/img/".
     * 
     * @param path the path to the image
     * @return the image
     */
    public static Image loadImage(String path)
    {
        if (!images.containsKey(path))
        {
            Image image = Toolkit.getDefaultToolkit().getImage(PATH_RES + PATH_IMG + path);
            images.put(path, image);
            return image;
        }
        else
        {
            return images.get(path);
        }
    }
    
    /**
     * Loads a <code>BufferedImage</code> from the path relative to the
     * directory "res/img/".
     * 
     * @param path the path to the image
     * @return the image
     */
    public static BufferedImage loadBufferedImage(String path)
    {
        if (!bufferedImages.containsKey(path))
        {
            try
            {
                BufferedImage image = ImageIO.read(new File(PATH_RES + PATH_IMG + path));
                bufferedImages.put(path, image);
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
            return bufferedImages.get(path);
        }
    }
}
