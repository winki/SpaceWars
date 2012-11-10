package spacewars.game.model.planets;

import spacewars.game.model.GameElement;
import spacewars.gamelib.geometrics.Vector;

public abstract class Planet extends GameElement
{
    /**
     * Id for serialization
     */
    private static final long serialVersionUID = 1L;
    
    public Planet(Vector position, int sizeRadius, int viewRadius)
    {
        super(position, sizeRadius, viewRadius);
        // TODO Auto-generated constructor stub
    }
}
