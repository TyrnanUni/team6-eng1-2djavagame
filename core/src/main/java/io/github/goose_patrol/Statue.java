package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Texture;

public class Statue extends Entity{
    // Constants
    private final float HIT_COOLDOWN = 0.5f;
    private final int MAX_HITS = 3;// max hits before statue breaks
    private final Texture brokenTexture;

    // Values
    private int statueIntegreity;
    private float hitTimer;

    // States
    public boolean Broken;


    /**
     * Creates the entities rectangle and sets its attributes
     * Hitbox.x or Hitbox.y gives the bottom left of the circle.
     * @param x bottom left
     * @param y bottom left
     * @param w width
     * @param h height
     * @param normal normal texture
     * @param brokenTexture texture when brokenTexture
     * @param HITBOX_SCALE scale of hitbox
     */
    public Statue(float x, float y, float w, float h, Texture normal, Texture brokenTexture, float HITBOX_SCALE) {
        super(x, y, w, h, normal, HITBOX_SCALE);
        statueIntegreity = MAX_HITS;
        this.brokenTexture = brokenTexture;
        Broken = false;
    }

    @Override
    void update(float delta) {
        if (hitTimer > 0) hitTimer -= delta;
        if (statueIntegreity <= 0) {
            setTexture(brokenTexture);
            Broken = true;
            GameScreen.incrementTriggered(); // only triggers once as statue stops updating when broken
        }
    }

    public void hit() {
        if (hitTimer <= 0) {
            statueIntegreity--;
            hitTimer = HIT_COOLDOWN;
        }
    }

    public boolean isBroken() {
        return Broken;
    }
}
