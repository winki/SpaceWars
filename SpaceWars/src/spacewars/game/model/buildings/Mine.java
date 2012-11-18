package spacewars.game.model.buildings;

import spacewars.game.model.Player;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class Mine extends Building
{
    private static final String NAME           = "Mine";
    
    protected int               mineralsPerMin = 30;
    private int                 energyConsum   = 6;
    
    public Mine(final Player player, final Vector position)
    {
        super(player, position, 10, 100, 100);
    }
    
    @Override
    public String getName()
    {
        return NAME;
    }
    
    public int getResPerMin()
    {
        
        return super.level * mineralsPerMin;
    }
    
    public int getEnergyConsumPerMin()
    {
        return energyConsum;
    }
    
    @Override
    public void upgrade()
    {
        // TODO Auto-generated method stub
        super.upgrade();
    }
}
