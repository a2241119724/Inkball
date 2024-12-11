package inkball.tile;

/**
 * @Description: 所有tile type的枚举
 * @author Boning Zhang
 * @date 2024/10/18 15:53
 */
public enum TileType {
    BALL("ball"), HOLE("hole"), SPACE("space"),
    SPAWNER("spawner"), WALL("wall");

    private String name;

    TileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
