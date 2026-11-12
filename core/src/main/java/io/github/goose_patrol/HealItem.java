package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Texture;

public class HealItem extends InitialItem{

    /**
     * @param texture  item texture
     * @param x        item bottom left x co-ordinate
     * @param y        item bottom left y co-ordinate
     * @param w        item width - also sets rect width
     * @param h        item height - also sets rect height
     * @param COOLDOWN item cooldown - time between item uses
     */
    public HealItem(Texture texture, float x, float y, float w, float h, float COOLDOWN) {
        super(texture, x, y, w, h, COOLDOWN);
    }

    @Override
    public void use() {

    }
}
