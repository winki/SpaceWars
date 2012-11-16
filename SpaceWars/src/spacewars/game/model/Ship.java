package spacewars.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Random;
import spacewars.game.SpaceWars;
import spacewars.game.model.planets.HomePlanet;
import spacewars.gamelib.GameTime;
import spacewars.gamelib.IUpdateable;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class Ship extends GameElement implements IUpdateable
{
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
        // direction: enemy's home planet
        final HomePlanet target = SpaceWars.getInstance().getGameState().getPlayers().get(1).getHomePlanet();
        final Vector dir = target.getPosition().sub(position);
        
        // flight in the direction of the vector
        final double targetangle = Math.atan((double) dir.y / dir.x);
        
        // angle difference
        anglediff = targetangle - angle;
        while (anglediff < 0)
        {
            anglediff += (2 * Math.PI);
        }
        anglediff %= (2 * Math.PI);
        
        final int TURN_SLOWMO = 300;
        if (anglediff > Math.PI)
        {
            // turn left
            angle -= anglediff / TURN_SLOWMO;
        }
        else
        {
            // turn right
            angle += anglediff / TURN_SLOWMO;
        }
        
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
}
