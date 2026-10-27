package io.github.goose_patrol;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 *  This class runs first, it calls create() implicitly which sets current
 *  screen to ControlScreen.
 **/
public class MainGame extends Game {
    private Batch batch;
    private BitmapFont font;

    @Override
    public void create() {

        batch = new SpriteBatch();
        // creates a ttf to bitmap font generator
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("BasicFont.ttf"));  // font file name
        FreeTypeFontGenerator.FreeTypeFontParameter fontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParam.size = 33;                 // font size
        fontParam.color = Color.WHITE;       // font color

        font = generator.generateFont(fontParam); // takes params with ttf font to generate bitmap font
        generator.dispose();

        setScreen(new ControlScreen(this));
    }

    // when game closes destroys loaded assets
    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        font.dispose();
    }

    // getters for class attributes - batch, shapeRenderer, font
    public Batch getBatch() { return batch; }

    public BitmapFont getFont() { return font; }
}