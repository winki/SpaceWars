package spacewars.game.model.buildings;

import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class Mine extends Building
{
    private static final String NAME = "Mine";
    
    public Mine(Vector position)
    {
        super(position, 10, 100);
    }
    
    @Override
    public String getName()
    {
        return NAME;
    }
}
