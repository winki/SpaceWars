package spacewars.game.model.buildings;

import spacewars.game.model.Player;
import spacewars.game.model.planets.MineralPlanet;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class Mine extends Building
{
    private static final String NAME           = "Mine";
    /**
     * The range in which the mine can collect minerals
     */
    protected int               mineRange      = sight / 2;
    protected int               mineralsPerMin = 60;
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
    
    public int getMineRange()
    {
        return mineRange;
    }
    
    public boolean canMine(MineralPlanet planet)
    {
        return position.distance(planet.getPosition()) - planet.getSizeRadius() < getMineRange();
    }
}
