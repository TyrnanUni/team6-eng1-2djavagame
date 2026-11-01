package io.github.goose_patrol;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 *  This class runs first, it calls create() implicitly which sets current
 *  screen to ControlScreen.
 **/
public class MainGame extends Game {
    private Batch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new ControlScreen(this));
    }

    // when game closes destroys loaded assets
    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }

    // getters for class attributes - batch, shapeRenderer, font
    public Batch getBatch() { return batch; }
}