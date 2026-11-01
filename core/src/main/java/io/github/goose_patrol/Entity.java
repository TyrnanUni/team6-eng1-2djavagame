package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

// the class all players and enemies are from
public abstract class Entity {
    protected final Rectangle rect;
    protected Texture texture;

    /**
     * Creates the entities rectangle and sets its attributes
     * @param x pos of bottom left
     * @param y pos of bottom left
     * @param w desired width of rectangle - usually the same as texture
     * @param h desired height of rectangle - usually the same as the texture
     * @param texture the image associated with the entity that will be drawn on screen
     */
    public Entity(float x, float y, float w, float h, Texture texture) {
        this.rect = new Rectangle(x, y, w, h);
        this.texture = texture;
    }

    // return true if the rectangles of the 2 objects are overlapping
    public boolean overlaps(Entity other) {
        return rect.overlaps(other.rect);
    }

    /**
     * Is called when we draw the entity on screen.
     * @param batch the batch so it is all drawn together
     */
    public void draw(Batch batch) {
        if (texture == null) return;

        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
    }

    /**
     * A method that should be used to update the entities attributes or position.
     * @param delta time since render was last called
     */
    abstract void update(float delta);

}
