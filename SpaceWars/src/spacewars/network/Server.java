package spacewars.network;

import de.root1.simon.annotation.SimonRemote;

@SimonRemote(value = { IServer.class })
public class Server implements IServer
{
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
}
