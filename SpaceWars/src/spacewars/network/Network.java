package spacewars.network;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import spacewars.util.Config;
import de.root1.simon.Lookup;
import de.root1.simon.Registry;
import de.root1.simon.Simon;
import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;
import de.root1.simon.exceptions.NameBindingException;

public class Network
{
   private static final int    PORT          = Config.getInt("network/port");
   private static final String REGISTRY_NAME = Config.getString("network/registryName");
   
   private static Registry     registry;
   private static Lookup       nameLookup;
   
   /**
    * Binds the server object and make it available for remote clients.
    * 
    * @param server the server object
    * @return <code>true</code> if server was successfully bound
    */
   public static boolean bindServer(IServer server)
   {
      try
      {
         if (registry == null)
         {
            // create the server's registry if necessairy
            registry = Simon.createRegistry(PORT);
         }
         
         // bind the serverobject to the registry
         registry.bind(REGISTRY_NAME, server);
         
         return true;
      }
      catch (IOException | NameBindingException ex)
      {
         Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
      }
      return false;
   }
   
   /**
    * Connects to the server and gets the remote server object of a specified
    * address.
    * 
    * @param serverAddress the server address
    * @return the remote server object
    */
   public static IServer connect(String serverAddress)
   {
      try
      {
         if (nameLookup == null)
         {
            // create a name lookup if necessairy
            nameLookup = Simon.createNameLookup(serverAddress, PORT);
         }
         
         // get the remote object
         return (IServer) nameLookup.lookup(REGISTRY_NAME);
      }
      catch (LookupFailedException | EstablishConnectionFailed | UnknownHostException ex)
      {
         Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
      }
      finally
      {
         // don't disconnect here
         // see disconnect method
      }
      return null;
   }
   
   /**
    * Disconnect from the server and release no more used ressources.
    * 
    * @param server the server object
    */
   public static void disconnect(IServer server)
   {
      if (nameLookup != null && server != null)
      {
         nameLookup.release(server);
      }
   }
}
