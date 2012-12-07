package spacewars.network;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.root1.simon.Lookup;
import de.root1.simon.Registry;
import de.root1.simon.Simon;
import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;
import de.root1.simon.exceptions.NameBindingException;

public class Network
{
   private static final int    PORT          = 2222;
   private static final String REGISTRY_NAME = "server";
   
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
   
   /**
    * Do some tests to test the network. Uses the server object as it would
    * exist on the local machine.
    * 
    * @param server the server object
    */
   public static void testNetwork(IServer server)
   {
      // first call (takes longer)
      //server.getBytes(0);
      
      // 1 byte:
      testNetworkSpeed(server, 1);
      // 10 byte:
      testNetworkSpeed(server, 10);
      // 100 byte:
      testNetworkSpeed(server, 100);
      // 1 kilobyte:
      testNetworkSpeed(server, 1024);
      // 10 kilobyte:
      testNetworkSpeed(server, 10 * 1024);
      // 100 kilobyte:
      testNetworkSpeed(server, 100 * 1024);
      // 1 megabyte:
      testNetworkSpeed(server, 1024 * 1024);
      // 10 megabyte:
      testNetworkSpeed(server, 10 * 1024 * 1024);
      
      // get game state
      /*final long start = System.currentTimeMillis();
      GameState gameState = server.getGameState();
      Logger.getGlobal().info(String.format("\nGetting GameState: %5d ms | %s", (int) (System.currentTimeMillis() - start), gameState));
      */
   }   
   
   private static void testNetworkSpeed(IServer server, int bytes)
   {
      final long start = System.currentTimeMillis();
      //server.getBytes(bytes);
      Logger.getGlobal().info(String.format("%5d ms for %10d bytes\n", (int) (System.currentTimeMillis() - start), bytes));
   }
}
