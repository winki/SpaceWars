package spacewars;

import spacewars.game.SpaceWarsGame;
import spacewars.gamelib.Game;

public class Launcher
{
    private static final boolean TESTING = true;
    
    /**
     * This method will be only called for testing.
     */
    private static void testing()
    {
        Game game = new SpaceWarsGame();
        game.run();
    }
    
    /**
     * If the program is started with the parameter "serveronly" then only the
     * server will be started. If the program is started with the ip from the
     * server as parameter then a client will be started that will connect to
     * the server. If the program is started with no parameter then first a
     * server will be started and then a client will be started that will
     * connect to the server.
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        if (TESTING)
        {
            testing();
            System.exit(0);
        }
        
        if (args.length > 0)
        {
            if (args[0].equals("serveronly"))
            {
                // TODO: only start server
            }
            else
            {
                // TODO: start client and connect to given ip address
                // String serverIp = args[0];
            }
        }
        else
        {
            // TODO: start server and connect to it
            // String serverIp = "127.0.0.1";
        }
    }
}
