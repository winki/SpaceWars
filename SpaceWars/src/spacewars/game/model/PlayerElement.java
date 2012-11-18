package spacewars.game.model;

import java.io.Serializable;
import spacewars.gamelib.geometrics.Vector;

@SuppressWarnings("serial")
public class PlayerElement extends GameElement implements Serializable
{
    /**
     * The player that owns this element
     */
    protected Player player;
    
    public PlayerElement(final Player player, final Vector position, final int radius, final int sight)
    {
        super(position, radius, sight);
        this.player = player;
    }
    
    /**
     * Gets the owner of this element.
     * 
     * @return the player that owns this element
     */
    public Player getPlayer()
    {
        return player;
    }
}
