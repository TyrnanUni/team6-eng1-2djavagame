package io.github.goose_patrol;

import com.badlogic.gdx.graphics.Texture;

public class ShieldItem extends InitialItem{

    private final float shieldTime = 5f;

    /**
     * @param texture  item texture
     * @param x        item bottom left x co-ordinate
     * @param y        item bottom left y co-ordinate
     * @param w        item width - also sets rect width
     * @param h        item height - also sets rect height
     * @param COOLDOWN item cooldown - time between item uses
     */
    public ShieldItem(Texture texture, float x, float y, float w, float h, float COOLDOWN, Player player) {
        super(texture, x, y, w, h, COOLDOWN, player);
    }

    @Override
    public void use() {
        player.setShield(5);
        GameScreen.incrementCounter();
    }
}
