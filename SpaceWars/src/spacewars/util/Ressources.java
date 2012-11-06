package spacewars.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Ressources
{
    public static final String PATH_RES = "res/";
    public static final String PATH_IMG = "img/";
    
    /**
     * Loads an <code>Image</code> from the path relative to the directory
     * "res/img/".
     * 
     * @param path the path to the image
     * @return the image
     */
    public static Image loadImage(String path)
    {
        return Toolkit.getDefaultToolkit().getImage(PATH_RES + PATH_IMG + path);
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
        try
        {
            return ImageIO.read(new File(PATH_RES + PATH_IMG + path));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
}
