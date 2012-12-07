package spacewars.network;

import spacewars.game.model.GameState;
import spacewars.game.model.buildings.Building;

public interface IServer
{
   /**
    * Client can register itself at the server.
    * 
    * @param client client callback object
    */
   void register(IClient client);
   
   void build(Building building);
   
   void upgrade(Building building);
   
   void recycle(Building building);

   /**
    * Gets the current game state from the server.
    * 
    * @return the game state
    */
   GameState getGameState();
}
