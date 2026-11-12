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
    private static Viewport gameview;

    public static void drawAllHitbox(Player player, Array<Goose> geese, Viewport viewport, OrthographicCamera camera) {
        gameview = viewport;
        shapeRenderer.setProjectionMatrix(camera.combined);

        Gdx.gl.glLineWidth(4.0f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for(Goose goose : geese) {
            goose.drawHitbox(shapeRenderer);
        }
        player.drawHitbox(shapeRenderer);
        shapeRenderer.end();
    }
}
