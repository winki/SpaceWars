package spacewars.game.model.buildings;

import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class SolarStation extends Building
{
    private static final String NAME = "Solar";
    private static int[] maxEnergy = {10, 12, 15, 20, 27};
    
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
}
