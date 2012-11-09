package spacewars.game.model;

import spacewars.gamelib.geometrics.Vector;

public abstract class Ship extends GameElement
{
    public int costs;
    public int power;
    public int speed;
    public int health;

    public Ship(Vector position, int sizeRadius, int viewRadius)
    {
        super(position, sizeRadius, viewRadius);
        // TODO Auto-generated constructor stub
    }   
}
