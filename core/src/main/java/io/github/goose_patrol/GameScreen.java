package io.github.goose_patrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
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
    private final int MAX_HITS = 5;
    private final float BUTTON_W = 140f;      // pause button size (UI pixels)
    private final float BUTTON_H = 50f;
    private final float BUTTON_MARGIN = 15f;  // gap between pause button and screen corner

    //Assets
    private final Texture playerTexture = new Texture("Student_Forward.png");
    private final Texture gooseTexture = new Texture("Goose.png");
    private final Texture background = new Texture("Campus_Background.png");

    // Objects
    private final MainGame game;
    private final Batch batch;
    private final BitmapFont font;
    private final BitmapFont smallFont;       // black text for the pause button
    private final BitmapFont overlayFont;     // white text for the pause overlay
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport gameView = new FitViewport(SCREEN_WIDTH/3, SCREEN_HEIGHT/3, camera);  // viewport locked on the player
    private final GlyphLayout layout = new GlyphLayout();     // used to help with text formatting on screen
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final Rectangle pauseButton = new Rectangle(); // on-screen pause/resume button, in UI pixels
    public final Player player = new Player(SCREEN_WIDTH/2 , SCREEN_HEIGHT/2, gameView, playerTexture);
    public static Array<Goose> geese = new Array<>();

    // Time keeping
    // time is now counted with delta so it stops while the game is paused - might be some issues with keeping time on less powerfull machines
    private float playTime;                   // seconds of un-paused play so far
    public long elapsed_T;                    // whole seconds of play so far
    private float spawnTimer;                 // time since last goose spawned
    private float timeLeft = MAX_TIME;        // time left until the game ends

    // State
    private boolean paused = true;            // true while paused - game now starts paused
    private boolean gameOver;
    private boolean won;

    // Constructor
    public GameScreen(MainGame game) {
        this.game = game;
        batch = game.getBatch();
        font = GooseUtil.getFont(Color.BLACK, 100);
        smallFont = GooseUtil.getFont(Color.BLACK, 32);
        overlayFont = GooseUtil.getFont(Color.WHITE, 32);
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
        geese.clear();
    }

    // called by libGDX so when min or paused time stops
    @Override
    public void pause() {
        paused = true;
    }


    // loops while this active screen
    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        // pause button sits in the top right corner of the screen
        pauseButton.set(gameView.getScreenWidth() - BUTTON_W - BUTTON_MARGIN,
                        gameView.getScreenHeight() - BUTTON_H - BUTTON_MARGIN,
                        BUTTON_W, BUTTON_H);

        if (gameOver) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                game.setScreen(new GameScreen(game));
                dispose();
                return;
            }
        }
        // toggle pause
        else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.Q)
                || pauseButtonClicked()) {
            GamePause();
        }

        if (!paused && !gameOver) {
            update(delta);
        }

        camera.position.set(player.getX() + player.getWidth()/2.0f, player.getY() + player.getHeight()/2.0f, 0.0f);
        camera.update(); // updates camera which makes sure no mismatching happens between player and camera - stops jittering
        batch.setProjectionMatrix(gameView.getCamera().combined);

        draw();
    }

    // contains:
    // player.update()    goose.update()    playTime += delta
    // Called while not paused - stops gameplay effectively
    private void update(float delta) {
        playTime += delta;
        elapsed_T = (long) playTime;
        timeLeft = MAX_TIME - playTime;

        // spawn geese in every {SPAWNPERIOD} seconds
        // continually drawn unless 'dispersed'
        spawnTimer += delta;
        if (elapsed_T >= GRACE) {
            // gets random spawn-point for each new goose
            float x = GooseUtil.getEnemySpawnX(player);
            float y = GooseUtil.getEnemySpawnY(player);

            // stops at 20 geese to align with group docs (if statement runs before another goose added so prevents an extra goose spawning)
            if (spawnTimer >= SPAWNPERIOD && geese.size < MAX_GEESE) {
                geese.add(new Goose(x, y, gooseTexture, player));
                spawnTimer = 0;
            }
        }

        // iterates through geese and removes the geese with <= 0 health
        Array<Integer> removeList = new Array<>();
        int indx = 0;
        for (Goose goose : geese) {
            if (goose.getHealth() <= 0.0f) {
                removeList.add(indx);
            }
            goose.update(delta);
            ++ indx;
        }
        for (int goose : removeList) {
            // may be changed to call a way of getting rid of the geese without them just disappearing
            geese.removeIndex(goose);
        }

        player.update(delta); // updates player status - pos, hit cooldown etc..
        player.keepInside(SCREEN_WIDTH, SCREEN_HEIGHT); // map edges act as walls

        if (player.getHits() >= MAX_HITS) {
            GameOver(false);
        } else if (timeLeft <= 0) {
            timeLeft = 0;
            GameOver(true);
        }
    }

    // if true on frame game pause
    private boolean pauseButtonClicked() {
        if (!Gdx.input.justTouched()) return false;
        // mouse y is measured from the top of the window, UI is drawn from the bottom of the game area
        float mx = Gdx.input.getX() - gameView.getScreenX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY() - gameView.getScreenY();
        return pauseButton.contains(mx, my);
    }

    // draws the things on the scene
    public void draw() {
        // gets position of centre of the game area for ease of drawing
        float XCentre = gameView.getScreenWidth() / 2f;
        float YCentre = gameView.getScreenHeight() / 2f;

        // time formating
        int minutesLeft = (int) Math.ceil(timeLeft) / 60;
        int secondsLeft = (int) Math.ceil(timeLeft) % 60;
        String timerStr = String.format("%d:%02d", minutesLeft, secondsLeft);

        gameView.apply();
        batch.begin();
        batch.draw(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        for (Goose goose : geese) {
            goose.draw(batch);
        }
        player.draw(batch);
        batch.end();

        // draws hitboxes attached to camera and gameView for debugging
        Debugger.drawAllHitbox(player, geese, gameView, camera);

        batch.getProjectionMatrix().setToOrtho2D(0, 0, gameView.getScreenWidth(), gameView.getScreenHeight());
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        String hitsStr = "Hits: " + player.getHits() + " / " + MAX_HITS;

        layout.setText(font, timerStr);

        Gdx.gl.glEnable(GL20.GL_BLEND);     // allows the use of alpha values - allows use of transparent shapes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (paused || gameOver) {
            shapeRenderer.setColor(0f, 0f, 0f, 0.6f);
            shapeRenderer.rect(0, 0, gameView.getScreenWidth(), gameView.getScreenHeight());
        }

        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(XCentre - layout.width/2f, gameView.getScreenHeight() - layout.height, layout.width + 25, layout.height);
        if (!gameOver) {
            shapeRenderer.rect(pauseButton.x, pauseButton.y, pauseButton.width, pauseButton.height);
        }
        shapeRenderer.rect(BUTTON_MARGIN, pauseButton.y, BUTTON_W + 40, BUTTON_H);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // all rest of rendering here
        batch.begin();
        font.draw(batch, layout, XCentre - layout.width/2, gameView.getScreenHeight());

        // pause button label
        if (!gameOver) {
            if (paused) {
                layout.setText(smallFont, "RESUME");
            } else {
                layout.setText(smallFont, "PAUSE");
            }
            smallFont.draw(batch, layout,
                pauseButton.x + (pauseButton.width - layout.width) / 2f,
                pauseButton.y + (pauseButton.height + layout.height) / 2f);
        }

        layout.setText(smallFont, hitsStr);
        smallFont.draw(batch, layout,
            BUTTON_MARGIN + (BUTTON_W + 40 - layout.width) / 2f,
            pauseButton.y + (BUTTON_H + layout.height) / 2f);

        if (gameOver) {
            String title;
            String message;
            if (won) {
                title = "YOU WIN!";
                message = "You survived the geese";
            } else {
                title = "GAME OVER";
                message = "The geese caught you " + MAX_HITS + " times";
            }
            layout.setText(overlayFont, title);
            overlayFont.draw(batch, layout, XCentre - layout.width/2f, YCentre + layout.height * 2);
            layout.setText(overlayFont, message);
            overlayFont.draw(batch, layout, XCentre - layout.width/2f, YCentre);
            layout.setText(overlayFont, "Press R to play again");
            overlayFont.draw(batch, layout, XCentre - layout.width/2f, YCentre - layout.height * 2);
        }
        // pause overlay text
        else if (paused) {
            layout.setText(overlayFont, "PAUSED");
            overlayFont.draw(batch, layout, XCentre - layout.width/2f, YCentre + layout.height);
            layout.setText(overlayFont, "Press Esc / Q or click RESUME to continue");
            overlayFont.draw(batch, layout, XCentre - layout.width/2f, YCentre - layout.height);
        }
        batch.end();
    }

    // make sure to dispose of screen unique items here
    @Override
    public void dispose() {
        background.dispose();
        playerTexture.dispose();
        gooseTexture.dispose();
        font.dispose();
        smallFont.dispose();
        overlayFont.dispose();
        shapeRenderer.dispose();
    }

    // will be called when game ends - allows retry
    public void GameOver(boolean playerWon) {
        gameOver = true;
        won = playerWon;
    }

    // called to pause / unpause the game - stops timer and game movement while paused
    public void GamePause() {
        paused = !paused;
    }
}
