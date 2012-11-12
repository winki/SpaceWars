package spacewars.game.model.buildings;

import java.awt.Color;
import java.awt.Graphics2D;

import spacewars.game.model.GameState;
import spacewars.game.model.Ship;
import spacewars.gamelib.Button;
import spacewars.gamelib.Mouse;
import spacewars.gamelib.Screen;
import spacewars.gamelib.geometrics.Vector;

public class Shipyard extends Building
{
    /**
     * Id for serialization
     */
	
    private static final long serialVersionUID = 1L;
    
    private int ships = 0;
    private static int costs = 500;
    
    public Shipyard(Vector position)
    {
        super(position, 15, 1000, costs);
    }
    
    @Override
    public void render(Graphics2D g)
    {
        super.render(g);
        
        final Vector o = Screen.getInstance().getViewport().getOriginPosition();
        final Vector p = getPosition().add(o);
        final int r = getSizeRadius();
        
        int innerX = 0;
        int innerY = 0;
        int innerR = r - 3;
        
        
        
        Ship ship = new Ship(new Vector());
        GameState gameState = new GameState();
        
        g.setColor(isPlaceable() ? Color.WHITE : Color.RED);
        g.fillOval(p.x - r, p.y - r, 2 * r, 2 * r);
        g.drawString("Shipyard", p.x + r + 2, p.y + 4);
        
        if (this.isPlaced() && Mouse.getState().getX() >= p.x - r && Mouse.getState().getX() <= p.x + r && Mouse.getState().getY() >= p.y - r && Mouse.getState().getY() <= p.y + r){
        	g.drawString("Build ship with left click | attack with right click", p.x + r + 20, p.y + 20);
        	if(Mouse.getState().isButtonPressed(Button.LEFT)){
        		if(ships <= 16){
        			ships += 1;
    	        	innerY = (int) (innerR * Math.sin(2 * Math.PI / 16 * ships));
    	        	innerX = (int) Math.sqrt(innerR * innerR - innerY * innerY);
    	        	
    	        	if (ships <= 4){
    	        		ship.setPosition(new Vector(p.x - innerX, p.y - innerY));
    	        		gameState.getShips().add(ship);
    	        	} else if (ships <= 8){
    	        		ship.setPosition(new Vector(p.x - innerX, p.y + innerY)); 
    	        		gameState.getShips().add(ship);
    	        	} else if (ships <= 12){
    	        		ship.setPosition(new Vector(p.x + innerX, p.y + innerY));
    	        		gameState.getShips().add(ship);
    	        	} else {
    	        		ship.setPosition(new Vector(p.x + innerX, p.y - innerY));
    	        		gameState.getShips().add(ship);
    	        	}
        		}else{
        			g.drawString("no space left in hangar", p.x, p.y-40);
        		}
        	}
        	
        	else if(Mouse.getState().isButtonPressed(Button.RIGHT)){
        		//send ships to attack!
        	}
        }
        
        // render ships        
        g.setColor(Color.blue);
        
        for (Ship ship2 : gameState.getShips()){
        	g.fillOval(ship2.getPosition().x, ship2.getPosition().y, 3, 3);
        	System.out.println(gameState.getShips().size() +100);
        }
        
        
    }
}
