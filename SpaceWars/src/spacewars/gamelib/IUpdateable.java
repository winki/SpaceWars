package spacewars.gamelib;

public interface IUpdateable
{
    /**
     * Is called, when the game element has to be updated.
     * 
     * @param gameTime elapsed game time
     */
    void update(GameTime gameTime);
}
