package spacewars.game.model.buildings;

import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class SolarStation extends Building
{
    private static final String NAME = "Solar";
    
    protected int energyPerMin = 100;
    
    public SolarStation(Vector position)
    {
        super(position, 15, 100, 100);
        this.energyPerMin = level * energyPerMin;
    }

    @Override
    public String getName()
    {
        return NAME;
    }
    
    public int getEnergyPerMin(){
    	return energyPerMin;
    }
}