package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

// the class all players and enemies are from
public abstract class Entity {
    protected final Circle hitbox;
    protected Texture texture;
    protected float textureWidth;
    protected final float textureHeight;

    /**
     * Creates the entities rectangle and sets its attributes
     * @param x pos of bottom left
     * @param y pos of bottom left
     * @param w texture width
     * @param h texture height
     * @param texture the image associated with the entity that will be drawn on screen
     */
    public Entity(float x, float y, float w, float h, Texture texture) {
        textureHeight = h;
        textureWidth = w;
        this.hitbox = new Circle(x + w/2, y + h/2, w/2.5f);
        this.texture = texture;
    }

    // return true if the rectangles of the 2 objects are overlapping
    public boolean overlaps(Entity other) {
        return hitbox.overlaps(other.hitbox);
    }

    /**
     * Is called when we draw the entity on screen.
     * @param batch the batch so it is all drawn together
     */
    public void draw(Batch batch) {
        if (texture == null) return;

        batch.draw(texture, hitbox.x, hitbox.y, textureWidth, textureHeight);
    }

    /**
     * A method that should be used to update the entities attributes or position.
     * @param delta time since render was last called
     */
    abstract void update(float delta);


    /**
     * Used for debugging to highlight hitboxes
     */
    public void drawHitbox(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.PURPLE);
        shapeRenderer.circle(hitbox.x + textureWidth/2, hitbox.y + textureHeight/2, hitbox.radius, 60);
    }
}
