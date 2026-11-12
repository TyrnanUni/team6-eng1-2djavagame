package io.github.goose_patrol;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

// utility for use in other classes to help make code look cleaner and easier to read
public class GooseUtil {

    private static final float RANGE = 170f ; // minimum range from the player that geese can spawn
    private static final float LIMIT = 80f;  // how far away from the minimum the geese can spawn,
                                              // spawning them too far away makes them not impactful and a waste of resources.


    /**
     * Used for enemies
     * @param player gets spawn based on player position
     * @return X position of enemy spawn point. Random.
     */
    public static float getEnemySpawnX(Player player) {
        float Xoffset = (float) Math.random() * LIMIT;
        if (Math.random() >= 0.5) {
            return player.getCenterX() + RANGE + Xoffset;
        }
        else {
            return player.getCenterX() - RANGE - Xoffset;
        }
    }
    /**
     * Used for enemies
     * @param player gets spawn based on player position
     * @return Y position of enemy spawn point. Random.
     */
    public static float getEnemySpawnY(Player player) {
        float Yoffset = (float) Math.random() * LIMIT;
        if (Math.random() >= 0.5) {
            return player.getCenterY() + RANGE + Yoffset;
        }
        else {
            return player.getCenterY() - RANGE - Yoffset;
        }
    }

    /**
     * Takes parameters and generates a bitmap font of the desired color and size from a ttf font
     * @param color color of font
     * @param size size of font
     * @return bitmap font
     */
    public static BitmapFont getFont(Color color, int size) {
        // creates a ttf to bitmap font generator
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("BasicFont.ttf"));  // font file name
        FreeTypeFontGenerator.FreeTypeFontParameter fontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParam.size = size;         // font size
        fontParam.color = color;       // font color

        return generator.generateFont(fontParam); // takes params with ttf font to generate bitmap font
    }

}
