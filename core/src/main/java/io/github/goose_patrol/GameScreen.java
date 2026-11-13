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
    private final float SCREEN_WIDTH = 1162f;         // width of background
    private final float SCREEN_HEIGHT = 650f;         // height of background
    private final long GRACE = 5;                     // time before geese can spawn
    private final float SPAWN_PERIOD = 3.5f;          // how long between each goose spawning
    private final int MAX_GEESE = 20;                 // maximum number of geese that can be instantiated at once
    private final float BUTTON_W = 140f;              // pause button size (UI pixels)
    private final float BUTTON_H = 50f;
    private final float BUTTON_MARGIN = 15f;          // gap between buttons and screen corner
    private final float BUTTON_PADDING = 15f;         // space around the text on a button
    private final float PLAYER_HITBOX_SCALE = 0.2f;   // hitbox scalers - does not affect size on screen
    private final float GOOSE_HITBOX_SCALE = 0.3f;    // hitbox scalers - does not affect size on screen
    private final float ITEM_ICON_HEIGHT = 150f;      // height of items on the UI
    private final float ITEM_ICON_WIDTH = 150f;       // width of items on the UI
    private final float SHIELD_COOLDOWN = 10f;         // in seconds
    private final float HEAL_COOLDOWN = 10f;


    //Assets
    private final Texture playerTexture = new Texture("Student_Forward.png");
    private final Texture gooseTexture = new Texture("Goose.png");
    private final Texture background = new Texture("Campus_Background.png");
           // placeholders
    private final Texture ShieldTexture = new Texture("Shield.png");
    private final Texture HealTexture = new Texture("Heal.png");

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
    private final Rectangle pauseButton = new Rectangle();
    private final Array<Integer> removeList = new Array<>();
    public final Player player = new Player(SCREEN_WIDTH/2 , SCREEN_HEIGHT/2, playerTexture, PLAYER_HITBOX_SCALE);
    public static Array<Goose> geese = new Array<>();
    public static Array<Item> inventory = new Array<>(5);
    public final ShieldItem shield = new ShieldItem(ShieldTexture, BUTTON_MARGIN, BUTTON_MARGIN, ITEM_ICON_WIDTH, ITEM_ICON_HEIGHT, SHIELD_COOLDOWN, player);
    public final HealItem heal = new HealItem(HealTexture, (ITEM_ICON_WIDTH+ BUTTON_MARGIN), BUTTON_MARGIN, ITEM_ICON_WIDTH, ITEM_ICON_HEIGHT, HEAL_COOLDOWN, player);

    // Time keeping
    // time is now counted with delta so it stops while the game is paused - might be some issues with keeping time on less powerful machines
    private final int MAX_TIME = 300;        // max time for game in seconds
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
        player.reset(SCREEN_WIDTH/2f, SCREEN_HEIGHT/2f);
        geese.clear();
        inventory.clear();
        inventory.add(shield);
        inventory.add(heal);
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
        else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
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

        // spawn geese in every {SPAWN PERIOD} seconds
        // continually drawn unless 'dispersed'
        spawnTimer += delta;
        if (elapsed_T >= GRACE) {
            // gets random spawn-point for each new goose
            float x = GooseUtil.getEnemySpawnX(player);
            float y = GooseUtil.getEnemySpawnY(player);

            // stops at 20 geese to align with group docs (if statement runs before another goose added so prevents an extra goose spawning)
            if (spawnTimer >= SPAWN_PERIOD && geese.size < MAX_GEESE) {
                geese.add(new Goose(x, y, gooseTexture, player, GOOSE_HITBOX_SCALE));
                spawnTimer = 0;
            }
        }

        // iterates through items whilst checking their index against the number pressed
        int count = 0;
        for (Item item : inventory) {
            int itemUsed = GooseUtil.getItemUsed();
            item.update(delta, (itemUsed == count + 1));
            count++;
        }

        // iterates through geese and removes the geese with <= 0 health
        removeList.clear();
        int index = 0;
        for (Goose goose : geese) {
            if (goose.getHealth() <= 0.0f) {
                removeList.add(index);
            }
            goose.update(delta);
            ++ index;
        }
        for (int goose : removeList) {
            // may be changed to call a way of getting rid of the geese without them just disappearing
            geese.removeIndex(goose);
        }

        player.update(delta); // updates player status - pos, hit cooldown etc...
        player.keepInside(SCREEN_WIDTH, SCREEN_HEIGHT); // map edges act as walls

        if (player.getHealth() <= 0) {
            GameOver(false);
        } else if (timeLeft <= 0) {
            timeLeft = 0;
            GameOver(true);
        }
    }

    // code for using mouse control //
