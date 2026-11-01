package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Texture;

/**
 * A class expressing a Goose enemy and an object. Extends Entity abstract class.
 */
public class Goose extends Entity{

    // Constants
    private static final float GOOSE_SCALE = 0.5f;
    private final float MAX_HEALTH = 100.0f;
    private final float RANGE = 100.0f; // the smallest distance a goose can spawn away from player without being seen

    // Values
    private float speed = 40f;
    private float health;

    // Objects
    private Player player;

    /**
     * Creates a goose enemy. Player is needed as a param as the position and pathfinding relies on it because its centered around the player.
     * @param x bottom left x pos
     * @param y bottom left y pos
     * @param w desired rect height
     * @param h desired rect  width
     * @param texture enemy texture
     * @param player player object tied to the enemies
     */
    public Goose(float x, float y, float w, float h, Texture texture, Player player) {
        super(x, y, w, h, texture);
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
        float sld = (float) Math.sqrt( (double)((Xdiff*Xdiff) + (Ydiff*Ydiff)) ); // straight line distance

        // direction based on straight line distance, delta time needed to keep distance consistent across all frame rates
        rect.x += speed * delta * (Xdiff/sld);
        rect.y += speed * delta * (Ydiff/sld);
    }

    public float getX() {
        return rect.x;
    }
    public float getY() {
        return rect.y;
    }
}
