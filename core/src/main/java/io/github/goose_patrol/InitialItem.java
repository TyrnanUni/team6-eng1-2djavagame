package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

// items that start in inventory
// all initial inventory items should extend this class
public abstract class InitialItem extends Item{

    /**
     * @param texture   item texture
     * @param x         item bottom left x co-ordinate
     * @param y         item bottom left y co-ordinate
     * @param w         item width - also sets rect width
     * @param h         item height - also sets rect height
     * @param COOLDOWN  item cooldown - time between item uses
     */
    public InitialItem(Texture texture, float x, float y, float w, float h, float COOLDOWN) {
        super(texture, x, y, w, h, COOLDOWN);
    }

    public abstract void use();

    public void update(float delta, boolean activated){
        if (activated && cooldownLeft <= 0) {
            cooldownLeft = COOLDOWN;
            use();
        }
        if (cooldownLeft >= 0) cooldownLeft -= delta;
    }

}
