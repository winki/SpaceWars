package spacewars.network;

public interface IClient
{
   /**
    * Is called from the server to tell the client that the game has been
    * started.
    */
   void startGame();
}
