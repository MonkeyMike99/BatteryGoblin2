import com.badlogic.gdx.ApplicationAdapter; 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer; 
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle; 
import com.badlogic.gdx.math.Circle; 
import com.badlogic.gdx.Input.Keys; 
import com.badlogic.gdx.math.Vector2; 
import com.badlogic.gdx.math.MathUtils; 
import com.badlogic.gdx.math.Intersector; 
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import java.util.*;

public class Escape extends ApplicationAdapter//A Pong object ___________ ApplicationAda
{
    private OrthographicCamera camera; //the camera to our world
    private Viewport viewport; //maintains the ratios of your world
    private ShapeRenderer renderer; //used to draw textures and fonts 
    private BitmapFont font; //used to draw fonts (text)
    private SpriteBatch batch; //also needed to draw fonts (text)
    private int playerx;
    private int playery;
    private Room[][] grid;
    private int roomR;
    private int roomC;
    private EnemyFollow[] enemies; //Circle object to represent the ball (Circle is a class form libGDX)
    private ArrayList<Bullet> bullets;
    private Player player; 
    private int logLen; 
    private int timer; 
    private Sound pew;

    private GameState gamestate; 


    @Override//called once when the game is started (kind of like our constructor)
    public void create(){
        camera = new OrthographicCamera(); //camera for our world, it is not moving
        viewport = new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera); //maintains world units from screen units
        renderer = new ShapeRenderer(); 
        font = new BitmapFont(); 
        batch = new SpriteBatch(); 
        pew = Gdx.audio.newSound(Gdx.files.internal("Laser-pew-sound-effect.mp3"));
        enemies = new EnemyFollow[1000]; 
        bullets = new ArrayList();
        grid = new Room[5][5];
        roomR=2;
        roomC=2;
        grid[roomR][roomC] = new Room(true,true,true,true);
        float randomRadius = 10;
        Color playerColor = new Color(0, 1, 0, 1); 
        player = new Player(Constants.WORLD_WIDTH / 2 - randomRadius / 2, 
            Constants.WORLD_HEIGHT / 2 - randomRadius / 2, randomRadius, playerColor); 
        playerx=(int)(Constants.WORLD_WIDTH / 2 - randomRadius / 2);
        playery=(int)(Constants.WORLD_HEIGHT / 2 - randomRadius / 2);
        timer = 0;
        logLen = 0;
        gamestate = GameState.MENU;  

    }

    @Override//this is called 60 times a second, all the drawing is in here, or helper
    //methods that are called from here
    public void render(){
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // viewport.apply(); 
        //these two lines wipe and reset the screen so when something action had happened
        //the screen won't have overlapping images
        if(Gdx.input.isKeyPressed(Keys.SPACE))
        {
            gamestate = GameState.GAME;
        }
        if(Gdx.input.isKeyPressed(Keys.W)){
            playery += 5;
        }
        if(Gdx.input.isKeyPressed(Keys.S)){
            playery -= 5;
        }
        if(Gdx.input.isKeyPressed(Keys.A)){
            playerx -= 5;
        }
        if(Gdx.input.isKeyPressed(Keys.D)){
            playerx += 5;
        }
        
        if(Gdx.input.justTouched()){
            generateBullet();
        }
        if(gamestate == GameState.GAME)
        {
            timer++; 
            checkRoom();
            moveBullets();
            player.move(playerx,playery);
            //check for collisions
            checkCollisions(); 

            if(player.getHealth() <= 0)
            {
                gamestate = GameState.MENU;
                player.setHealth(Constants.MAX_HEALTH);
                enemies = new EnemyFollow[1000]; 
                logLen=0;
            }

        }

        //now that all game logic is finished draw objects based on the state of their attributes
        if(gamestate == GameState.GAME)
        {
            renderer.setProjectionMatrix(viewport.getCamera().combined);
            renderer.setColor(Color.WHITE); 
            renderer.begin(ShapeType.Filled);

            for(int i = 0; i < logLen; i++)
            {
                renderer.setColor(enemies[i].getColor()); 
                Enemy temp = enemies[i];
                renderer.circle(temp.x, temp.y, temp.radius);
            }
            for(int i=0;i<bullets.size();i++){
                renderer.setColor(bullets.get(i).getColor());
                Bullet temp = bullets.get(i);
                renderer.circle(temp.x,temp.y, temp.radius);
            }

            //draw player
            renderer.setColor(player.getColor()); 
            renderer.circle(player.x, player.y, player.radius); 

            
             //draw health bar
            renderer.setColor(Color.RED); 
            renderer.rect(Constants.WORLD_WIDTH - 150, Constants.WORLD_HEIGHT - 50, Constants.MAX_HEALTH, 10);

            renderer.setColor(Color.GREEN); 
            float percentage = player.getHealth() / Constants.MAX_HEALTH;
            renderer.rect(Constants.WORLD_WIDTH - 150, Constants.WORLD_HEIGHT - 50, percentage * Constants.MAX_HEALTH , 10);

            renderer.end();
        }

        if(gamestate == GameState.MENU)
        {
            GlyphLayout layout = new GlyphLayout(font, "Press SPACE_BAR to start");

            batch.begin();

            font.draw(batch, layout, 
                Constants.WORLD_WIDTH / 2 - layout.width / 2, 
                Constants.WORLD_HEIGHT/2 + layout.height / 2 + 20);

            batch.end(); 
        }

    }

    public void generateEnemies()
    {
        float randX = 0; 
        float randY = 0;
        float randRadius = 0; 
        float randAngle = 0;
        float randSpeed =0;
        //Create an enemy every 10/60 of a second AND as long as there are not more than the length of the array
        if(timer % 20 == 0 && logLen < enemies.length)
        {
            randRadius = (float)(Math.random() * 8 + 5); //create a random radius from 5-20
            //enemies will come from top, bottom, left or right

            //TODO: generate a random # from 1 to 4, TOP = 1, BOTTOM = 2, LEFT = 3, RIGHT = 4 
            int num = (int)(Math.random() * 4 + 1); 

            // switch(num)
            // {
                // case Constants.TOP: 
                // //TODO: generate a random x value from 0 to the WORLD_WIDTH - 1, and cast it to a float
                // randX = (float)(Math.random()*Constants.WORLD_WIDTH);

                // //TODO: set the y value to be just above the WORLD_HEIGHT
                // randY = Constants.WORLD_HEIGHT+1;

                // //TODO: generate a random angle from 181 to 359 since the object is coming from the top the angle shoule be between 181 and 359

                // randAngle = (float)(Math.random()*(359-181)+181);
                // randSpeed = (float)(Math.random()*3+3);
                // break; 

                // case Constants.BOTTOM:
                // randX = (float)(Math.random() * Constants.WORLD_WIDTH); 
                // randY = -randRadius; 
                // randAngle = (float)(Math.random() * (179 - 1 + 1) + 1);
                // randSpeed = (float)(Math.random()*7+4);
                // break; 

                // case Constants.LEFT: 
                // randX = -randRadius; 
                // randY = (float)(Math.random() * Constants.WORLD_HEIGHT);
                // randAngle = (float)(Math.random() * (89 - (-89) + 1) -89);
                // randSpeed = (float)(Math.random()*3+4);
                // break; 

                // case Constants.RIGHT:
                randX = Constants.WORLD_WIDTH + randRadius; 
                randY = (float)(Math.random() * Constants.WORLD_HEIGHT);
                randAngle = (float)(Math.random() * (169 - 91 + 1) + 91);
                randSpeed = (float)(Math.random()*7+4);
                // break;

            // }

            //this generates a random color, in Libgdx the RGB values are from [0,1]
            Color randColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1);

            //TODO: Create and new Enemy object with the above attributes
            enemies[logLen] =new EnemyFollow(randX,randY,randRadius,randColor,randAngle,randSpeed);

            logLen++; //increase logLen, signifying we have put 1 more object in our array
        }   
    }
    public void generateBullet(){
        int screenX = Gdx.input.getX(); 
        int screenY = Gdx.input.getY(); 

        //translate the coordinate to match our world coordinates. The screen coordinate put (0, 0) in the top right, where as all out math is done
        //with (0,0) in the bottom left. 
        Vector2 worldCoor = viewport.unproject(new Vector2(screenX, screenY)); 
        pew.play();
        float angle = (float)((Math.atan((worldCoor.y-playery)/(worldCoor.x-playerx)))*(180/Math.PI));
        if(worldCoor.x-playerx<0){
            angle+=180;
        }
        Bullet temp = new Bullet(playerx,playery,Constants.BULLET_RADIUS,Color.WHITE,angle,Constants.BULLET_SPEED);
        bullets.add(temp);
        temp = new Bullet(playerx,playery,Constants.BULLET_RADIUS,Color.WHITE,angle+10,Constants.BULLET_SPEED);
        bullets.add(temp);
        temp = new Bullet(playerx,playery,Constants.BULLET_RADIUS,Color.WHITE,angle-10,Constants.BULLET_SPEED);
        bullets.add(temp);
    }

    public void moveEnemies()
    {
        for(int i = 0; i < logLen; i++)
        {
            EnemyFollow temp = enemies[i];
            temp.follow((float)playerx,(float)playery);
            if(!temp.inBounds())
            {
                shiftLeftAndRemove(i); 
            }
            temp.move(); 
        }
    }
    public void moveBullets()
    {
        for(int i = 0; i < bullets.size(); i++)
        {
            Bullet temp = bullets.get(i);
            if(!temp.inBounds()||temp.timer()>14)
            {
                bullets.remove(i); 
            }
            temp.timerInc();
            temp.move(); 
        }
    }

    public void movePlayer()
    {
        //get the coordinates of the mouse
        int screenX = Gdx.input.getX(); 
        int screenY = Gdx.input.getY(); 

        //translate the coordinate to match our world coordinates. The screen coordinate put (0, 0) in the top right, where as all out math is done
        //with (0,0) in the bottom left. 
        Vector2 worldCoor = viewport.unproject(new Vector2(screenX, screenY)); 

        //set the attributes of the player to the coordinates of the mouse
        player.move(playery,playerx); 

    }
    

    public void checkCollisions()
    {
        //TODO: loop through the enemies array and if the Enemy object overlaps with the an Enemy call the hit method in the player class! and then break out of the loop. 
        for(int i = 0;i<logLen;i++){
            if(enemies[i].overlaps(player)){
                player.hit(30);
                shiftLeftAndRemove(i);
                break;
            }
        }
        for(int i = 0;i<bullets.size();i++){
            for(int o = 0;o<logLen;o++){
                if(enemies[o].overlaps(bullets.get(i))){
                    shiftLeftAndRemove(o);
                    bullets.remove(i);
                    break;
                }
            }
        }
    }

    public void shiftLeftAndRemove(int index)
    {
        //TODO: This method is called when an Enemy object is out of bounds. Shift all the Enemy objects left (starting from index), set the last index of
        //enemies to null, and decrease the logLen
        for(int i = index; i < logLen - 1; i++)
        {
            enemies[i] = enemies[i+1];
        }
        enemies[logLen - 1] = null; 
        logLen--; 
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true); 
    }

    @Override
    public void dispose(){
        renderer.dispose(); 
        batch.dispose(); 
    }

}

