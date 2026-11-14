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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Main game screen that appears after the controls screen.
 */
public class GameScreen extends ScreenAdapter {

    // Constants
    private final float HEALTH_BAR_WIDTH = 500f;
    private final float HEALTH_BAR_HEIGHT = 30f;
    private final float SCREEN_WIDTH = 1162f;         // width of background
    private final float SCREEN_HEIGHT = 650f;         // height of background
    private final long GRACE = 5;                     // time before geese can spawn
    private final float SPAWN_PERIOD = 3.5f;          // how long between each goose spawning
    private final int MAX_GEESE = 20;                 // maximum number of geese that can be instantiated at once
    private final float PLAYER_HITBOX_SCALE = 0.2f;   // hitbox scalers - does not affect size on screen
    private final float GOOSE_HITBOX_SCALE = 0.3f;    // hitbox scalers - does not affect size on screen
    private final float SHIELD_COOLDOWN = 10f;         // in seconds
    private final float HEAL_COOLDOWN = 10f;

    //UI Constants
    private final float ITEM_ICON_HEIGHT = 150f;      // height of items on the UI - texture stretches to fit
    private final float ITEM_ICON_WIDTH = 150f;       // width of items on the UI
    private final float BUTTON_MARGIN = 15f;          // gap between buttons and screen
    private final float BUTTON_PADDING = 15f;         // space between edge of button and thing on the button
    private final int UI_FONT_SIZE = 50;
    private final int OVERLAY_FONT_SIZE = 50;
    private final int TIMER_FONT_SIZE = 100;
    private final Color UI_FONT_COLOR = Color.BLACK;
    private final Color TIMER_FONT_COLOR = Color.BLACK;
    private final Color OVERLAY_FONT_COLOR = Color.WHITE;

    //Assets
    private final Texture playerTexture = new Texture("Student_Forward.png");
    private final Texture gooseTexture = new Texture("Goose.png");
    private final Texture background = new Texture("Campus_Background.png");
    private final Texture ShieldTexture = new Texture("Shield.png");
    private final Texture HealTexture = new Texture("Heal.png");

    // Objects
    private final MainGame game;
    private final Batch batch;
    private final BitmapFont timerFont;
    private final BitmapFont UIFont;       // black text for the pause button
    private final BitmapFont overlayFont;     // white text for the pause overlay
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport gameView = new FitViewport(SCREEN_WIDTH/3, SCREEN_HEIGHT/3, camera);  // viewport locked on the player
    private final GlyphLayout layout = new GlyphLayout();     // used to help with text formatting on screen
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final Array<Integer> removeList = new Array<>();
    public final Player player = new Player(SCREEN_WIDTH/2 , SCREEN_HEIGHT/2, playerTexture, PLAYER_HITBOX_SCALE);
    public static Array<Goose> geese = new Array<>();
    public static Array<Item> inventory = new Array<>(5);
    public final ShieldItem shield = new ShieldItem(ShieldTexture, BUTTON_MARGIN + BUTTON_PADDING/2f, BUTTON_MARGIN + BUTTON_PADDING/2f, ITEM_ICON_WIDTH, ITEM_ICON_HEIGHT, SHIELD_COOLDOWN, player);
    public final HealItem heal = new HealItem(HealTexture, (ITEM_ICON_WIDTH + BUTTON_PADDING + BUTTON_PADDING/2f + BUTTON_MARGIN), BUTTON_MARGIN + BUTTON_PADDING/2f, ITEM_ICON_WIDTH, ITEM_ICON_HEIGHT, HEAL_COOLDOWN, player);

    // Time keeping
    // time is now counted with delta so it stops while the game is paused - might be some issues with keeping time on less powerful machines
    private final int MAX_TIME = 300;        // max time for game in seconds
    private float playTime;                   // seconds of un-paused play so far
    public long elapsed_T;                    // whole seconds of play so far
    private float spawnTimer;                 // time since last goose spawned
    private float timeLeft = MAX_TIME;        // time left until the game ends

    // States
    private boolean paused = true;            // true while paused - game now starts paused
    private boolean gameOver;
    private boolean won;

