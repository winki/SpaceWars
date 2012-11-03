package spacewars.gamelib;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JFrame;
import spacewars.util.Ressources;

public class Screen
{
    private static Screen instance;
    
    Game          owner;
    final JFrame  frame;
    
    int           framerate;
    
    private Screen()
    {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        frame.setBackground(Color.BLACK);
        frame.addKeyListener(Keyboard.getListener());
        frame.setContentPane(new JComponent()
        {
            /**
             * Serial version
             */
            private static final long serialVersionUID = 1L;
            
            /**
             * Anonymous constructor
             */
            {
                this.setOpaque(true);
                this.addMouseListener(Mouse.getListener());
                this.addMouseMotionListener(Mouse.getListener());
                this.addMouseWheelListener(Mouse.getListener());
            }
            
            public void paintComponent(Graphics graphics)
            {
                Graphics2D g = (Graphics2D) graphics;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (owner != null)
                {
                    owner.render(g);
                }
                
                // draw framerate
                g.setColor(Color.WHITE);
                g.drawString("Framerate: " + framerate + " fps", 10, 20);                
            }
        }); 
        frame.setResizable(false);
        frame.setPreferredSize(new Dimension(800, 600));
        frame.pack();
        frame.setLocationRelativeTo(null);
    }
    
    public static Screen getInstance()
    {
        if (instance == null)
        {
            instance = new Screen();
        }
        return instance;
    }
    
    void setOwner(Game owner)
    {
        this.owner = owner;
    }

    public void setTitle(String title)
    {
        frame.setTitle(title);
    }
    
    public void setIcon(String path)
    {
        Image image = Ressources.loadImage(path);
        frame.setIconImage(image);
    }
    
    public void setSize(Dimension size)
    {
        final boolean visibility = frame.isVisible();
        
        frame.dispose();
        
        if (size != null)
        {
            frame.setUndecorated(false);
            frame.setExtendedState(JFrame.NORMAL);
            frame.setPreferredSize(size);
            frame.pack();
            frame.setLocationRelativeTo(null);
        }
        else
        {
            frame.setUndecorated(true);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        
        frame.setVisible(visibility);
    }
    
    public void setVisible(boolean visibility)
    {
        frame.setVisible(visibility);
    }
    
    public void setCursor(String path, int x, int y)
    {
        if (path != null)
        {
            Image cursor = Ressources.loadImage(path);
            if (cursor != null)
            {
                frame.setCursor(frame.getToolkit().createCustomCursor(cursor, new Point(x, y), path));
            }
            else
            {
                frame.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    public void render(int framerate)
    {
        this.framerate = framerate;
        frame.repaint();
    }
}
