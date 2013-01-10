package spacewars.game.model;

public class Star
{
   private float x, y;
   private int   layer;
   
   public Star(final float x, final float y, final int layer)
   {
      this.x = x;
      this.y = y;
      this.layer = layer;
   }
   
   public float getX()
   {
      return x;
   }
   
   public float getY()
   {
      return y;
   }
   
   public int getLayer()
   {
      return layer;
   }
}
