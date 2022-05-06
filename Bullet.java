import com.badlogic.gdx.math.Circle; 
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color; 
/**
 * Write a description of class Bullet here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Bullet extends GameObject
{
    private float angle; 
    private Color c;
    private float speed;
    private int timer;
    public Bullet(float x, float y,float radius,   Color c, float angle,float speed)
    {
        super(x,y,radius,c);
        this.angle=angle;
        this.speed=speed;
        timer=0;
        //TODO: call the super classes constructor to set this Enemy objects attributes
        //and set what is unique to an Enemy

    }
    public void timerInc(){
        timer+=1;
    }
    public int timer(){
        return timer;
    }
    public void setAngle(float angle)
    {
        this.angle = angle;    
    }

    public float getAngle()
    {
        return angle; 
    }

    @Override
    public void move()
    {
        //TODO: change the x and y coordinates using MathUtils.cosDeg and
        //MathUtils.sinDeg and multiplying it by the BALL_SPEED
        this.x+=speed*MathUtils.cosDeg(angle);
        this.y+=speed*MathUtils.sinDeg(angle);
        //note this.x and this.y are inherited and can be changed because they 
        //are PUBLIC instance variables in the Circle class in Libgdx. 
        //Please DO NOT do this when you are writing a class. You should have 
        //setters and getters



    }

    public boolean inBounds()
    {
       //scaled up by 3 because when Enemy objects are first created they are just off the screen, so they would be considered out of bounds without ever moving
        return (this.x + 3 * this.radius) > 0 &&
        (this.x - 3 * this.radius) < Constants.WORLD_WIDTH &&
        (this.y + 3 * this.radius) > 0 &&
        (this.y - 3 * this.radius) < Constants.WORLD_HEIGHT; 
    }

}
