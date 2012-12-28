package spacewars.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Helpers
{
   /**
    * Creates a deep copy of an object.
    * 
    * @param object object to copy
    * @return copy of the object
    */
   @SuppressWarnings("unchecked")
   public static <T> T deepCopy(T object)
   {
      try
      {
         ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
         new ObjectOutputStream(byteOutput).writeObject(object);
         return (T) new ObjectInputStream(new ByteArrayInputStream(byteOutput.toByteArray())).readObject();
      }
      catch (ClassNotFoundException | IOException ex)
      {
         Logger.getGlobal().log(Level.SEVERE, "Error on maiking deep copy of object.", ex);
         return null;
      }
   }
}
