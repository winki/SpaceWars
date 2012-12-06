package spacewars.network;

import spacewars.game.model.GameState;

public interface IClient
{
   public void updateGameState(GameState gameState);
   
   public void callback(String text);
}
