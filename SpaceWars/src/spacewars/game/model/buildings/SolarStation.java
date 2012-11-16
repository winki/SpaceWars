package spacewars.game.model.buildings;

import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class SolarStation extends Building
{
    private static final String NAME = "Solar";
    
    protected int energyPerMin = 100;
    protected int maxEnergy = 10;
    
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
    
    public int getMaxEnergy()
    {
        return maxEnergy;
    }
    
    public void setMaxEnergy(int maxEnergy)
    {
        this.maxEnergy = maxEnergy;
    }
    
    
    @Override
    public void upgrade()
    {
        // TODO Auto-generated method stub
        super.upgrade();
    }
    
}