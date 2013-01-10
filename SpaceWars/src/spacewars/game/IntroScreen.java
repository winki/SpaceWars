package spacewars.game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.sound.sampled.Clip;
import spacewars.gamelib.Button;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.IUpdateable;
import spacewars.gamelib.Mouse;
import spacewars.gamelib.Screen;
import spacewars.util.Config;
import spacewars.util.Ressources;

public class IntroScreen implements IUpdateable, IRenderable
{
   private boolean          visible           = false;
   final Dimension          screen            = Screen.getInstance().getSize();
   
   private final Image      background        = Ressources.loadImage("background.png");
   private final Image      title             = Ressources.loadImage("title.png");
   
   private static final int DY_LOGO           = 0;
   
   // = -205;
   private int              zeroX             = 0;
   // = 875;
   private int              zeroY             = 0;
   
   private boolean          firstRound        = true;
   
   // intro
   private int              introWidth        = screen.width;
   private int              introHeight       = screen.height;
   
   private boolean          exitIntro         = false;
   private boolean          flyIn             = true;
   
   private int              spaceX            = -210;
   private int              spaceY            = introHeight / 2 - 35;
   private int              warsX             = introWidth;
   private int              warsY             = introHeight / 2 - 35;
   
   // start button
   private Dimension        startSize;
   private int              startX            = introWidth / 2 - 170;
   private int              startY            = introHeight / 2 + 200;
   private float            startFontSize     = 40.0f;
   
   private Color            startColorNormal  = Color.GRAY;
   private Color            startColorHover   = Color.WHITE;
   private Color            startColor        = startColorNormal;
   
   private int              circle            = 5;
   private boolean          shrink            = false;
   
   // background
   private float            transparency      = 1.0f;
   
   // start button
   private float            startTransparency = 1.0f;
   
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
      Clip backgroundClip = Ressources.loadSound("intro.wav");
      Clip startButtonClip = Ressources.loadSound("laser.wav");
      
      if (exitIntro)
      {
         if (transparency >= 0.0f)
         {
            transparency = transparency - 0.04f;
            if (transparency < 0) transparency = 0;
         }
         else
         {
            setVisible(false);
         }
      }
      else
      {
         // fly in!
         if (flyIn)
         {
            if (firstRound)
            {
               backgroundClip.start();
               firstRound = false;
            }
            // start title animation after a short delay (0.4 sec)
            else if (Client.getInstance().getGameTime().getTotalGameTime() > 400000000)
            {
               final int toFly = (introWidth / 2 - title.getWidth(null) / 2) - spaceX;
               if (toFly > 0)
               {
                  spaceX += toFly / 6 + 1;
                  warsX -= toFly / 6 + 1;
               }
               else
               {
                  flyIn = false;
               }
            }
         }
         else
         {
            if (Mouse.getState().getX() + zeroX >= startX && Mouse.getState().getX() + zeroX <= startX + startSize.getWidth() && Mouse.getState().getY() + zeroY >= startY - startSize.getHeight() && Mouse.getState().getY() + zeroY <= startY)
            {
               startColor = startColorHover;
               
               if (Mouse.getState().isButtonPressed(Button.LEFT))
               {
                  startButtonClip.start();
                  exitIntro = true;
                  
                  // register at server
                  final String playerName = Config.getString("client/playerName");
                  Client.getInstance().registerAtServer(playerName);
               }
            }
            else
            {
               startColor = startColorNormal;
            }
         }
      }
   }
   
   public void setTransparency(float transparency)
   {
      this.transparency = transparency;
   }
   
   @Override
   public void render(Graphics2D g)
   {
      // save original font to reset it later
      Font original = g.getFont();
      Font font = Ressources.loadFont("space_age.ttf", startFontSize);
      FontMetrics metrics = g.getFontMetrics(font);
      
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
      
      // background
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, screen.width, screen.height);
      
      // background image
      final Dimension screen = Screen.getInstance().getSize();
      final int x = (int) (screen.getWidth() - background.getWidth(null)) / 2;
      final int y = (int) (screen.getHeight() - background.getHeight(null)) / 2 + DY_LOGO;
      g.drawImage(background, x, y, null);
      
      // title
      g.drawImage(title, spaceX, spaceY, spaceX + title.getWidth(null) / 2, spaceY + title.getHeight(null), 0, 0, title.getWidth(null) / 2, title.getHeight(null), null);
      g.drawImage(title, warsX, warsY, warsX + title.getWidth(null) / 2, warsY + title.getHeight(null), title.getWidth(null) / 2, 0, title.getWidth(null), title.getHeight(null), null);
      
      // start
      g.setFont(font);
      int hgt = metrics.getHeight();
      int adv = metrics.stringWidth("Start Game");
      startSize = new Dimension(adv + 2, hgt + 2);
      if (!exitIntro)
      {
         if (startColor == startColorHover)
         {
            g.setColor(Color.WHITE);
            
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, startTransparency));
            g.drawOval(startX + startSize.width / 2 - circle / 2, startY - startSize.height / 2 - circle / 2, circle, circle);
            
            if (circle >= 50)
            {
               g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, startTransparency - 0.2f));
               g.drawOval(startX + startSize.width / 2 - (circle - 40) / 2, startY - startSize.height / 2 - (circle - 40) / 2, circle - 40, circle - 40);
            }
            if (circle >= 100)
            {
               g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, startTransparency - 0.3f));
               g.drawOval(startX + startSize.width / 2 - (circle - 90) / 2, startY - startSize.height / 2 - (circle - 90) / 2, circle - 90, circle - 90);
            }
            if (circle >= 150)
            {
               g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, startTransparency - 0.5f));
               g.drawOval(startX + startSize.width / 2 - (circle - 140) / 2, startY - startSize.height / 2 - (circle - 140) / 2, circle - 140, circle - 140);
            }
            if (circle >= 200)
            {
               g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, startTransparency - 0.7f));
               g.drawOval(startX + startSize.width / 2 - (circle - 200) / 2, startY - startSize.height / 2 - (circle - 200) / 2, circle - 200, circle - 200);
            }
            if (circle >= 250)
            {
               g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, startTransparency - 0.9f));
               g.drawOval(startX + startSize.width / 2 - (circle - 240) / 2, startY - startSize.height / 2 - (circle - 240) / 2, circle - 240, circle - 240);
            }
            
            if (circle >= 2 * startSize.width)
            {
               if (circle >= 3 * startSize.width)
               {
                  shrink = true;
               }
               
            }
            if (circle <= 10)
            {
               shrink = false;
            }
            if (shrink)
            {
               circle = 20;
               startTransparency = 1.0f;
               shrink = false;
            }
            else
            {
               circle = circle + 20;
            }
         }
         else
         {
            circle = 5;
         }
      }
      
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
      g.setColor(startColor);
      g.drawString("Start game", startX, startY);
      
      // reset font
      g.setFont(original);
   }
}
