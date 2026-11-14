package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

// the class all players and enemies are from
public abstract class Entity {
    //hitbox
    protected final Circle hitbox;
    protected float HITBOX_SCALE;

    //Entity attributes
    protected Texture texture;
    protected float textureWidth;
    protected float textureHeight;

    /**
     * Creates the entities rectangle and sets its attributes
     * Hitbox.x or Hitbox.y gives the bottom left of the circle.
     *
     * @param x pos of bottom left
     * @param y pos of bottom left
     * @param w texture width
     * @param h texture height
     * @param texture the image associated with the entity that will be drawn on screen
     * @param HITBOX_SCALE scales the hitbox
     */
    public Entity(float x, float y, float w, float h, Texture texture, float HITBOX_SCALE) {
        textureHeight = h; // can be changed to scale the texture to fit when drawn
        textureWidth = w;  // can be changed to scale the texture to fit when drawn

        this.HITBOX_SCALE = HITBOX_SCALE;

        this.hitbox = new Circle(x + textureWidth/2, y + textureHeight/2, textureWidth*HITBOX_SCALE);
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
        batch.draw(texture, getX(), getY(), textureWidth, textureHeight);
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
        shapeRenderer.circle(getCenterX(), getCenterY(), hitbox.radius, 60);
    }

    // setters
    public void setTexture(Texture texture) {this.texture = texture;}

    //getters
    public float getCenterX() { return hitbox.x;}

    public float getCenterY() { return hitbox.y;}

    public float getX() { return hitbox.x - textureWidth/2f;}

    public float getY() { return hitbox.y - textureHeight/2f;}

    public float getWidth() {return textureWidth;}

    public float getHeight() {return textureHeight;}

}
