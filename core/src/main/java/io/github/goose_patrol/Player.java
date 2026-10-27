package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.Viewport;


public class Player extends Entity{
    // Assets + objects
    private final Viewport gameView;

    // Constants - will be changed later but just some arbitrary values for now
    private static final float PLAYER_SCALE = 0.5f;
    private final float MAX_HEALTH = 100;

    // Values
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
    void update(float delta) {

    }

    // getters
    public float getHealth() { return health ;}

    public float getPlayerX() { return rect.x; }

    public float getPlayerY() { return rect.y; }

    public float getPlayerHeight() { return texture.getHeight() * PLAYER_SCALE; }

    public float getPlayerWidth() { return texture.getWidth() * PLAYER_SCALE; }


}