    public GameScreen(MainGame game) {
        this.game = game;
        batch = game.getBatch();
        timerFont = GooseUtil.getFont(TIMER_FONT_COLOR, TIMER_FONT_SIZE);
        UIFont = GooseUtil.getFont(UI_FONT_COLOR, UI_FONT_SIZE);
        overlayFont = GooseUtil.getFont(OVERLAY_FONT_COLOR, OVERLAY_FONT_SIZE);
    }

    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0){
            return;
        }
        gameView.update(width, height, false);
    }

    // called when screen is set + game reset
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

        // Clamps the camera position so it stays within the map boundaries
        float clampedX = com.badlogic.gdx.math.MathUtils.clamp(
                player.getX() + player.getWidth() / 2f,
                gameView.getWorldWidth()/2f,
                SCREEN_WIDTH - gameView.getWorldWidth()/2f);

        float clampedY = com.badlogic.gdx.math.MathUtils.clamp(
                player.getY() + player.getHeight() / 2f,
                gameView.getWorldHeight()/2f,
                SCREEN_HEIGHT - gameView.getWorldHeight()/2f);


        camera.position.set(clampedX, clampedY, 0.0f);
        camera.update();
        batch.setProjectionMatrix(gameView.getCamera().combined);

        draw();
    }

    // All object time based updates done here - everything in here stops when game paused
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

    // ALL GameScreen drawing done here
    public void draw() {
        // gets position of center of the game area for ease of drawing
        float XCentre = gameView.getScreenWidth() / 2f;
        float YCentre = gameView.getScreenHeight() / 2f;

        // time formating
        int minutesLeft = (int) Math.ceil(timeLeft) / 60;
        int secondsLeft = (int) Math.ceil(timeLeft) % 60;
        String timerStr = String.format("%d:%02d", minutesLeft, secondsLeft);

        gameView.apply();

        // lowest layer rendering first //
        batch.begin();
        batch.draw(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        // draws all geese
        for (Goose goose : geese) goose.draw(batch);
        player.draw(batch);
        batch.end();

//        // HIT BOX CODE //
//        // draws hitboxes attached to camera and gameView for debugging
//        Debugger.drawAllHitbox(player, geese, camera);

        batch.getProjectionMatrix().setToOrtho2D(0, 0, gameView.getScreenWidth(), gameView.getScreenHeight());
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        layout.setText(timerFont, timerStr);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        // draws gray transparent screen for pause or game end
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (paused || gameOver) {
            shapeRenderer.setColor(0f, 0f, 0f, 0.6f);
            shapeRenderer.rect(0, 0, gameView.getScreenWidth(), gameView.getScreenHeight());
        }

        // draws rectangle for items to be drawn on-top of
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(BUTTON_MARGIN, BUTTON_MARGIN,
                (ITEM_ICON_WIDTH * inventory.size) + (BUTTON_PADDING*inventory.size),
                ITEM_ICON_HEIGHT + BUTTON_PADDING);

        // draws rectangle for timer to be drawn on-top of
        shapeRenderer.rect(XCentre - layout.width/2f + BUTTON_PADDING/2f, gameView.getScreenHeight() - layout.height - BUTTON_MARGIN - BUTTON_PADDING/2f,
                layout.width + BUTTON_PADDING, layout.height + BUTTON_PADDING);


        // Health panel + red health bar
        shapeRenderer.rect(XCentre - HEALTH_BAR_WIDTH/2f, BUTTON_MARGIN,
                HEALTH_BAR_WIDTH + BUTTON_PADDING, HEALTH_BAR_HEIGHT + BUTTON_PADDING);

        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(XCentre - HEALTH_BAR_WIDTH/2f + BUTTON_PADDING/2f, BUTTON_MARGIN + BUTTON_PADDING/2f,
                HEALTH_BAR_WIDTH , HEALTH_BAR_HEIGHT);

        // colors health-bar for item used - shielding takes priority
        if (player.isShielded()) shapeRenderer.setColor(Color.BLUE);
        else if (player.isHealing()) shapeRenderer.setColor(Color.GREEN);
        else{ shapeRenderer.setColor(Color.RED); }

        // bar moves so the player knows when they are healing - doesn't need to rely on color alone
        shapeRenderer.rect(XCentre - HEALTH_BAR_WIDTH/2f + BUTTON_PADDING/2f, BUTTON_MARGIN + BUTTON_PADDING/2f,
                HEALTH_BAR_WIDTH * (player.getHealth()/player.MAX_HEALTH), HEALTH_BAR_HEIGHT);


        // draws cooldown slider on items
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

        // top most layer drawing //
        batch.setProjectionMatrix(shapeRenderer.getProjectionMatrix());
        batch.begin();

        // item textures drawn
        for (Item item : inventory) item.draw(batch);

        // timer text
        layout.setText(timerFont, timerStr);
        timerFont.draw(batch, layout,
                XCentre - layout.width/2f + BUTTON_PADDING, gameView.getScreenHeight() - BUTTON_MARGIN);


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
            layout.setText(overlayFont, "Press Q or ESC to RESUME..");
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
        timerFont.dispose();
        UIFont.dispose();
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
