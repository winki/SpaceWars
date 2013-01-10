package spacewars.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import spacewars.SpaceWars;

public class Ressources
{
   public static final String               PATH_RES   = "res/";
   
   public static final String               PATH_IMG   = "img/";
   public static final String               PATH_FONT  = "font/";
   public static final String               PATH_SOUND = "sound/";
   
   private static final Map<String, Object> cache      = new HashMap<>();
   
   /**
    * Gets the path, where the runnable jar file is located.
    * 
    * @return location of the runnable jar file
    */
   public static String getJarPath()
   {
      try
      {
         final String path = ClassLoader.getSystemClassLoader().getResource(".").getPath();
         return URLDecoder.decode(path, "UTF-8");
      }
      catch (UnsupportedEncodingException ex)
      {
         Logger.getGlobal().throwing(Ressources.class.getName(), "getJarPath", ex);
      }
      return null;
   }
   
   /**
    * Gets the path to the game ressources. In the runnable jar file the
    * ressources are in the root folder of the jar file. If developing in
    * eclipse, the ressources are in the root folder of the project.
    * 
    * @return path to the game ressources
    */
   public static String getRessourcesPath(String path)
   {
      if (SpaceWars.RELEASE_VERSION) return getJarPath() + path;
      else return path;
   }
   
   /**
    * Loads an <code>Image</code> from the path relative to the directory
    * "res/img/". The image will be cached in memory.
    * 
    * @param path the path to the image
    * @return the image
    */
   public static Image loadImage(String path)
   {
      path = getRessourcesPath(PATH_RES + PATH_IMG + path);
      if (!cache.containsKey(path))
      {
         Image image = Toolkit.getDefaultToolkit().getImage(path);
         
         cache.put(path, image);
         return image;
      }
      else
      {
         return (Image) cache.get(path);
      }
   }
   
   public static Font loadFont(String path, float size)
   {
      path = getRessourcesPath(PATH_RES + PATH_FONT + path);
      if (!cache.containsKey(path))
      {
         try
         {
            // Returned font is of pt size 1
            final Font font = Font.createFont(Font.TRUETYPE_FONT, new File(path));
            
            // Derive and return a 12 pt version:
            // Need to use float otherwise
            // it would be interpreted as style
            final Font sizedFont = font.deriveFont(size);
            
            cache.put(path, sizedFont);
            return sizedFont;
         }
         catch (IOException | FontFormatException ex)
         {
            Logger.getGlobal().throwing(Ressources.class.getName(), "loadFont", ex);
         }
         return null;
      }
      else
      {
         final Font font = (Font) cache.get(path);
         return font.deriveFont(size);
      }
   }
   
   public static Clip loadSound(String path)
   {
      path = getRessourcesPath(PATH_RES + PATH_SOUND + path);
      if (!cache.containsKey(path))
      {
         try
         {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path));
            AudioFormat af = audioInputStream.getFormat();
            int size = (int) (af.getFrameSize() * audioInputStream.getFrameLength());
            byte[] audio = new byte[size];
            audioInputStream.read(audio, 0, size);
            Clip clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, af, size));
            clip.open(af, audio, 0, size);
            cache.put(path, clip);
            return clip;
         }
         catch (Exception ex)
         {
            Logger.getGlobal().throwing(Ressources.class.getName(), "loadSound", ex);
         }
         return null;
      }
      else
      {
         return (Clip) cache.get(path);
      }
   }
   
   /**
    * Loads a <code>BufferedImage</code> from the path relative to the directory
    * "res/img/". The image will be cached in memory.
    * 
    * @param path the path to the image
    * @return the image
    */
   public static BufferedImage loadBufferedImage(String path)
   {
      path = getRessourcesPath(PATH_RES + PATH_IMG + path);
      if (!cache.containsKey(path))
      {
         try
         {
            BufferedImage image = ImageIO.read(new File(path));
            cache.put(path, image);
            return image;
         }
         catch (IOException ex)
         {
            Logger.getGlobal().throwing(Ressources.class.getName(), "loadBufferedImage", ex);
         }
         return null;
      }
      else
      {
         return (BufferedImage) cache.get(path);
      }
   }
}
