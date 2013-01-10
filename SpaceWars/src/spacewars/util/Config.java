package spacewars.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.InputSource;
import spacewars.SpaceWars;

public class Config
{
   public static final String               PATH_CONFIG      = "config/";
   public static final String               FILE_CONFIG      = "config.xml";
   public static final String               ROOT_NODE        = "/SpaceWars/";
   private static final String              ARRAY_SEPARARTOR = ",";
   
   private static final Map<String, Object> cache            = new HashMap<>();
   private static final XPath               xpath            = XPathFactory.newInstance().newXPath();
   private static final InputSource         source;
   
   static
   {
      if (SpaceWars.RELEASE_VERSION) source = new InputSource(Ressources.getJarPath() + Ressources.PATH_RES + PATH_CONFIG + FILE_CONFIG);
      else source = new InputSource(Ressources.PATH_RES + PATH_CONFIG + FILE_CONFIG);
   }
   
   /**
    * Gets a <code>String</code> value from the config file.
    * 
    * @param expression xpath expression
    * @return <code>String</code> config value
    */
   public static String getString(final String expression)
   {
      String value = (String) cache.get(expression);
      if (value == null)
      {
         value = get(expression);
         cache.put(expression, value);
      }
      return value;
   }
   
   /**
    * Gets a <code>boolean</code> value from the config file.
    * 
    * @param expression xpath expression
    * @return <code>boolean</code> config value
    */
   public static boolean getBool(final String expression)
   {
      Boolean value = (Boolean) cache.get(expression);
      if (value == null)
      {
         value = Boolean.parseBoolean(get(expression));
         cache.put(expression, value);
      }
      return value;
   }
   
   /**
    * Gets a <code>int</code> value from the config file.
    * 
    * @param expression xpath expression
    * @return <code>int</code> config value
    */
   public static int getInt(final String expression)
   {
      Integer value = (Integer) cache.get(expression);
      if (!cache.containsKey(expression))
      {
         value = Integer.parseInt(get(expression));
         cache.put(expression, value);
      }
      return value;
   }
   
   /**
    * Gets a <code>double</code> value from the config file.
    * 
    * @param expression xpath expression
    * @return <code>double</code> config value
    */
   public static double getDouble(final String expression)
   {
      Double value = (Double) cache.get(expression);
      if (value == null)
      {
         value = Double.parseDouble(get(expression));
         cache.put(expression, value);
      }
      return value;
   }
   
   /**
    * Gets a <code>int[]</code> value from the config file.
    * 
    * @param expression xpath expression
    * @return <code>int[]</code> config value
    */
   public static int[] getIntArray(final String expression)
   {
      int[] value = (int[]) cache.get(expression);
      if (value == null)
      {
         String[] str = get(expression).split(ARRAY_SEPARARTOR);
         value = new int[str.length];
         for (int i = 0; i < value.length; i++)
         {
            value[i] = Integer.parseInt(str[i]);
         }
         cache.put(expression, value);
      }
      return value;
   }
   
   /**
    * Gets a <code>Color</code> value from the config file.
    * 
    * @param expression xpath expression
    * @return <code>Color</code> config value
    */
   public static Color getColor(final String expression)
   {
      Color value = (Color) cache.get(expression);
      if (value == null)
      {
         value = new Color(Integer.decode(get(expression)));
         cache.put(expression, value);
      }
      return value;
   }
   
   private static String get(final String expression)
   {
      try
      {
         return xpath.evaluate(ROOT_NODE + expression, source);
      }
      catch (XPathExpressionException ex)
      {
         Logger.getGlobal().throwing(Ressources.class.getName(), "get", ex);
      }
      return null;
   }
}
