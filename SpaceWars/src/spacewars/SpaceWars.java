package spacewars;

import java.util.logging.Level;
import java.util.logging.Logger;
import spacewars.game.SpaceWarsGame;

public class SpaceWars
{
   private static final boolean DEBUG = true;
   
   /**
    * This method will be called if we are testing.
    */
   private static void testing()
   {
      // set logging
      Logger.getGlobal().setLevel(Level.ALL);
      
      // run game
      SpaceWarsGame.getInstance().run();
      
      /*
       Network.runServer();
       Network.runClient("127.0.0.1");
       */
   }
   
   /**
    * If the program is started with the parameter "serveronly" then only the
    * server will be started.
    * <p>
    * <code>spacewars.jar serveronly</code>
    * <p>
    * If the program is started with the ip from the server as parameter then a
    * client will be started that will connect to the server.
    * <p>
    * <code>spacewars.jar 192.168.30.100</code>
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
      if (DEBUG)
      {
         testing();
         System.exit(0);
      }
      
      if (args.length > 0)
      {
         if (args[0].equals("serveronly"))
         {
            // only start server
            runServer();
         }
         else
         {
            // start client and connect to given ip address
            runClient(args[0]);
         }
      }
      else
      {
         // start server and connect to it
         runServer();
         runClient("127.0.0.1");
      }
   }
   
   /**
    * Run a server instance.
    */
   private static void runServer()
   {
      // TODO: run server
   }
   
   /**
    * Run a client instance that connects to the server.
    * 
    * @param serverAddress server address
    */
   private static void runClient(String serverAddress)
   {
      // TODO: run client, connect to server
   }
}
