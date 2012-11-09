package spacewars.gamelib.geometrics;

public class Vector
{
    public int x;
    public int y;
    
    public Vector()
    {}
    
    public Vector(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public void set(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public Vector add(Vector other)
    {
        return new Vector(x + other.x, y + other.y);
    }
    
    public Vector sub(Vector other)
    {
        return new Vector(x - other.x, y - other.y);
    }
    
    public double distance(Vector other)
    {
        return Math.sqrt(Math.pow(Math.abs(x - other.x), 2) + Math.pow(Math.abs(y - other.y), 2));
    }
    
    public double lenght()
    {
        return Math.sqrt(x * x + y + y);
    }
    
    public String toString()
    {
        return "Vector[" + x + "," + y + "]";
    }
}
