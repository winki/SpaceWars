package spacewars.game.model.buildings;

import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class Relay extends Building
{
    private static final String NAME = "Relay";
    
    public Relay(Vector position)
    {
        super(position, 5, 100, 200);
    }
    
    @Override
    public String getName()
    {
        return NAME;
    }
}
