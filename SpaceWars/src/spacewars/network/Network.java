package spacewars.network;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import de.root1.simon.Lookup;
import de.root1.simon.Registry;
import de.root1.simon.Simon;
import de.root1.simon.SimonPublication;
import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;
import de.root1.simon.exceptions.NameBindingException;

public class Network
{
    private static final int    PORT          = 2222;
    private static final String REGISTRY_NAME = "server";
    
    public static void runServer()
    {
        // create the serverobject
        Server server = new Server();
        
        try
        {
            // create the server's registry
            Registry registry;
            registry = Simon.createRegistry(PORT);
            
            // bind the serverobject to the registry
            registry.bind(REGISTRY_NAME, server);
            
            System.out.println("Server up and running!");
        }
        catch (IOException | NameBindingException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public static void searchGames()
    {
        List<SimonPublication> games = Simon.searchRemoteObjects(100000);
        System.out.println("Anzahl gefundener Spiele: ");
        for (SimonPublication game : games)
        {
            System.out.printf(" - %10s %10s %10s \n", game.getAddress(), game.getPort(), game.getRemoteObjectName());
        }
    }
    
    public static void runClient(String serverAddress)
    {
        // create a callback object
        //Client client = new Client();
        
        // 'lookup' the server object
        Lookup nameLookup = null;
        IServer server = null;
        
        try
        {
            nameLookup = Simon.createNameLookup(serverAddress, PORT);
            server = (IServer) nameLookup.lookup(REGISTRY_NAME);
            
            // use the serverobject as it would exist on your local machine
            
            // first call (longer)
            server.testNetworkSpeed(0);
            
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
        }
        catch (LookupFailedException | EstablishConnectionFailed | UnknownHostException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            // and finally 'release' the serverobject to release to connection
            // to
            // the server
            if (nameLookup != null && server != null)
            {
                nameLookup.release(server);
            }
        }
    }
    
    private static void testNetworkSpeed(IServer server, int bytes)
    {
        final long start = System.currentTimeMillis();
        server.testNetworkSpeed(bytes);
        System.out.printf("%5d ms for %10d bytes\n", (int) (System.currentTimeMillis() - start), bytes);
    }
}
