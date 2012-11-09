package spacewars.gamelib;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.List;
import java.util.Vector;

import javax.swing.*;

import spacewars.game.SpaceWars;
import spacewars.game.model.BuildingType;
import spacewars.game.model.GameElement;
import spacewars.game.model.GameState;
import spacewars.util.Ressources;

public class Screen
{
    private static Screen  	instance;
    
    private boolean        	debug;
    private Game           	owner;
    private final JFrame   	frame;
    private final Viewport 	viewport;
    private BuildingType 	buildingType;
    
    private Screen()
    {
        this.viewport = new Viewport();
        
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
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
                String building = "";
            	Graphics2D g = (Graphics2D) graphics;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (owner != null)
                {
                    owner.render(g);
                    
                    if (isDebug())
                    {
                        if (!SpaceWars.getInstance().getBuildingType().equals("")){
                        	building = SpaceWars.getInstance().getBuildingType().toString();
                        }
                        //draw framerate
                        g.setColor(Color.WHITE);
                        g.drawString("FPS: " + owner.getGameTime().getFrameRate(), 10, 20);
                        
                        g.drawString("Buildingtype: " + building, 160, 40);
                    }
                    
                    //problem weil ich einen neuen gamestate erzeuge?!
                    
                    GameState state = new GameState();
                    List<GameElement> elements = state.getGameElements();
                    if(elements.size() >0 ){
                    	spacewars.gamelib.geometrics.Vector position = elements.get(0).getPosition();
                        g.setColor(Color.white);
                    	g.drawRect(position.x, position.y, 100, 100);
                    }
                    
                }
            }
        });
        setSize(null);
    }
    
    protected void register(Game owner)
    {
        this.owner = owner;
    }
    
    public static Screen getInstance()
    {
        if (instance == null)
        {
            instance = new Screen();
        }
        return instance;
    }
    
    public boolean isDebug()
    {
        return debug;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
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
    
    public Dimension getSize()
    {
        return frame.getContentPane().getSize();
    }
    
    public Viewport getViewport()
    {
        return viewport;
    }
    
    public void setSize(Dimension size)
    {
        final boolean visibility = frame.isVisible();
        
        frame.dispose();
        
        if (size != null)
        {
            frame.setUndecorated(false);
            frame.setExtendedState(JFrame.NORMAL);
            frame.getContentPane().setPreferredSize(size);
            frame.getContentPane().setSize(size);
            frame.pack();
            frame.setLocationRelativeTo(null);
        }
        else
        {
            size = Toolkit.getDefaultToolkit().getScreenSize();
            
            frame.setUndecorated(true);
            frame.getContentPane().setPreferredSize(size);
            frame.getContentPane().setSize(size);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        
        viewport.setSize(size.width, size.height);
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
    
    public void render()
    {
        frame.repaint();
    }
}
