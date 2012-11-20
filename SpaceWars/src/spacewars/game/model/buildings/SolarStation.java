package spacewars.game.model.buildings;

import java.awt.Graphics2D;
import spacewars.game.model.Player;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class SolarStation extends Building
{
    private static final String NAME           = "Solar";
    private static int[]        maxEnergy      = { 0, 12, 15, 20, 27 };
    
    protected int               energyPerMin   = 60;
    private int                 energyReserves = 0;
    
    public SolarStation(final Player player, final Vector position)
    {
        super(player, position, 15, 100, 100);
        this.energyPerMin = level * energyPerMin;
        this.hasEnergy = true;
    }
    
    @Override
    public String getName()
    {
        return NAME;
    }
    
    public int getEnergyPerMin()
    {
        return energyPerMin;
    }
    
    @Override
    public void upgrade()
    {
        // TODO Auto-generated method stub
        super.upgrade();
        energyPerMin += 50;
        costs += 50;
    }
    
    public int getMaxEnergy()
    {
        return maxEnergy[level];
    }
    
    public void update()
    {
        if (energyPerMin / 60 + energyReserves >= maxEnergy[level])
        {
            energyReserves = maxEnergy[level];
        }
        else
        {
            energyReserves += energyPerMin;
        }
    }
    
    public int getEnergy()
    {
        return energyReserves;
    }
    
    @Override
    public void render(Graphics2D g)
    {
        super.render(g);
        
        // TODO: kai
        /*
        if (this.isPlaced() && Mouse.getState().getX() >= p.x - r && Mouse.getState().getX() <= p.x + r && Mouse.getState().getY() >= p.y - r && Mouse.getState().getY() <= p.y + r)
        {                
            g.drawString("Build ship with left click | attack with right click", p.x + r + 20, p.y + 20);
            if (Mouse.getState().isButtonPressed(Button.LEFT))
            {
                if (ships <= 16)
                {
                    ships += 1;
                    innerY = (int) (innerR * Math.sin(2 * Math.PI / 16 * ships));
                    innerX = (int) Math.sqrt(innerR * innerR - innerY * innerY);
                    if (ships <= 4)
                    {
                        ship.setPosition(new Vector(p.x - innerX, p.y - innerY));
                        gameState.getShips().add(ship);
                    }
                    else if (ships <= 8)
                    {
                        ship.setPosition(new Vector(p.x - innerX, p.y + innerY));
                        gameState.getShips().add(ship);
                    }
                    else if (ships <= 12)
                    {
                        ship.setPosition(new Vector(p.x + innerX, p.y + innerY));
                        gameState.getShips().add(ship);
                    }
                    else
                    {
                        ship.setPosition(new Vector(p.x + innerX, p.y - innerY));
                        gameState.getShips().add(ship);
                    }
                }
                else
                {
                    g.drawString("no space left in hangar", p.x, p.y - 40);
                }
            }
            else if (Mouse.getState().isButtonPressed(Button.RIGHT))
            { 
            // send ships to attack!
            }
        }
        */
    }
}
