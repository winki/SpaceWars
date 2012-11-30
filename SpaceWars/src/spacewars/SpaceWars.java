package spacewars;

import spacewars.game.ClientGame;
import spacewars.network.Network;

public class SpaceWars
{
   private static final boolean DEBUG = true;
   
   /**
    * This method will be called if we are testing.
    */
   
   public static void print(int v, int w, String s1, String s2){
      for(int y = 0; y < v * w; y++){
         for(int x = 0; x<v*w;x++){
            System.out.println((x/w)%2==0 || (y/w)%2 ==0 ? s1:s2);
         }
         System.out.println();
      }
   }
   
   private static void testing()
   {
      print(8, 4, " ", "#");
      
      //ClientGame.getInstance().run();
      
      
      /*
       Network.runServer();
       System.out.println("hello");
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
    * <code>spacewars.jar 127.0.0.1</code>
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
            Network.runServer();
         }
         else
         {
            // start client and connect to given ip address
            String serverIp = args[0];
            Network.runClient(serverIp);
         }
      }
      else
      {
         // start server and connect to it
         Network.runServer();
         
         String serverIp = "127.0.0.1";
         Network.runClient(serverIp);
      }
   }
}
