import com.badlogic.gdx.math.Circle; 
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color; 
/**
 * Write a description of class EnemyFollow here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class EnemyFollow extends Enemy
{
    public EnemyFollow(float x, float y,float radius,   Color c, float angle,float speed)
    {
        super(x,y,radius,c,angle,speed);
    }
    public void follow(float playerx,float playery){
        float a = (float)((Math.atan((playery-this.y)/(playerx-this.x)))*(180/Math.PI));
        if(playerx-this.x<0){
            a+=180;
        }
        super.setAngle(a);
    }
}
