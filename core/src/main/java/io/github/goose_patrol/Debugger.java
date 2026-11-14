package io.github.goose_patrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;


/**
 * This class is for debugging utility and is not optimized.
 * Remove all methods used from this class before making a release.
 */
public class Debugger {

    private static final ShapeRenderer shapeRenderer = new ShapeRenderer();

    public static void drawAllHitbox(Array<SeedProjectile> seeds, Player player, Array<Goose> geese,  OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        Gdx.gl.glLineWidth(4.0f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (SeedProjectile seed : seeds) {
            seed.drawHitbox(shapeRenderer);
        }

        for(Goose goose : geese) {
            goose.drawHitbox(shapeRenderer);
        }
        player.drawHitbox(shapeRenderer);
        shapeRenderer.end();
    }
}
