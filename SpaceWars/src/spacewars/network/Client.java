package spacewars.network;

import de.root1.simon.annotation.SimonRemote;

@SimonRemote(value = {IClient.class}) 
public class Client implements IClient
{
    @Override
    public void callback(String text)
    {
        System.out.printf("This message was received from the server: %s", text);
    }  
}
