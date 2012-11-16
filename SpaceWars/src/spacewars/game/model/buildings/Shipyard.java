package spacewars.game.model.buildings;

import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class Shipyard extends Building
{
    private static final String NAME  = "Shipyard";
    
    public Shipyard(Vector position)
    {
        super(position, 15, 200, 500);
    }
    
    @Override
    public String getName()
    {
        return NAME;        
    }
}
