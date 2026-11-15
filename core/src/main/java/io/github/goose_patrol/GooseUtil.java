package io.github.goose_patrol;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;

// utility for use in other classes to help make code look cleaner and easier to read
public class GooseUtil {

    private static final float RANGE = 240f ; // minimum range from the player that geese can spawn
    private static final float LIMIT = 80f;  // how far away from the minimum the geese can spawn,
                                              // spawning them too far away makes them not impactful and a waste of resources.

    static float colorTimer = 0;


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

        BitmapFont font = generator.generateFont(fontParam); // takes params with ttf font to generate bitmap font
        generator.dispose();
        return font;
    }

    /**
     * Returns the number of key pressed if it corresponds to an item.
     * @return int num key pressed. returns -1 if none apply
     */
    public static int getItemUsed() {
        // can add more but we only need 2 for now
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) return 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) return 2;
        return -1;
    }

    /**
     * Takes 2 entities and separates them.
     * @param target - the entity that should be moved
     * @param actor - the entity doing the moving.
     */
    public static void separate(Entity target, Entity actor) {
        float distance = Vector2.dst(target.hitbox.x, target.hitbox.y, actor.hitbox.x, actor.hitbox.y);
        float minDistance = target.hitbox.radius + actor.hitbox.radius;

        if (distance < minDistance && distance > 0) {
            float overlap = minDistance - distance;
            // Push each goose away from the other by half the overlap distance
            Vector2 separationDir = new Vector2(target.hitbox.x - actor.hitbox.x, target.hitbox.y - actor.hitbox.y).nor();

            target.hitbox.x += separationDir.x * (overlap * 0.5f);
            target.hitbox.y += separationDir.y * (overlap * 0.5f);
        }
    }

    /**
     * Generates colours for rainbow efects
     * @return Color
     */
    public static Color rainbowColors() {
        colorTimer += Gdx.graphics.getDeltaTime() * 60f;
        float hue = colorTimer % 360f; // creates hue based off game uptime

        Color color = new Color();     // color object
        color.fromHsv(hue, 1f, 1f);    // HSV to RGB
        color.a = 1f;                  // alpha from 0f to 1f

        return color;
    }



}
