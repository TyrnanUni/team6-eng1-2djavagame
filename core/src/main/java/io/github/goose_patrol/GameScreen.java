package io.github.goose_patrol;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/// not yet implemented fully///

public class GameScreen extends ScreenAdapter {
    // main game screen loads after first screen
    private final GooseGame game;
    private final Batch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final Viewport viewport = new ScreenViewport();
    private final GlyphLayout layout = new GlyphLayout();

    public GameScreen(GooseGame game) {
        this.game = game;
        this.batch = game.getBatch();
        this.font = game.getFont();
        this.shapeRenderer = game.getShapeRenderer();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        viewport.update(width, height, true);
        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void render(float delta) {
        // render logic, may need another class to handle the game logic itself.
        // drawing done in GameScreen.draw() method bellow
    }

    public void draw() {
        // draw loop
    }

    public void dispose() {
        super.dispose();
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
