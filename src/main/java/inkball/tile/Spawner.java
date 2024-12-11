package inkball.tile;

/**
 * @Description: Spawner相关操作
 * @author Boning Zhang
 * @date 2024/10/18 15:53
 */
public class Spawner extends Tile {
    private Pos pos;

    public Spawner(int index, Pos pos) {
        super(index,TileType.SPAWNER.getName());
        this.pos = pos;
    }

    public Pos getPos() {
        return pos;
    }
}
