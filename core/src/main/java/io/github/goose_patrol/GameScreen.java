package io.github.goose_patrol;

import com.badlogic.gdx.ScreenAdapter;

/// not yet implemented ///

public class GameScreen extends ScreenAdapter {
    // main game screen loads after first screen
    private final GooseGame game;

    public GameScreen(GooseGame game) {
        this.game = game;
    }
}
