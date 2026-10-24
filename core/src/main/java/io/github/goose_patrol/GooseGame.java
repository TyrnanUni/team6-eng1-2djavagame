package io.github.goose_patrol;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GooseGame extends Game {
    // Runs first and hands over to other Screens when "setScreen" called in create()
    private ShapeRenderer shapeRenderer;
    private Batch batch;
    private BitmapFont font;

    @Override
    public void create() {

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("BasicFont.ttf"));  // font file name
        FreeTypeFontGenerator.FreeTypeFontParameter fontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParam.size = 24;                 // font size
        fontParam.color = Color.WHITE;       // font color

        font = generator.generateFont(fontParam);
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