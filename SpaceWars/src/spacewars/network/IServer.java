package spacewars.network;

import spacewars.game.model.GameState;
import spacewars.game.model.buildings.Building;

public interface IServer
{
   /**
    * Client can register itself at the server. Returns a player object.
    * 
    * @param client the client callback interface
    * @param name the players name
    * @return player id
    */
   int register(IClient client, String name);
   
   /**
    * Build a building.
    * 
    * @param building the building
    */
   void build(Building building);
   
   /**
    * Upgrade a building.
    * 
    * @param building the building
    */
   void upgrade(Building building);
   
   /**
    * Recycle a building.
    * 
    * @param building the building
    */
   void recycle(Building building);
   
   /**
    * Gets the current game state from the server.
    * 
    * @return the game state
    */
   GameState getGameState();
}
