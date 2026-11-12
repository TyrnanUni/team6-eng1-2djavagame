package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

// items that start in inventory
// all initial inventory items should extend this class
public class InitialItem extends Item{

    /**
     * @param texture item texture
     * @param x       item bottom left x co-ordinate
     * @param y       item bottom left y co-ordinate
     * @param w       item width - also sets rect width
     * @param h       item height - also sets rect height
     */
    public InitialItem(Texture texture, float x, float y, float w, float h) {
        super(texture, x, y, w, h);
    }

    @Override
    public void use() {
        //
    }


}
