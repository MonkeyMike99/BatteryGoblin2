import com.badlogic.gdx.math.Circle; 
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color; 
public class GameObject extends Circle
{
    //inherits the attributes of the x, y, and radius
    //can be accessed here or any subclass by this.x, this.y, this.radius
    
    //Libgdx actually made them public instance variables, so we have direct access to them
    //we do NOT have to call the setters or getters
    
    //Note: Making your instance variables public is NOT a good practice do NOT do it
    //when writing your own classes. 
    
    private Color c;

    public GameObject(float theX, float theY,float radius, Color c)
    {
        super(theX, theY, radius);  
        this.c = c; 
    
    }

    public Color getColor()
    {
        return c; 
    }

    public void setColor(Color color)
    {
        this.c = color; 
    }
    
    
    /**
     * by default every GameObject will move up diagonally to the right, unless overridden
     */
    public void move()
    {
        this.x += 1; 
        this.y += 1;
    }

}
