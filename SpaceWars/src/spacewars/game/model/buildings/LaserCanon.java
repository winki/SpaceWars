package spacewars.game.model.buildings;

import spacewars.game.model.Player;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class LaserCanon extends Building
{
    private static final String NAME = "Laser";
    
    public int                  power;
    public int                  health;
    
    public LaserCanon(final Player player, final Vector position)
    {
        super(player, position, 15, 200, 300);
    }
    
    @Override
    public String getName()
    {
        return NAME;
    }
    
    @Override
    public void upgrade()
    {
        super.upgrade();
        this.power += 10;        
    }
}
