package spacewars.game.model.buildings;

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
}
