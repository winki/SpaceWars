package spacewars.network;

import spacewars.game.model.GameState;

public interface IServer
{
    public byte[] testNetworkSpeed(int bytes);
    
    public GameState getGameState(ClientInput input);
    
    public int register(String ID);
}
