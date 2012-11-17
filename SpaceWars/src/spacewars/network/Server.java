package spacewars.network;

import spacewars.game.model.GameState;
import spacewars.game.model.Map;
import spacewars.game.model.Player;
import spacewars.game.model.Ship;
import spacewars.game.model.buildings.Mine;
import spacewars.gamelib.geometrics.Vector;
import de.root1.simon.annotation.SimonRemote;

@SimonRemote(value = { IServer.class })
public class Server implements IServer
{
    /*
     * TODO: kai
     * 
     * - GameState minimieren 
     * - Clients können sich beim Server registrieren
     * - Server informiert Clients, wann das Spiel startet
     *
     * - Client holt neuen GameState
     * - Client ruft Methoden auf, wenn er z.B. bauen will, oder upgraden
     * - Server muss im Update loop die Anfragen berücksichtigen
     * 
     */
    
    @Override
    public byte[] testNetworkSpeed(int bytes)
    {
        byte[] data = new byte[bytes];
        for (int i = 0; i < data.length; i++)
        {
            data[i] = (byte) i;
        }
        return data;
    }
    
    @Override
    public GameState getGameState(ClientInput input)
    {
        Map map = new Map(100, 100, 500, 10);
        map.getHomePlanetPositions().add(new Vector(1, 2));
        map.getHomePlanetPositions().add(new Vector(6, 7));
        
        GameState gameState = new GameState(map);
        
        // 2 players
        for (int i = 0; i < 2; i++)
        {
            gameState.getPlayers().add(new Player(i, map));
        }
        
        // 500 buildings
        for (int i = 0; i < 500; i++)
        {
            gameState.getBuildings().add(new Mine(new Vector(1, 4)));
        }
        
        // 100 ships
        for (int i = 0; i < 100; i++)
        {
            gameState.getShips().add(new Ship(new Vector(3454, 345), 0.5));
        }
        
        return gameState;
    }
}
