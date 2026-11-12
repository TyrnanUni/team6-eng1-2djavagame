package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

// items that are found as pickups across the map
// all map pickup items should extend this class
public class PickUpItem extends Item{

    /**
     * @param texture item texture
     * @param x       item bottom left x co-ordinate
     * @param y       item bottom left y co-ordinate
     * @param w       item width - also sets rect width
     * @param h       item height - also sets rect height
     */
    public PickUpItem(Texture texture, float x, float y, float w, float h) {
        super(texture, x, y, w, h);
    }

    @Override
    public void draw(Batch batch) {
        // should override draw to allow it to be drawn on the map as well as UI
        // not as simple to draw as for InitialItem
    }

    @Override
    public void use() {
        // use item here
    }
}
