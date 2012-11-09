package spacewars.game.model;

public class Player
{   
    /**
     * which homeplanet does he have
     */
    protected int playerId;
    /**
     * 
     */
    protected int score;
    /**
     * amount of minerals he owns
     */
    protected int minerals;    
    protected int mineralsPerMinute;    

    
    public Player()
    {   
        
    }

    public int getScore()
    {
        return score;
    }
    
    public void setScore(int score)
    {
        this.score = score;
    }
}
