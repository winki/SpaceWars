package spacewars.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.InputSource;

public class Config
{
   public static final String               PATH_CONFIG      = "config/";
   public static final String               FILE_CONFIG      = "config.xml";
   public static final String               ROOT_NODE        = "/SpaceWars/";
   private static final String              ARRAY_SEPARARTOR = ",";
   
   private static final Map<String, Object> cache            = new HashMap<>();
   private static final XPath               xpath            = XPathFactory.newInstance().newXPath();
   private static final InputSource         source           = new InputSource(Ressources.PATH_RES + PATH_CONFIG + FILE_CONFIG);
   
   /**
    * Gets a <code>String</code> value from the config file.
    * 
    * @param expression xpath expression
    * @return <code>String</code> config value
    */
   public static String getStringValue(final String expression)
   {
      if (!cache.containsKey(expression))
      {
         String value = getValue(expression);
         cache.put(expression, value);
         return value;
      }
      else
      {
         return (String) cache.get(expression);
      }
   }
   
   /**
    * Gets a <code>boolean</code> value from the config file.
    * 
    * @param expression xpath expression
    * @return <code>boolean</code> config value
    */
   public static boolean getBooleanValue(final String expression)
   {
      if (!cache.containsKey(expression))
      {
         boolean value = Boolean.parseBoolean(getValue(expression));
         cache.put(expression, value);
         return value;
      }
      else
      {
         return (boolean) cache.get(expression);
      }
   }
   
   /**
    * Gets a <code>int</code> value from the config file.
    * 
    * @param expression xpath expression
    * @return <code>int</code> config value
    */
   public static int getIntValue(final String expression)
   {
      if (!cache.containsKey(expression))
      {
         int value = Integer.parseInt(getValue(expression));
         cache.put(expression, value);
         return value;
      }
      else
      {
         return (int) cache.get(expression);
      }
   }
   
   /**
    * Gets a <code>double</code> value from the config file.
    * 
    * @param expression xpath expression
    * @return <code>double</code> config value
    */
   public static double getDoubleValue(final String expression)
   {
      if (!cache.containsKey(expression))
      {
         double value = Double.parseDouble(getValue(expression));
         cache.put(expression, value);
         return value;
      }
      else
      {
         return (double) cache.get(expression);
      }
   }
   
   /**
    * Gets a <code>int[]</code> value from the config file.
    * 
    * @param expression xpath expression
    * @return <code>int[]</code> config value
    */
   public static int[] getIntArrayValue(final String expression)
   {
      if (!cache.containsKey(expression))
      {
         String[] str = getValue(expression).split(ARRAY_SEPARARTOR);
         int[] value = new int[str.length];
         for (int i = 0; i < value.length; i++)
         {
            value[i] = Integer.parseInt(str[i]);
         }
         cache.put(expression, value);
         return value;
      }
      else
      {
         return (int[]) cache.get(expression);
      }
   }
   
   private static String getValue(final String expression)
   {
      try
      {
         return xpath.evaluate(ROOT_NODE + expression, source);
      }
      catch (XPathExpressionException ex)
      {
         Logger.getGlobal().throwing(Ressources.class.getName(), "getValue", ex);
      }
      return null;
   }
}
