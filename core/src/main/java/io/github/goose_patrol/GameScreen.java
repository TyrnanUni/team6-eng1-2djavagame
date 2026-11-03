package io.github.goose_patrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Main game screen that appears after the controls screen.
 */
public class GameScreen extends ScreenAdapter {

    // Constants
    private final int MAX_TIME = 300;        // max time for game in seconds
    private final float SCREEN_WIDTH = 1162f; // width of background
    private final float SCREEN_HEIGHT = 650f; // height of background
    private final long GRACE = 5;             // time before geese can spawn
    private final float SPAWNPERIOD = 3.5f;     // how long between each goose spawning
    private final int MAX_GEESE = 20;         // maximum number of geese that can be instantiated at once

    //Assets
    private final Texture playerTexture = new Texture("Student_Forward.png");
    private final Texture gooseTexture = new Texture("Goose.png");
    private final Texture background = new Texture("Campus_Background.png");

    // Objects
    private final Batch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport gameView = new FitViewport(SCREEN_WIDTH/3, SCREEN_HEIGHT/3, camera);  // viewport locked on the player
    private final GlyphLayout layout = new GlyphLayout();     // used to help with text formatting on screen
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    public final Player player = new Player(SCREEN_WIDTH/2 , SCREEN_HEIGHT/2, gameView, playerTexture);
    public static Array<Goose> geese = new Array<>();

    // Time keeping
    public long START_T = TimeUtils.millis(); // game start time
    public long elapsed_T;                    // get current time with TimeUtils.timeSinceMillis(START_T);
    private float spawnTimer;                 // time since last goose spawned
    private float timeLeft;                   // time left until the game ends

    // Constructor
    public GameScreen(MainGame game) {
        batch = game.getBatch();
        font = GooseUtil.getFont(Color.BLACK, 100);
    }

    // called when window resized
    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0){
            return;
        }
        gameView.update(width, height, false);
    }


    // called when screen is set
    @Override
    public void show() {
        player.reset(SCREEN_WIDTH/2, SCREEN_HEIGHT/2);
    }


    // loops while this is the active screen
    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        elapsed_T = TimeUtils.timeSinceMillis(START_T) / 1000; // div 1000 to give seconds
        timeLeft = MAX_TIME - elapsed_T;

        // logic to spawn geese in every {SPAWNPERIOD} seconds
        // general idea is to have an Array of geese that spawn every however many seconds and are continually drawn unless 'dispersed'
        spawnTimer += delta;
        if (elapsed_T >= GRACE) {
            float x = GooseUtil.getEnemySpawnX(player);
            float y = GooseUtil.getEnemySpawnY(player);

            if (spawnTimer >= SPAWNPERIOD && geese.size <= MAX_GEESE) {
                geese.add(new Goose(x, y, gooseTexture, player));
                spawnTimer = 0;
            }
        }

        for (Goose goose : geese) {
            goose.update(delta);
        }

        // game ends when timeLeft hits 0
        if (timeLeft <= 0) {
            GameOver();
        }

        player.update(delta); // updates player status - pos, health etc..
        camera.position.set(player.getX() + player.getWidth()/2.0f, player.getY() + player.getHeight()/2.0f, 0.0f);
        camera.update(); // updates camera which makes sure no mismatching happens between player and camera - stops jittering
        batch.setProjectionMatrix(gameView.getCamera().combined);

        draw(); // all drawing done here
    }

    // draws the things on the scene
    public void draw() {
        // gets position of centre of screen for ease of drawing
        float XCentre = Gdx.graphics.getWidth() / 2f;
        float YCentre = Gdx.graphics.getHeight() / 2f;

        // formatting screen timer to string
        int minutesLeft = (int) timeLeft / 60;
        int secondsLeft = (int) timeLeft % 60;
        String timerStr = String.format("%d:%02d", minutesLeft, secondsLeft);

        // game batch - world
        gameView.apply(); //reapply viewport
        batch.begin();
        batch.draw(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        for (Goose goose : geese) {
            goose.draw(batch);
        }
        player.draw(batch);
        batch.end();

        //UI batch - fixed on screen
        batch.getProjectionMatrix().setToOrtho2D(0, 0, gameView.getScreenWidth(), gameView.getScreenHeight());

        layout.setText(font, timerStr);

        // do all UI shape rendering here
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(XCentre - layout.width/2f, gameView.getScreenHeight() - layout.height, layout.width + 25, layout.height);
        shapeRenderer.end();

        // all other rendering here
        batch.begin();
        font.draw(batch, layout, XCentre - layout.width/2, gameView.getScreenHeight());
        batch.end();
    }

    // make sure to dispose of screen unique items here
    @Override
    public void dispose() {
        background.dispose();
    }

    // will be called when game ends - allows retry
    public void GameOver() {

    }

    // will be called to pause the game - stops timer and game movement
    public void GamePause() {

    }
}
