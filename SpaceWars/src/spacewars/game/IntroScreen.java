package spacewars.game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.IUpdateable;
import spacewars.gamelib.Screen;

public class IntroScreen implements IUpdateable, IRenderable
{
   private boolean visible;
   private float transparency = 1.0f;
   
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

  
   public void setTransparency(float transparency){
      this.transparency = transparency;
   }
   

   
   @Override
   public void render(Graphics2D g)
   {
      final Dimension screen = Screen.getInstance().getSize();
      // TODO: kai
      int zeroX = -205;
      int zeroY = 875;
      
      int introWidth = 1000;
      int introHeight = 700;
      
      int introX = zeroX + screen.width/2 - introWidth/2;
      int introY = zeroY + screen.height/2 -introHeight/2;
      
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
      g.setColor(Color.BLACK);
      g.fillRect(zeroX, zeroY, screen.width, screen.height);
      
      
      g.setColor(Color.GREEN);
  
      g.drawString("Space Wars", introX, introY);
      g.drawRect(introX, introY, introWidth, introHeight);
      
      

   }
}
