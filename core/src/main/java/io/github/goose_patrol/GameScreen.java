package io.github.goose_patrol;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/// not yet implemented fully///

public class GameScreen extends ScreenAdapter {
    // main game screen loads after first screen

    // Assets + objects
    private final GooseGame game;
    private final Batch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final Viewport viewport;
    private final GlyphLayout layout = new GlyphLayout();
    private final TextureRegion background;

    // Values
    private final float viewportScaler; // scales the size of the viewport without affecting the resolution ratio

    public GameScreen(GooseGame game) {
        this.game = game;
        this.batch = game.getBatch();
        this.font = game.getFont();
        this.shapeRenderer = game.getShapeRenderer();
        background = new TextureRegion(new Texture("Campus_Background.png"));
        viewportScaler = 70;
        viewport = new FitViewport(4 * viewportScaler, 3 * viewportScaler);
        // FitViewport keeps the experience consistent for all aspect ratios but can introduce black bars
        // if the window is resized.
        // ^^^ this may be fixable if we can make the window only resizable from a corner? ? ?
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
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        ScreenUtils.clear(Color.BLACK);
        draw();
    }

    public void draw() {
        // draws the things on scene that will always be there
        // i.e. background, ui, hotbar? etc...
        final float backgroundHeight = 739.0f;
        final float backgroundWidth = 1162.0f;

        batch.begin();

        batch.draw(background, 0, 0, backgroundWidth, backgroundHeight);

        batch.end();
    }

    // make sure to dispose of screen unique items here
    @Override
    public void dispose() {
        super.dispose();
    }
}
