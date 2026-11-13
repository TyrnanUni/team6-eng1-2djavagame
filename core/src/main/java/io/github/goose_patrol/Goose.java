package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * A class expressing a Goose enemy and an object. Extends Entity abstract class.
 */
public class Goose extends Entity{

    // Constants
    private static final float GOOSE_SCALE = 0.5f;
    private final float MAX_HEALTH = 100.0f;
    private final float SPEED = 90f;
    private final float DAMAGE = 1f;

    // Values
    private float health;

    // Objects
    private final Player player;
    public final Vector2 velocity = new Vector2(0,0);
    public final Vector2 push = new Vector2(0,0);
    private final Vector2 diff = new Vector2(0, 0);

    /**
     * Creates a goose enemy. Player is needed as a param as the position and pathfinding relies on it because it's centered around the player.
     * @param x bottom left x pos
     * @param y bottom left y pos
     * @param texture enemy texture
     * @param player player object tied to the enemies
     */
    public Goose(float x, float y, Texture texture, Player player, float HITBOX_SCALE) {
        super(x, y, texture.getWidth() * GOOSE_SCALE, texture.getHeight() * GOOSE_SCALE, texture, HITBOX_SCALE);
        this.player = player;
        health = MAX_HEALTH;
    }

    /**
     * Updates position, health etc... of a goose object
     * @param delta time since last render call
     */
    @Override
    void update(float delta) {
        float Xdiff = player.getCenterX() - getCenterX();
        float Ydiff = player.getCenterY() - getCenterY();
        velocity.set(Xdiff, Ydiff).nor();

        // this just keeps the geese from packing to closely together, it offsets their velocity by the direction of the incoming geese.
        for (int i = 0; i < GameScreen.geese.size; ++i) {
            Goose other = GameScreen.geese.get(i);
            if (this == other) continue;

            if (this.hitbox.overlaps(other.hitbox)) {
                GooseUtil.seperate(other, this);
            }
        }

        if (this.overlaps(player)) {
            player.hit(DAMAGE);
            GooseUtil.seperate(this, player);
        }

        hitbox.x += velocity.x * delta * SPEED;
        hitbox.y += velocity.y * delta * SPEED;


    }

    // getters
    public float getHealth() { return health ;}

    public float getHeight() { return texture.getHeight() * GOOSE_SCALE; }

    public float getWidth() { return texture.getWidth() * GOOSE_SCALE; }
}
