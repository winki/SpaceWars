package spacewars;

import java.util.logging.Level;
import java.util.logging.Logger;
import spacewars.game.Client;
import spacewars.game.Server;
import spacewars.network.IServer;
import spacewars.network.Network;
import spacewars.util.Config;

public class SpaceWars
{
   public static final boolean RELEASE_VERSION = SpaceWars.class.getResource("SpaceWars.class").toString().startsWith("jar:");
   
   /**
    * This method will be called if we are testing.
    */
   private static void testing()
   {
      // set logging
      Logger.getGlobal().setLevel(Level.ALL);
      
      runServer(true);
      runClient("localhost");
   }
   
   /**
    * If the program is started with the parameter "--server" then only the
    * server will be started.
    * <p>
    * <code>spacewars.jar --server</code>
    * <p>
    * If the program is started with the parameter "--client" then a client will
    * be started that will connect to the server.
    * <p>
    * <code>spacewars.jar --client</code>
    * <p>
    * If the program is started with no parameter then first a server will be
    * started and then a client will be started that will connect to the server.
    * <p>
    * <code>spacewars.jar</code>
    * <p>
    * 
    * @param args program arguments
    */
   public static void main(String[] args)
   {
      if (!RELEASE_VERSION)
      {
         testing();
         System.exit(0);
      }
      
      // no logging
      Logger.getGlobal().setLevel(Level.OFF);
      
      if (args.length > 0)
      {
         if (args[0].equals("--server"))
         {
            // only start server
            runServer(false);
         }
         else
         {
            // start client and connect to the host address in the config file
            runClient(Config.getString("network/host"));
         }
      }
      else
      {
         // start server and connect to it
         runServer(true);
         runClient("127.0.0.1");
      }
   }
   
   /**
    * Run a server instance.
    */
   private static void runServer(boolean ownThread)
   {
      final Server server = Server.getInstance();
      Network.bindServer(server);
      
      // run server in his own thread
      if (ownThread)
      {
         new Thread(server).start();
      }
      else
      {
         server.run();
      }
   }
   
   /**
    * Run a client instance that connects to the server.
    * 
    * @param serverAddress server address
    */
   private static void runClient(String serverAddress)
   {
      final IServer server = Network.connect(serverAddress);
      final Client client = Client.getInstance();
      
      client.setServer(server);
      client.run();
   }
}
