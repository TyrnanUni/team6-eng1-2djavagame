package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Texture;

/**
 * A class expressing a Goose enemy and an object. Extends Entity abstract class.
 */
public class Goose extends Entity{

    // Constants
    private static final float GOOSE_SCALE = 0.5f;
    private final float MAX_HEALTH = 100.0f;
    private float SPEED = 90f;

    // Values
    private float health;

    // Objects
    private Player player;

    /**
     * Creates a goose enemy. Player is needed as a param as the position and pathfinding relies on it because its centered around the player.
     * @param x bottom left x pos
     * @param y bottom left y pos
     * @param texture enemy texture
     * @param player player object tied to the enemies
     */
    public Goose(float x, float y, Texture texture, Player player) {
        super(x, y, texture.getWidth() * GOOSE_SCALE, texture.getHeight() * GOOSE_SCALE, texture);
        this.player = player;
    }

    /**
     * Updates position, health etc... of a goose object
     * @param delta time since last render call
     */
    @Override
    void update(float delta) {
        float Xdiff = player.getX() - getX();
        float Ydiff = player.getY() - getY();
        float sld = (float) Math.sqrt((Xdiff*Xdiff) + (Ydiff*Ydiff)); // straight line distance

        // direction based on straight line distance, delta time needed to keep distance consistent across all frame rates
        rect.x += SPEED * delta * (Xdiff/sld);
        rect.y += SPEED * delta * (Ydiff/sld);

    }

    // getters
    public float getHealth() { return health ;}

    public float getX() { return rect.x; }

    public float getY() { return rect.y; }

    public float getHeight() { return texture.getHeight() * GOOSE_SCALE; }

    public float getWidth() { return texture.getWidth() * GOOSE_SCALE; }
}
