package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * A class expressing a Goose enemy and an object. Extends Entity abstract class.
 */
public class Goose extends Entity{

    // Constants
    private final float HEALTH_BAR_LENGTH = 25f;
    private final float HEALTH_BAR_HEIGHT = 4f;
    private final float HEALTH_BAR_BORDER = 1f;
    private static final float GOOSE_SCALE = 0.5f;
    public static final float MAX_FULLNESS = 10.0f;
    private final float DAMAGE = 1f;
    private final float SPEED = 90f;
    private final float RUNAWAY_SPEED = 200f;
    private final float HIT_COOLDOWN = 1f;

    // Values

    private float fullness;
    public boolean Full;
    private float despawnX;
    private float despawnY;
    private float hitTimer;

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
    public Goose(float x, float y, float w, float h, Texture texture, Player player, float HITBOX_SCALE) {
        super(x, y, w, h, texture, HITBOX_SCALE);
        this.player = player;
        fullness = 0;
        Full = false;
        hitTimer = 0;
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
        if (!Full) {
            Xdiff = player.getCenterX() - getCenterX();
            Ydiff = player.getCenterY() - getCenterY();
        }
        else {
            Xdiff = despawnX - getCenterX();
            Ydiff = despawnY - getCenterY();
            if (Xdiff == 0 && Ydiff == 0) {
                despawn();
            }
        }
        velocity.set(Xdiff, Ydiff).nor();

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
            player.hit(DAMAGE);
            GooseUtil.separate(this, player);
        }

        // if full run faster as to get off-screen before despawning
        if (!Full) {
            hitbox.x += velocity.x * delta * SPEED;
            hitbox.y += velocity.y * delta * SPEED;
        }
        else {
            hitbox.x += velocity.x * delta * RUNAWAY_SPEED;
            hitbox.y += velocity.y * delta * RUNAWAY_SPEED;
        }
    }

    @Override
    public void draw(Batch batch) {
        if (hitTimer > 0) {
            int flashStep = (int) (hitTimer * 10);
            if (flashStep % 2 == 0) {
                batch.setColor(1f, 0.6f, 0.6f, 1f);
            }
        }
        super.draw(batch);
        batch.setColor(Color.WHITE);
    }


    //  - seperate from draw as needs shape renderer
    // draws the fullness bar of goose
    public void drawGooseFullnessBar(ShapeRenderer shapeRenderer) {
        float x = this.getX() + (10f * GOOSE_SCALE);
        float y = this.getY() - 5f;

        if (fullness > MAX_FULLNESS) fullness = MAX_FULLNESS;
        float fullnessRatio = fullness/MAX_FULLNESS;

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
        fullness = MAX_FULLNESS; // prevents fullness going past max and messing with the fullness bar
        despawnX = GooseUtil.getEnemySpawnX(player); // sets despawn location X
        despawnY = GooseUtil.getEnemySpawnY(player); // Y component
        Full = true;
    }

    // called when goose has reached its despawn location
    public void despawn() {
        Array<Goose> geese = GameScreen.geese;
        int index = 0;
        for (Goose goose : geese) {
            if (goose == this) {
                geese.removeIndex(index);
            }
            index ++;
        }
    }


    // getters
    public float getFullness() { return fullness ;}

    // increments fullness
    public void increaseFullness(float increase) {
        fullness += increase;
    }
}
