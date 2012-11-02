package spacewars.util;

import java.awt.Image;
import java.awt.Toolkit;

public class Ressources
{
    public static Image loadProjectImage(String path)
    {
        return Toolkit.getDefaultToolkit().getImage("res/img/" + path);
    }    
}
