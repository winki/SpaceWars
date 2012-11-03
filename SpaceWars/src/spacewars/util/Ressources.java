package spacewars.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Ressources
{
    public static final String PATH = "res/";
    
    public static Image loadImage(String path)
    {
        return Toolkit.getDefaultToolkit().getImage(PATH + "img/" + path);        
    } 
    
    public static BufferedImage loadBufferedImage(String path)
    {
        try
        {
            return ImageIO.read(new File(PATH + "img/" + path));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    } 
}
