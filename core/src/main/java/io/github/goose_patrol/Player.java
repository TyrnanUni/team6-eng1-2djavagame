package io.github.goose_patrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A class expressing a Player. Can be controlled by user input.
 */
public class Player extends Entity{
    // Assets + objects
    private final Viewport gameView;

    // Constants - will be changed later but just some arbitrary values for now
    private static final float PLAYER_SCALE = 0.5f;
    private final float MAX_HEALTH = 100.0f;

    // Values
    private float speed = 100f;
    private float health;

    /**
     * Constructor
     * @param x bottom left starting x pos
     * @param y bottom left starting y pos
     * @param gameView the game viewport
     * @param texture the player texture
     */
    public Player(float x, float y, Viewport gameView, Texture texture) {
        super(x, y, texture.getWidth() * PLAYER_SCALE, texture.getHeight() * PLAYER_SCALE, texture);

        this.gameView = gameView;
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
        // needs to check if taking damage + collisions
    }

    /**
     * checks for input and adjusts position on screen accordingly
     * @param delta time since last render
     */
    public void Input(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            rect.y += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            rect.x -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            rect.y -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            rect.x += speed * delta;
        }
    }

    // getters
    public float getHealth() { return health ;}

    public float getX() { return rect.x; }

    public float getY() { return rect.y; }

    public float getHeight() { return texture.getHeight() * PLAYER_SCALE; }

    public float getWidth() { return texture.getWidth() * PLAYER_SCALE; }


}
