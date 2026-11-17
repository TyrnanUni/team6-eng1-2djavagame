package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * A class expressing a Goose enemy and an object. Extends Entity abstract class.
 */
public class Goose extends Entity{

    // Constants
    private final float HEALTH_BAR_LENGTH = 25f;
    private final float HEALTH_BAR_HEIGHT = 4f;
    private final float HEALTH_BAR_BORDER = 1f;
    private final float RUNAWAY_SPEED = 50f;
    private final float HIT_COOLDOWN = 0.6f;
    private final float DESPAWN_DIST = 200f;

    // Values
    private final Texture flipped;
    private final Texture normal;
    private float fullness;
    public boolean Full;
    private float hitTimer;
    public boolean shouldDespawn;
    private float damage = 1.1f;
    private  float speed = 110f;
    public static float maxFullness = 7.0f;

    // Objects
    private final Player player;
    public final Vector2 velocity = new Vector2(0,0);

    /**
     * Creates a goose enemy. Player is needed as a param as the position and pathfinding relies on it because it's centered around the player.
     * @param x bottom left x pos
     * @param y bottom left y pos
     * @param texture enemy texture
     * @param player player object tied to the enemies
     */
    public Goose(float x, float y, float w, float h, Texture texture, Texture flipped, Player player, float HITBOX_SCALE) {
        super(x, y, w, h, texture, HITBOX_SCALE);
        this.player = player;
        this.flipped = flipped;
        normal = texture;
        fullness = 0;
        Full = false;
        hitTimer = 0;
        shouldDespawn = false;
    }

    /**
     * Updates position, health etc... of a goose object
     * @param delta time since last render call
     */
    @Override
    void update(float delta) {
        float Xdiff;
        float Ydiff;

        if (hitTimer > 0) hitTimer -= delta;
        // when full the gooses target changes to run off-screen
        Xdiff = player.getCenterX() - getCenterX();
        Ydiff = player.getCenterY() - getCenterY();

        if (Full) velocity.set(Xdiff, Ydiff).nor().scl(-1);
        else{ velocity.set(Xdiff, Ydiff).nor(); }

        if ((Xdiff >= DESPAWN_DIST || Ydiff >= DESPAWN_DIST) && Full) {
            despawn();
        }


        // collision detection for other geese
        for (int i = 0; i < GameScreen.geese.size; ++i) {
            Goose other = GameScreen.geese.get(i);
            if (this == other) continue;

            if (this.hitbox.overlaps(other.hitbox)) {
                GooseUtil.separate(other, this);
            }
        }
        // collision detection for player
        if (this.overlaps(player)) {
            player.hit(damage);
            GooseUtil.separate(this, player);
        }

        // flips texture based on velocity
        if (velocity.x > 0 && !(texture == flipped)) setTexture(flipped);
        else if (velocity.x < 0 && (texture == flipped)) setTexture(normal);


        // if full run faster as to get off-screen before despawning
        if (!Full) {
            hitbox.x += velocity.x * delta * speed;
            hitbox.y += velocity.y * delta * speed;
        }
        else {
            hitbox.x += velocity.x * delta * RUNAWAY_SPEED;
            hitbox.y += velocity.y * delta * RUNAWAY_SPEED;
        }
    }

    @Override
    public void draw(Batch batch) {
        if (hitTimer > 0) {
            batch.setColor(1f, 0.6f, 0.6f, 1f);
        }
        super.draw(batch);
        batch.setColor(Color.WHITE);
    }


    //  - seperat from draw as needs shape renderer
    // draws the fullness bar of goose
    public void drawGooseFullnessBar(ShapeRenderer shapeRenderer) {
        float x = this.getX();
        float y = this.getY() - 5f;

        if (fullness > maxFullness) fullness = maxFullness;
        float fullnessRatio = fullness/ maxFullness;

        float innerRectX = x + HEALTH_BAR_BORDER;
        float innerRectY = y + HEALTH_BAR_BORDER;
        float innerRectW = HEALTH_BAR_LENGTH - HEALTH_BAR_BORDER*2f;
        float innerRectH = HEALTH_BAR_HEIGHT - HEALTH_BAR_BORDER*2f;

        // draws fullness-bar for goose.
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(x, y, HEALTH_BAR_LENGTH, HEALTH_BAR_HEIGHT);

        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(innerRectX, innerRectY, innerRectW, innerRectH);

        shapeRenderer.setColor(Color.PURPLE);
        shapeRenderer.rect(innerRectX, innerRectY, innerRectW * fullnessRatio, innerRectH);
    }

    // called when seed collides with goose
    public void hit(SeedProjectile seed) {
        if (hitTimer <= 0) {
            increaseFullness(seed.getCalories());
            hitTimer = HIT_COOLDOWN;
        }
    }

    // called when goose is full
    public void runawaySequence() {
        fullness = maxFullness; // prevents fullness going past max and messing with the fullness bar
        Full = true;
    }

    // called when goose has reached its despawn location
    public void despawn() {
        shouldDespawn = true;
    }


    // getters
    public float getFullness() { return fullness ;}

    // increments fullness
    public void increaseFullness(float increase) {
        fullness += increase;
    }

    // setters
    public void setSpeed(float newSpeed) {
        speed = newSpeed;
    }

    public void setDamage(float newDamage) {
        damage = newDamage;
    }

    public void setMaxFullness(float newMax) {
        maxFullness = newMax;
    }
}
