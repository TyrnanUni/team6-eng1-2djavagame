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

    // split texture into own field so can clear from GPU later
    private final Texture controlsTexture = new Texture("WASDimg.png");
    private final Sprite controls = new Sprite(controlsTexture);


    public ControlScreen(MainGame game) {
        this.game = game;
        batch = game.getBatch();
        font = GooseUtil.getFont(Color.WHITE, 33);

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

        // gets pos of centre of the screen
        float XCentre = viewport.getWorldWidth() / 2;
        float YCentre = viewport.getWorldHeight() / 2;

        // pos of controls sprite
        float Control_sprite_X = XCentre - 200;
        float Control_sprite_Y = YCentre - 155;

        // y pos of text
        float y = YCentre + 190;

        // Draws control scheme help on screen
        int lineSpacing = 35; // line spacing to make spacing easier to change

        layout.setText(font, "GOOSE PATROL"); // layout.width helps us find the center of the text
        font.draw(batch,layout, (XCentre - (layout.width/2)), y);
        y -= lineSpacing * 2;

        layout.setText(font, "Controls:");
        font.draw(batch,layout, (XCentre - (layout.width/2)), y);
        y -= lineSpacing * 2;
        layout.setText(font, "W / UP - Up");
        font.draw(batch,layout, XCentre + 80, y);
        y -= lineSpacing;
        layout.setText(font, "A / LEFT - Left");
        font.draw(batch,layout, XCentre + 80, y);
        y -= lineSpacing;
        layout.setText(font, "S / DOWN - Down");
        font.draw(batch,layout, XCentre + 80, y);
        y -= lineSpacing;
        layout.setText(font, "D / RIGHT - Right");
        font.draw(batch,layout, XCentre + 80, y);
        y -= lineSpacing;
        layout.setText(font, "1 - Use 1st item");
        font.draw(batch,layout, XCentre + 80, y);
        y -= lineSpacing;
        layout.setText(font, "2 - Use 2nd item");
        font.draw(batch,layout, XCentre + 80, y);
        y -= lineSpacing;
        layout.setText(font, "R - Restart game");
        font.draw(batch,layout, XCentre + 80, y);
        y -= lineSpacing;
        layout.setText(font, "Esc / Q - Pause / Play");
        font.draw(batch,layout, XCentre + 80, y);
        y -= lineSpacing;

        controls.setPosition(Control_sprite_X, Control_sprite_Y);
        controls.draw(batch);
        y -= lineSpacing * 2;

        layout.setText(font, "Press SPACE key to continue...");
        font.draw(batch, layout, (XCentre - (layout.width/2)), y);

        batch.end();
        // end of draw batch
    }

    // clears font and WASD image from GPU
    @Override
    public void dispose() {
        font.dispose();
        controlsTexture.dispose();
    }
}