package spacewars.game.model.buildings;

import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class SolarStation extends Building
{
    private static final String NAME = "Solar";
    private static int MAXENERGY = 5;
    protected int energyPerMin = 50;
    
    public SolarStation(Vector position)
    {
        super(position, 15, 100, 100);
    }

    @Override
    public String getName()
    {
        return NAME;
    }
    
    public int getEnergyPerMin(){
    	return energyPerMin;
    }
    
    public int getMaxEnergy(){
        return MAXENERGY;
    }
    
    @Override
    public void upgrade()
    {
        // TODO Auto-generated method stub
        super.upgrade();
        energyPerMin += 50;
        MAXENERGY += 5;
        costs += 50;
    }
}