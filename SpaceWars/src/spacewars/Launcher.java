package spacewars;

import spacewars.network.Network;

public class Launcher
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        //Network.runServer();
        //Network.runClient("127.0.0.1");
    }
    
    public static void run(String[] args)
    {
        if (args.length > 0)
        {
            // start client
            Network.runClient(args[0]);
        }
        
        // start server
        Network.runServer();
    }
}
