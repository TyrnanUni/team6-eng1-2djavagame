package io.github.goose_patrol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** First screen of the application. Displayed after the application is created.
 *  It flashes up at the start and waits for an input to continue to the next screen.
 *  - Also shows control scheme.
 **/
public class ControlScreen extends ScreenAdapter {
    private final MainGame game;
    private final Batch batch;
    private final BitmapFont font;
    private final Viewport viewport = new ScreenViewport();
    private final GlyphLayout layout = new GlyphLayout();
    private final Sprite controls = new Sprite(new Texture("WASDimg.png"));


    public ControlScreen(MainGame game) {
        this.game = game;
        batch = game.getBatch();
        font = game.getFont();

        controls.scale(1f);
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        // Screen drawn here. "delta" is the time since last render in seconds.
        // called over and over ~60 times per second
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // when a key is pressed the next screen "GameScreen" is called.
            game.setScreen(new GameScreen(game));
            dispose();
            return; //stops rendering
        }

        // clears screen and applies centred viewport
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        // sends all draw instructions to GPU in a 'batch' - efficient
        batch.begin();

        // gets pos of center of the screen
        float XCentre = viewport.getWorldWidth() / 2;
        float YCentre = viewport.getWorldHeight() / 2;
        float y = YCentre + 190;

        // Draws control scheme help on screen
        int lineSpacing = 35; // line spacing to make spacing easier to change

        layout.setText(font, "GOOSE PATROL"); // layout.width helps us find the center of the text
        font.draw(batch,layout, (XCentre - (layout.width/2)), y);
        y -= lineSpacing * 2;

        layout.setText(font, "Controls:");
        font.draw(batch,layout, (XCentre - (layout.width/2)), y);
        y -= lineSpacing * 2;
        layout.setText(font, "W - Up");
        font.draw(batch,layout, XCentre + 80, y);
        y -= lineSpacing;
        layout.setText(font, "A - Left");
        font.draw(batch,layout, XCentre + 80, y);
        y -= lineSpacing;
        layout.setText(font, "S - Down");
        font.draw(batch,layout, XCentre + 80, y);
        y -= lineSpacing;
        layout.setText(font, "D - Right");
        font.draw(batch,layout, XCentre + 80, y);
        y -= lineSpacing;
        layout.setText(font, "Q - Pause / Play");
        font.draw(batch,layout, XCentre + 80, y);
        y -= lineSpacing;

        controls.setPosition(XCentre - 150, YCentre - 80);
        controls.draw(batch);
        y -= lineSpacing * 2;

        layout.setText(font, "Press SPACE key to continue...");
        font.draw(batch, layout, (XCentre - (layout.width/2)), y);

        batch.end();
        // end of draw batch
    }
}