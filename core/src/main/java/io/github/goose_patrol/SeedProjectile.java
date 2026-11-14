package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class SeedProjectile extends Entity{

    private static final float SEED_SPEED = 300f;
    private final Vector2 velocity;
    private static final float CALORIES = 2f;    // how full the seed makes the goose
    private final float rotation;

    /**
     * Creates the entities rectangle and sets its attributes
     * Hitbox.x or Hitbox.y gives the bottom left of the circle.
     *
     * @param x            pos of bottom left
     * @param y            pos of bottom left
     * @param w            texture width
     * @param h            texture height
     * @param texture      the image associated with the entity that will be drawn on screen
     * @param HITBOX_SCALE scales the hitbox
     * @param direction    vector expressing the direction in which the seed should go
     */
    public SeedProjectile(float x, float y, float w, float h, Texture texture, float HITBOX_SCALE, Vector2 direction) {
        super(x, y, w, h, texture, HITBOX_SCALE);

        if (direction.isZero()) direction.set(0, 1);

        velocity = new Vector2(direction).nor();
        rotation = velocity.angleDeg();
    }

    @Override
    void update(float delta) {
        hitbox.x += velocity.x * delta * SEED_SPEED;
        hitbox.y += velocity.y * delta * SEED_SPEED;
    }

    @Override
    public void draw(Batch batch) {
        if (texture == null) return;
        // allows us to apply rotation so the seed faces the way its going
        batch.draw(texture, getX(), getY(),
                getWidth()/2f, getHeight()/2f, getWidth(), getHeight(),
                1f, 1f, rotation, 0, 0, texture.getWidth(), texture.getHeight(),
                false, false);
    }

    // getters
    /**
     * Calories = damage, Fullness = health
     * @return the 'calories' AKA damage value of the seed
     */
    public float getCalories() {
        return CALORIES;
    }
}
