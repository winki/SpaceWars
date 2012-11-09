package spacewars.game.model;

public class Building extends GameElement
{   
    //how many res to build it
	public int costs;
	
	//how far can it grap res and give res (solar station and relais)
    public int radius;
    
    public int basicProduction;   
    
    //true if its connected to a power supply
    public boolean isConnected;
}
