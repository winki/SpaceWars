package spacewars.game.model.buildings;

import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class SolarStation extends Building
{
    private static final String NAME = "Solar";
    
    public SolarStation(Vector position)
    {
        super(position, 15, 100);
    }

    @Override
    public String getName()
    {
        return NAME;
    }
}