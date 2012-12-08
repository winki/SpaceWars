package spacewars.util;

import java.awt.Font;
import java.awt.FontFormatException;
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
    
    public static Font getFont(String path){
       try {
          //Returned font is of pt size 1
          Font font = Font.createFont(Font.TRUETYPE_FONT, new File(PATH_RES + path));

          //Derive and return a 12 pt version:
          //Need to use float otherwise
          //it would be interpreted as style

          return font.deriveFont(12f);

     } catch (IOException|FontFormatException e) {
          // Handle exception
     }
       return null;
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
