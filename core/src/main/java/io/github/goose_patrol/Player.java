package io.github.goose_patrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * A class expressing a Player. Can be controlled by user input.
 */
public class Player extends Entity{

    // Constants - will be changed later but just some arbitrary values for now
    private final float SPEED = 120f;
    private final float HIT_COOLDOWN = 0.9f;
    public final float MAX_HEALTH = 10f;
    public float healingPotency;

    // Values
    private int hits;
    private float health;
    private float hitTimer;
    private float healingTimer;
    private float shield;


    //Objects
    public final Vector2 velocity = new Vector2();

    /**
     * Constructor
     * @param x bottom left starting x pos
     * @param y bottom left starting y pos
     * @param texture the player texture
     */
    public Player(float x, float y, float w, float h, Texture texture, float HITBOX_SCALE) {
        super(x, y, w, h, texture, HITBOX_SCALE);
    }


    /**
     * called when game starts/ restarts
     * @param x bottom left starting x pos
     * @param y bottom left starting y pos
     */
    public void reset(float x, float y) {
        hitbox.setPosition(x, y);
        health = MAX_HEALTH;
        hitTimer = 0;
        shield = 0;
        healingTimer = 0;
    }

    /**
     * Updates status' of player. Input, hit cooldown etc...
     * @param delta time since last render
     */
    @Override
    public void update(float delta) {
        Input(delta);
        if (hitTimer > 0) hitTimer -= delta;

        if (shield > 0 ) {
            shield -= delta;
        }
        if (healingTimer > 0) {
            healingTimer -= delta;
            if (health < MAX_HEALTH) {
                health += healingPotency * delta;
            }
        }
        if (health < 0f) health = 0f;
    }

    public void hit(float damage) {
        if (hitTimer <= 0 && shield <= 0) {
            ++hits;
            health -= damage;
            hitTimer = HIT_COOLDOWN;
            shield = 0;
        }
    }

    // flashes red when hit (and duration of hit cool down)
    @Override
    public void draw(Batch batch) {
        // flashes red when hit
        if (hitTimer > 0) {
            int flashStep = (int) (hitTimer * 10);
            if (flashStep % 2 == 0) {
                batch.setColor(1f, 0.6f, 0.6f, 1f);
            }
        }
        // flashes blue when shielded
        if (shield > 0) {
            int flashStep = (int) (shield * 7);
            if (flashStep % 2 == 0) {
                batch.setColor(0.7f, 0.7f, 1f, 1f);
            }

        }
        // flashes green when healing
        if (healingTimer > 0) {
            int flashStep = (int) (healingTimer * 7);
            if (flashStep % 2 == 0) {
                batch.setColor(0.7f, 1f, 0.7f, 1f);
            }
        }

        super.draw(batch);
        batch.setColor(Color.WHITE);
    }

    /**
     * checks for input and adjusts position on screen accordingly
     * @param delta time since last render
     */
    public void Input(float delta) {
        velocity.set(0, 0); // resets velocity so doesn't keep going after stopped pressing

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))    velocity.y += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))  velocity.x -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))  velocity.y -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) velocity.x += 1f;

        if (!velocity.isZero()) velocity.nor();

        hitbox.x += velocity.x * delta * SPEED;
        hitbox.y += velocity.y * delta * SPEED;

    }

    /**
     * Stops player leaving the map - edges act as walls.
     */
    public void keepInside(float mapWidth, float mapHeight) {
        hitbox.x = MathUtils.clamp(hitbox.x, getWidth()/2f, mapWidth - getWidth()/2f);
        hitbox.y = MathUtils.clamp(hitbox.y, getHeight()/2f, mapHeight - getHeight()/2f);
    }


    // getters
    public float getHealth() { return health; }

    public int getHits() { return hits; }

    public boolean isShielded() { return shield > 0;}

    public boolean isHealing() { return healingTimer > 0;}

    // setters
    public void setShield(float time) {
        shield = time;
    }

    public void setHealingTimer(float time) {
        healingTimer = time;
    }

    public void setHealingPotency(float potency) {
        healingPotency = potency;
    }
}
