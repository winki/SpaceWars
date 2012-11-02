package spacewars.gamelib;

public class GameTime
{
    private long   elapsedTime;
    private double timeFactor;
    private int    frameRate;
    
    GameTime()
    {}
    
    public long getElapsedTime()
    {
        return elapsedTime;
    }
    
    void setElapsedTime(long nanoseconds)
    {
        this.elapsedTime = nanoseconds;
        this.timeFactor = nanoseconds / 1E9;
        this.frameRate = (int) (1000000000 / nanoseconds);
    }
    
    public double getTimeFactor()
    {
        return timeFactor;
    }
    
    public int getFrameRate()
    {
        return frameRate;
    }
}
