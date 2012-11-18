package spacewars.game.model.buildings;

import spacewars.game.model.Player;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class Shipyard extends Building
{
    private static final String NAME  = "Shipyard";
    
    public Shipyard(final Player player, final Vector position)
    {
        super(player, position, 15, 200, 500);
    }
    
    @Override
    public String getName()
    {
        return NAME;        
    }
    
    @Override
    public void upgrade()
    {
        // TODO Auto-generated method stub
        super.upgrade();
    }
}
