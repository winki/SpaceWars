package spacewars.game;

import java.awt.Graphics2D;
import java.util.logging.Logger;
import spacewars.gamelib.GameClient;
import spacewars.gamelib.GameTime;
import spacewars.network.IClient;
import de.root1.simon.annotation.SimonRemote;

@SimonRemote(value = { IClient.class })
public class Client extends GameClient implements IClient
{
   @Override
   public void callback(String text)
   {
      Logger.getGlobal().info(String.format("This message was received from the server: %s", text));
   }

   @Override
   protected void initialize()
   {
      // TODO Auto-generated method stub
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      // TODO Auto-generated method stub      
   }
   
   @Override
   public void render(Graphics2D g)
   {
      // TODO Auto-generated method stub      
   }

   @Override
   protected void sync()
   {
      // TODO Auto-generated method stub      
   }
}
