package spacewars.network;

import java.awt.Color;
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
   
   private String[] clients = new String[2];
   
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
   
   public int register(String ID)
   {
      if (clients[0].equals(""))
      {
         clients[0] = ID;
         return 1;
      }
      else if (clients[1].equals(""))
      {  
         clients[1] = ID;
         return 2;
      }else{
         return 99;
      }
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
         gameState.getPlayers().add(new Player(i, Color.WHITE, new Vector(1, 2)));
      }
      
      // 500 buildings
      for (int i = 0; i < 500; i++)
      {
         gameState.getBuildings().add(new Mine(gameState.getPlayers().get(0), new Vector(1, 4)));
      }
      
      // 100 ships
      for (int i = 0; i < 100; i++)
      {
         gameState.getShips().add(new Ship(gameState.getPlayers().get(0), new Vector(3454, 345), 0.5));
      }
      
      return gameState;
   }
}
