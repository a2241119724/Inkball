package inkball.tile;

import processing.core.PImage;

/**
 * @Description: Wall相关操作
 * @author Boning Zhang
 * @date 2024/10/18 15:53
 */
public class Wall extends Tile {
    private int hit = 3;
    private static PImage[] hitImages = new PImage[5];
    private long lastCollision = System.currentTimeMillis();

    public Wall(int index) {
        super(index,TileType.WALL.getName());
    }

    /**
     * @Description: 添加墙体损坏图片
     * @param hitImages
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:42
     */
    public static void setHitImages(PImage[] hitImages){
        Wall.hitImages = hitImages;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public void subOne(){
        if(System.currentTimeMillis()-lastCollision<50){
            lastCollision = System.currentTimeMillis();
            return;
        }
        lastCollision = System.currentTimeMillis();
        hit -= 1;
    }

    public int getHit() {
        return hit;
    }

    @Override
    public PImage getImage(String name) {
        if(hit!=3){
            return hitImages[index];
        }
        return super.getImage(name);
    }
}
