package spacewars.network;

import spacewars.game.model.GameState;

public interface IServer
{
   public void login();
   
   /**
    * Test method.
    * 
    * @param bytes number of bytes to receive
    * @return array of random bytes
    */
   public byte[] getBytes(int bytes);
   
   public GameState getInitialGameState();
}
