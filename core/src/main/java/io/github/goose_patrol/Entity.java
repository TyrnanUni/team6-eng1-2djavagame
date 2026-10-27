package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

// the class all players and enemies are from
public abstract class Entity {

    protected final Rectangle rect;
    protected Texture texture;

    //Constructor -  pos of bottom left corner, width and height, texture
    public Entity(float x, float y, float w, float h, Texture texture) {
        this.rect = new Rectangle(x, y, w, h);
        this.texture = texture;
    }

    // return true if the rectangles of the 2 objects are overlapping
    public boolean overlaps(Entity other) {
        return rect.overlaps(other.rect);
    }

    // draws entity
    public void draw(Batch batch) {
        if (texture == null) return;

        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
    }

    abstract void update(float delta);

}
