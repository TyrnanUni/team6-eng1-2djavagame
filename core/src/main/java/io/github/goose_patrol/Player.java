package io.github.goose_patrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A class expressing a Player. Can be controlled by user input.
 */
public class Player extends Entity{

    // Constants - will be changed later but just some arbitrary values for now
    private static final float PLAYER_SCALE = 0.5f;
    private final float MAX_HEALTH = 100.0f;
    private final float SPEED = 115f;

    // Values
    private float health;
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
        health = MAX_HEALTH;
    }

    /**
     * Updates status' of player. Input, health etc...
     * @param delta time since last render
     */
    @Override
    public void update(float delta) {
        Input(delta);
    }

    /**
     * checks for input and adjusts position on screen accordingly
     * @param delta time since last render
     */
    public void Input(float delta) {
        velocity.set(0, 0); // resets velocity so doesn't keep going after stopped pressing

        if (Gdx.input.isKeyPressed(Input.Keys.W))  velocity.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A))  velocity.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S))  velocity.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D))  velocity.x += 1;

        if (!velocity.isZero()) velocity.nor();

        rect.x += velocity.x * delta * SPEED;
        rect.y += velocity.y * delta * SPEED;

    }

    // getters
    public float getHealth() { return health ;}

    public float getX() { return rect.x; }

    public float getY() { return rect.y; }

    public float getHeight() { return texture.getHeight() * PLAYER_SCALE; }

    public float getWidth() { return texture.getWidth() * PLAYER_SCALE; }

    // setters
    public void setHealth(float newHealth) {
        health = newHealth;
    }
}