//    // if true on frame game pause
//    private boolean pauseButtonClicked() {
//        if (!Gdx.input.justTouched()) return false;
//        // mouse y is measured from the top of the window, UI is drawn from the bottom of the game area
//        float mx = Gdx.input.getX() - gameView.getScreenX();
//        float my = Gdx.graphics.getHeight() - Gdx.input.getY() - gameView.getScreenY();
//        return pauseButton.contains(mx, my);
//    }

    // draws the things on the scene
    public void draw() {
        // gets position of center of the game area for ease of drawing
        float XCentre = gameView.getScreenWidth() / 2f;
        float YCentre = gameView.getScreenHeight() / 2f;

        // time formating
        int minutesLeft = (int) Math.ceil(timeLeft) / 60;
        int secondsLeft = (int) Math.ceil(timeLeft) % 60;
        String timerStr = String.format("%d:%02d", minutesLeft, secondsLeft);

        gameView.apply();

        batch.begin();
        batch.draw(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        // draws Entities
        for (Goose goose : geese) goose.draw(batch);
        player.draw(batch);
        batch.end();

        // draws hitboxes attached to camera and gameView for debugging
        Debugger.drawAllHitbox(player, geese, camera);

        batch.getProjectionMatrix().setToOrtho2D(0, 0, gameView.getScreenWidth(), gameView.getScreenHeight());
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        // keeps it to 1 dp
        String hitsStr = "HP: " + Math.round(player.getHealth()*10)/10f + " / " + player.MAX_HEALTH;

        layout.setText(font, timerStr);

        Gdx.gl.glEnable(GL20.GL_BLEND);     // allows the use of alpha values - allows use of transparent shapes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (paused || gameOver) {
            shapeRenderer.setColor(0f, 0f, 0f, 0.6f);
            shapeRenderer.rect(0, 0, gameView.getScreenWidth(), gameView.getScreenHeight());
        }
        shapeRenderer.setColor(Color.WHITE);

        // draws rectangle for items to be drawn on-top of
        shapeRenderer.rect(BUTTON_MARGIN, BUTTON_MARGIN,
                (ITEM_ICON_WIDTH * inventory.size),
                ITEM_ICON_HEIGHT);

        // draws rectangle for timer to be drawn on-top of
        shapeRenderer.rect(XCentre - layout.width/2f, gameView.getScreenHeight() - layout.height - BUTTON_MARGIN*2, layout.width + BUTTON_PADDING, layout.height + BUTTON_PADDING);
        if (!gameOver) {
            //draws pause button
            if (!paused) layout.setText(smallFont, "PAUSE");
            else {layout.setText(smallFont, "RESUME");}

            shapeRenderer.rect(gameView.getScreenWidth() - layout.width - BUTTON_MARGIN - BUTTON_PADDING*2,
                    gameView.getScreenHeight() - layout.height - BUTTON_MARGIN - BUTTON_PADDING*2,
                    layout.width + BUTTON_PADDING*2, layout.height + BUTTON_PADDING*2);
        }
        // Health panel
        layout.setText(smallFont, hitsStr);
        shapeRenderer.rect(BUTTON_MARGIN, gameView.getScreenHeight() - layout.height - BUTTON_PADDING*2,
                layout.width + BUTTON_PADDING, layout.height + BUTTON_PADDING);

        shapeRenderer.setColor(0f, 0f, 0f, 0.6f);
        for (Item item : inventory) {
            float cooldownLeft = item.cooldownLeft;
            float cooldown = item.COOLDOWN;
            if (cooldownLeft <= 0) {
                continue;
            }
            else{ shapeRenderer.rect(item.getX(), item.getY(), item.getWidth(), cooldownLeft/cooldown * item.getHeight());}
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);


        // top layer batch render
        batch.setProjectionMatrix(shapeRenderer.getProjectionMatrix());
        batch.begin();

        // item textures drawn
        for (Item item : inventory) item.draw(batch);

        // timer text
        layout.setText(font, timerStr);
        font.draw(batch, layout,
                XCentre - layout.width/2 + BUTTON_PADDING/2, gameView.getScreenHeight() - BUTTON_PADDING*1.5f);

        // pause button label
        if (!gameOver) {
            if (paused) {
                layout.setText(smallFont, "RESUME");
            } else {
                layout.setText(smallFont, "PAUSE");
            }
            smallFont.draw(batch, layout,
                gameView.getScreenWidth() - layout.width - BUTTON_MARGIN - BUTTON_PADDING,
                gameView.getScreenHeight() - BUTTON_MARGIN - BUTTON_PADDING);
        }

        // Health panel text
        layout.setText(smallFont, hitsStr);
        smallFont.draw(batch, layout,
            BUTTON_MARGIN*1.5f,
            gameView.getScreenHeight() - BUTTON_PADDING*1.5f);
        // GameOver / win screen
        if (gameOver) {
            String title;
            String message;
            if (won) {
                title = "YOU WIN!";
                message = "You survived the geese";
            } else {
                title = "GAME OVER";
                message = "The geese caught you " + player.getHits() + " times";
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
