package io.github.goose_patrol;


// utility for use in other classes to help make it look cleaner and easier to read
public class GameLogic {
    private static final float RANGE = 140f ;

    private GameLogic() {
    }


    /**
     * Used for enemies
     * @param player gets spawn based on player position
     * @return X position of enemy spawn point. Random.
     */
    public static float getEnemySpawnX(Player player) {
        float Xoffset = (float) Math.random() * 150;
        if (Math.random() >= 0.5) {
            return player.getX() + RANGE + Xoffset;
        }
        else {
            return player.getX() - RANGE - Xoffset;
        }
    }
    /**
     * Used for enemies
     * @param player gets spawn based on player position
     * @return Y position of enemy spawn point. Random.
     */
    public static float getEnemySpawnY(Player player) {
        float Yoffset = (float) Math.random() * 150;
        if (Math.random() >= 0.5) {
            return player.getY() + RANGE + Yoffset;
        }
        else {
            return player.getY() - RANGE - Yoffset;
        }
    }

}
