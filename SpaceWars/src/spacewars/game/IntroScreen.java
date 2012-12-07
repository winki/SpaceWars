package spacewars.game;

import java.awt.Graphics2D;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.IUpdateable;

public class IntroScreen implements IUpdateable, IRenderable
{
   private boolean visible;
   
   public IntroScreen()
   {      
      this.visible = false;
   }
   
   public boolean isVisible()
   {
      return visible;
   }
   
   public void setVisible(boolean visible)
   {
      this.visible = visible;
   }
   
   @Override
   public void update(GameTime gameTime)
   {
      // TODO: kai
   }

   @Override
   public void render(Graphics2D g)
   {
      // TODO: kai
   }
}
