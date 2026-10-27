package io.github.goose_patrol;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 *  This class runs first, it calls create() implicitly which sets current
 *  screen to FirstScreen.
 **/
public class GooseGame extends Game {
    private ShapeRenderer shapeRenderer;
    private Batch batch;
    private BitmapFont font;

    @Override
    public void create() {

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // creates a ttf to bitmap font generator
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("BasicFont.ttf"));  // font file name
        FreeTypeFontGenerator.FreeTypeFontParameter fontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParam.size = 24;                 // font size
        fontParam.color = Color.WHITE;       // font color

        font = generator.generateFont(fontParam); // takes params with ttf font to generate bitmap font
        generator.dispose();

        setScreen(new FirstScreen(this));
    }

    // when game closes destroys loaded assets
    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }


    // getters for class attributes - batch, shapeRenderer, font
    public Batch getBatch() {
        return batch;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public BitmapFont getFont() {
        return font;
    }
}