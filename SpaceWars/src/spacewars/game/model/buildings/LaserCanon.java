package spacewars.game.model.buildings;

import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class LaserCanon extends Building
{
    private static final String NAME = "Laser";
    
    public int                  power;
    public int                  health;
    
    public LaserCanon(Vector position)
    {
        super(position, 15, 200, 300);
    }
    
    @Override
    public String getName()
    {
        return NAME;
    }
}
