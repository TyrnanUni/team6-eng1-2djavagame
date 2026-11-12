package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Class to express items.
 * Inherited by classes expressing types of item. Draw method scales to fit
 */
public abstract class Item {
    //Objects
    protected final Texture texture;
    protected Rectangle rect;

    //Values
    protected final float textureWidth;
    protected final float textureHeight;
    protected float cooldownLeft = 0;

    //Constants
    protected final float COOLDOWN;

    /**
     * @param texture item texture
     * @param x item bottom left x co-ordinate
     * @param y item bottom left y co-ordinate
     * @param w item width - also sets rect width
     * @param h item height - also sets rect height
     */
    public Item(Texture texture, float x, float y, float w, float h, float COOLDOWN) {
        this.texture = texture;
        textureWidth = w;
        textureHeight = h;
        rect = new Rectangle(x, y, w, h);
        this.COOLDOWN = COOLDOWN;
    }

    /**
     * @param batch the draw batch
     */
    public void draw(Batch batch) {
        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
    }

    public abstract void update(float delta, boolean activated);

    // getters
    public float getX() {
        return rect.x;
    }

    public float getY() { return rect.y; }
    public float getWidth() {
        return textureWidth;
    }

    public float getHeight() {
        return textureHeight;
    }
}

