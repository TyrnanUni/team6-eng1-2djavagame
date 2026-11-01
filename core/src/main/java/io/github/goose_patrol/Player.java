package io.github.goose_patrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.Viewport;


public class Player extends Entity{
    // Assets + objects
    private final Viewport gameView;

    // Constants - will be changed later but just some arbitrary values for now
    private static final float PLAYER_SCALE = 0.5f;
    private final float MAX_HEALTH = 100.0f;
    private final float RANGE = 70.0f; // range past which enemies spawn as to be off-screen

    // Values
    private float speed = 150f;
    private float health;


    // Constructor
    public Player(float x, float y, Viewport gameView, Texture texture) {
        super(x, y, texture.getWidth() * PLAYER_SCALE, texture.getHeight() * PLAYER_SCALE, texture);

        this.gameView = gameView;
    }

    // called when game starts/ restarts
    public void reset(float x, float y) {
        rect.setPosition(x, y);
        health = MAX_HEALTH;
    }

    //
    @Override
    public void update(float delta) {
        Input(delta);
        // needs to check if taking damage + collisions
    }

    // checks for input and adjusts position on screen accordingly
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
