package spacewars.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Random;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.IUpdateable;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class Ship extends GameElement implements IUpdateable
{
    protected int        costs;
    protected int        power;
    protected int        health;
    
    protected double     x;
    protected double     y;
    protected double     speed;
    protected double     angle;
    
    protected double     anglediff;
    private final Random random;

    
    public Ship(final Vector position, final double angle)
    {
        super(position, 5, 100);
        this.random = new Random();
        this.x = position.x;
        this.y = position.y;
        this.speed = random.nextDouble() + 0.4;
        this.angle = angle;
    }
    
    @Override
    public void update(GameTime gameTime)
    {
        final double MAX_ANGLE_DIFF = 0.05;
        
        anglediff += (random.nextDouble() - (0.5 + anglediff)) / 100;
        if (anglediff > MAX_ANGLE_DIFF) anglediff = MAX_ANGLE_DIFF;
        angle += anglediff;
        
        x += speed * Math.cos(angle);
        y += speed * Math.sin(angle);
        position.x = (int) x;
        position.y = (int) y;
    }
    
    @Override
    public void render(Graphics2D g)
    {
        final AffineTransform viewport = g.getTransform();
        
        Shape ship = new Polygon(new int[] { position.x + 8, position.x - 4, position.x - 4 }, new int[] { position.y, position.y + 4, position.y - 4 }, 3);
        g.rotate(angle, x, y);
        g.setColor(Color.MAGENTA);
        g.fill(ship);
        
        g.setTransform(viewport);
    }
    
    public void moveTo(Vector vector){
    	
    }
}
