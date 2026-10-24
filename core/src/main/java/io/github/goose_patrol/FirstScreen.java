package io.github.goose_patrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen extends ScreenAdapter {
    // this screen will flash up telling the user the control scheme
    private final GooseGame game;
    private final Batch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final Viewport viewport = new ScreenViewport();
    private final GlyphLayout layout = new GlyphLayout();

    public FirstScreen(GooseGame game) {
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
        // Screen drawn here. "delta" is the time since last render in seconds.
        // called over and over ~60 times per second
        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            // when a key is pressed the next screen "GameScreen" is called.
            game.setScreen(new GameScreen(game));
            dispose();
            return; //stops rendering
        }

        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        // gets pos of center of the screen
        float XCentre = viewport.getWorldWidth() / 2;
        float YCentre = viewport.getWorldHeight() / 2;

        float x = XCentre;
        float y = YCentre + 150;

        layout.setText(font, "GOOSE PATROL"); // layout.width helps us find the center of the text
        font.draw(batch,layout, (XCentre - (layout.width/2)), y);
        y -= 100;

        layout.setText(font, "Controls:"); // layout.width helps us find the center of the text
        font.draw(batch,layout, (XCentre - (layout.width/2)), y);
        y -= 50;

        layout.setText(font, "W, A, S, D / up, left, down, right");
        font.draw(batch, layout, (XCentre - (layout.width/2)), y);
        y -= 50;

        layout.setText(font, "Press any key to continue...");
        font.draw(batch, layout, (XCentre - (layout.width/2)), y);

        batch.end();

    }

    @Override
    public void dispose() {
        super.dispose();
    }
}