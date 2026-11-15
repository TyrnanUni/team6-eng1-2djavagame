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
    private final BitmapFont smallFont;
    private final BitmapFont bigFont;
    private final Viewport viewport = new ScreenViewport();
    private final GlyphLayout layout = new GlyphLayout();

    // split texture into own field so can clear from GPU later
    private final Texture controlsTexture = new Texture("WASDimg.png");
    private final Sprite controls = new Sprite(controlsTexture);


    public ControlScreen(MainGame game) {
        this.game = game;
        batch = game.getBatch();
        bigFont = GooseUtil.getFont(Color.WHITE, 70);
        smallFont = GooseUtil.getFont(Color.WHITE, 33);

        controls.scale(1.5f);
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
        float Control_sprite_X = XCentre - 350;
        float Control_sprite_Y = YCentre - 50;

        // y pos of text
        float y = YCentre + 460;
        float x = XCentre + 200;

        // Draws control scheme help on screen
        int lineSpacing = 35; // line spacing to make spacing easier to change

        layout.setText(bigFont, "GOOSE PATROL"); // layout.width helps us find the center of the text
        bigFont.draw(batch,layout, (XCentre - (layout.width/2)), y);
        y -= lineSpacing * 7;

        layout.setText(smallFont, "W / UP - Up");
        smallFont.draw(batch,layout, x, y);
        y -= lineSpacing;
        layout.setText(smallFont, "A / LEFT - Left");
        smallFont.draw(batch,layout, x, y);
        y -= lineSpacing;
        layout.setText(smallFont, "S / DOWN - Down");
        smallFont.draw(batch,layout, x, y);
        y -= lineSpacing;
        layout.setText(smallFont, "D / RIGHT - Right");
        smallFont.draw(batch,layout, x, y);
        y -= lineSpacing;
        layout.setText(smallFont, "1 - Use 1st item");
        smallFont.draw(batch,layout, x, y);
        y -= lineSpacing;
        layout.setText(smallFont, "2 - Use 2nd item");
        smallFont.draw(batch,layout, x, y);
        y -= lineSpacing;
        layout.setText(smallFont, "R - Restart game");
        smallFont.draw(batch,layout, x, y);
        y -= lineSpacing;
        layout.setText(smallFont, "Esc / Q - Pause / Play");
        smallFont.draw(batch,layout, x, y);
        y -= lineSpacing;
        layout.setText(smallFont, "\n");
        smallFont.draw(batch,layout, x, y);
        y -= lineSpacing;
        layout.setText(smallFont, "Mouse - Point and left-click\nto shoot seeds");
        smallFont.draw(batch,layout, x, y);
        y -= lineSpacing;

        controls.setPosition(Control_sprite_X, Control_sprite_Y);
        controls.draw(batch);
        y -= lineSpacing * 7;

        layout.setText(smallFont, "Press SPACE key to continue...");
        smallFont.draw(batch, layout, (XCentre - (layout.width/2)), y);

        batch.end();
        // end of draw batch
    }

    // clears font and WASD image from GPU
    @Override
    public void dispose() {
        bigFont.dispose();
        smallFont.dispose();
        controlsTexture.dispose();
    }
}