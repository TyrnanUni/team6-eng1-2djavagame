package io.github.goose_patrol;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/// not yet implemented fully///
/* Screen after controlScreen that contains the main game */
public class GameScreen extends ScreenAdapter {
    // Constants
    private final float SCREEN_WIDTH = 1162f;
    private final float SCREEN_HEIGHT = 650f;

    // Assets + objects
    private final Batch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport gameView = new FitViewport(SCREEN_WIDTH/3, SCREEN_HEIGHT/3, camera);
    private final GlyphLayout layout = new GlyphLayout();
    private final Texture background = new Texture("Campus_Background.png");
    private final Texture playerTexture = new Texture("Student_Forward.png");
    private final Player player = new Player(SCREEN_WIDTH/2 , SCREEN_HEIGHT/2, gameView, playerTexture);


    // Constructor
    public GameScreen(MainGame game) {
        batch = game.getBatch();
        font = game.getFont();
    }

    // called when window resized
    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0){
            return;
        }
        gameView.update(width, height, false);
    }

    // called when screen is set
    @Override
    public void show() {
        player.reset(SCREEN_WIDTH/2, SCREEN_HEIGHT/2);
    }

    // loops while this is the active screen
    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        player.update(delta); // updates player status - pos, health etc..
        camera.position.set(player.getX() + player.getWidth()/2.0f, player.getY() + player.getHeight()/2.0f, 0.0f);
        camera.update(); // updates camera which makes sure no mismatching happens between player and camera - stops jittering
        batch.setProjectionMatrix(gameView.getCamera().combined);
        gameView.apply(); //reapply viewport
        draw(); // all drawing done here

    }

    // draws the things on the scene
    public void draw() {
        batch.begin();

        batch.draw(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        player.draw(batch);

        batch.end();
    }

    // make sure to dispose of screen unique items here
    @Override
    public void dispose() {
        background.dispose();
    }
}
