
/**
 * Write a description of class EnemyRoom here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class EnemyRoom extends Room
{
    private int enemies;
    private boolean defeated;
    EnemyRoom(int count){
        super(false,false,false,false);
        enemies = count;
        defeated = false;
    }
    public int getEnemyCount(){
        return enemies;
    }
    public void killEnemy(){
        enemies--;
        if(enemies==0){
            defeated = true;
        }
    }
    public boolean getDefeated(){
        return defeated;
    }
}
