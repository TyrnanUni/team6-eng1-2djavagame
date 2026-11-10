package io.github.goose_patrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A class expressing a Player. Can be controlled by user input.
 */
public class Player extends Entity{

    // Constants - will be changed later but just some arbitrary values for now
    private static final float PLAYER_SCALE = 0.5f;
    private final float SPEED = 115f;
    private final float HIT_COOLDOWN = 1.5f;

    // Values
    private int hits;
    private float hitTimer;
    public static Vector2 velocity = new Vector2();

    /**
     * Constructor
     * @param x bottom left starting x pos
     * @param y bottom left starting y pos
     * @param gameView the game viewport
     * @param texture the player texture
     */
    public Player(float x, float y, Viewport gameView, Texture texture) {
        super(x, y, texture.getWidth() * PLAYER_SCALE, texture.getHeight() * PLAYER_SCALE, texture);

        // Assets + objects
    }

    /**
     * called when game starts/ restarts
     * @param x bottom left starting x pos
     * @param y bottom left starting y pos
     */
    public void reset(float x, float y) {
        rect.setPosition(x, y);
        hits = 0;
        hitTimer = 0;
    }

    /**
     * Updates status' of player. Input, hit cooldown etc...
     * @param delta time since last render
     */
    @Override
    public void update(float delta) {
        Input(delta);
        if (hitTimer > 0) {
            hitTimer -= delta;
        }
    }

    public void hit() {
        if (hitTimer <= 0) {
            hits++;
            hitTimer = HIT_COOLDOWN;
        }
    }

    /**
     * checks for input and adjusts position on screen accordingly
     * @param delta time since last render
     */
    public void Input(float delta) {
        velocity.set(0, 0); // resets velocity so doesn't keep going after stopped pressing

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))    velocity.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))  velocity.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))  velocity.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) velocity.x += 1;

        if (!velocity.isZero()) velocity.nor();

        rect.x += velocity.x * delta * SPEED;
        rect.y += velocity.y * delta * SPEED;

    }

    /**
     * Stops player leaving the map - edges act as walls.
     */
    public void keepInside(float mapWidth, float mapHeight) {
        rect.x = MathUtils.clamp(rect.x, 0, mapWidth - rect.width);
        rect.y = MathUtils.clamp(rect.y, 0, mapHeight - rect.height);
    }

    // getters
    public int getHits() { return hits; }

    public float getX() { return rect.x; }

    public float getY() { return rect.y; }

    public float getHeight() { return texture.getHeight() * PLAYER_SCALE; }

    public float getWidth() { return texture.getWidth() * PLAYER_SCALE; }
}
