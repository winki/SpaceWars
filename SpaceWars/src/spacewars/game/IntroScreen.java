package spacewars.game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import javax.sound.sampled.Clip;
import spacewars.game.model.GameState;
import spacewars.game.model.Player;
import spacewars.gamelib.Button;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.IRenderable;
import spacewars.gamelib.IUpdateable;
import spacewars.gamelib.Mouse;
import spacewars.gamelib.Screen;
import spacewars.gamelib.Vector;
import spacewars.util.Ressources;

public class IntroScreen implements IUpdateable, IRenderable
{
   private boolean   visible;
   final Dimension   screen        = Screen.getInstance().getSize();
   
   // Kai: fix this! why do i need to set these? !! with getting the viewport position and calculate the zero point! Vector p = gameState.getMap().getHomePlanetPositions().get(player.getId());
   
   private int       zeroX         = -205;
   private int       zeroY         = 875;
   
   private boolean   firstRound    = true;
   
   // intro
   private int       introWidth    = 1000;
   private int       introHeight   = 700;
   private int       introX        = zeroX + screen.width / 2 - introWidth / 2;
   private int       introY        = zeroY + screen.height / 2 - introHeight / 2;
   private boolean   exitIntro     = false;
   private boolean   flyIn         = true;
   private Dimension spaceSize;
   private Dimension warsSize;
   private int       spaceX        = zeroX - 600;
   private int       spaceY        = introY + 200;
   private int       warsX         = zeroX + screen.width;
   private int       warsY         = introY + 300;
   
   // start button
   private Dimension startSize;
   private int       startX        = introX + introWidth / 2 - 200;
   private int       startY        = introY + introHeight / 2 + 200;
   private float     startFontSize = 40.0f;
   private Color     startColor    = Color.WHITE;
   
   // background
   private float     transparency  = 1.0f;
   
   public IntroScreen()
   {
      this.visible = false;
      Server server = Server.getInstance();
      GameState gameState = server.getGameState();
      /*
      Player player = gameState.getPlayers().get(0);
      
      Vector p = gameState.getMap().getHomePlanetPositions().get(player.getId());
      
      zeroX = p.x - screen.width/2;
      zeroY = p.y - screen.height/2;
      */
      
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
      Clip backgroundClip = Ressources.loadSound("background.wav");
      Clip startButtonClip = Ressources.loadSound("start_button.wav");
      
      // TODO: kai
      if (exitIntro)
      {
         if (transparency >= 0.0f)
         {
            transparency = transparency - 0.04f;
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
            if (spaceX <= introX + 40)
            {
               spaceX += 30;
            }
            if (warsX >= introX + introWidth / 2 - 60)
            {
               warsX -= 30;
            }
            else
            {
               flyIn = false;
            }
            
         }
         else
         {
            if (Mouse.getState().getX() + zeroX >= startX && Mouse.getState().getX() + zeroX <= startX + startSize.getWidth() && Mouse.getState().getY() + zeroY >= startY - startSize.getHeight() && Mouse.getState().getY() + zeroY <= startY)
            {
               startColor = Color.ORANGE;
               
               if (Mouse.getState().isButtonPressed(Button.LEFT))
               {
                  startButtonClip.start();                  
                  exitIntro = true;
               }
            }
            else
            {
               startColor = Color.WHITE;
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
      
      // TODO: kai
      
      // save original font to reset it later
      Font original = g.getFont();
      Font font = Ressources.loadFont("space_age.ttf", startFontSize);
      FontMetrics metrics = g.getFontMetrics(font);
      
      // background
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
      g.setColor(Color.BLACK);
      g.fillRect(zeroX, zeroY, screen.width, screen.height);
      
      // title
      g.setColor(Color.GREEN);
      g.setFont(Ressources.loadFont("space_age.ttf", 150.0f));
      g.drawString("Space", spaceX, spaceY);
      int hgt = metrics.getHeight();
      int adv = metrics.stringWidth("Space");
      spaceSize = new Dimension(adv + 2, hgt + 2);
      g.drawString("Wars", warsX, warsY);
      hgt = metrics.getHeight();
      adv = metrics.stringWidth("Wars");
      warsSize = new Dimension(adv + 2, hgt + 2);
      
      // start
      
      g.setFont(font);
      hgt = metrics.getHeight();
      adv = metrics.stringWidth("Start Game");
      startSize = new Dimension(adv + 2, hgt + 2);
      
      g.setColor(startColor);
      g.drawString("Start game", startX, startY);
      
      
      // reset font
      g.setFont(original);
   }
}
