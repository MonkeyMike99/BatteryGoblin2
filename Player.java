import com.badlogic.gdx.math.Circle; 
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color; 

public class Player extends GameObject
{
    private float health; 
    public Player(float theX, float theY, float radius, Color c )
    {
        super(theX, theY, radius, c); 
        health = Constants.MAX_HEALTH;
    }

    public void move(float theX, float theY)
    {
        this.x = theX; 
        this.y = theY; 
    }

    public void setHealth(float t)
    {
        health = t;    
    }

    public float getHealth()
    {
        return health; 
    }

    public void hit(int rad)
    {
        health -= rad/4; 
    }

}
