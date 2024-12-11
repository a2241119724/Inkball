package inkball.tile;

import processing.core.PImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 所有tile的公共操作信息
 * @author Boning Zhang
 * @date 2024/10/18 15:53
 */
public abstract class Tile {
    protected int index;

    private static Map<String,Integer> nameToStartIndex = new HashMap();
    private static List<PImage> images = new ArrayList();

    public String name;

    public Tile(int index,String name) {
        this.index = index;
        this.name = name;
    }

    /**
     * @Description: 添加图片
     * @param null
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:39
     */
    public static void addImage(PImage pImage){
        images.add(pImage);
    }

    public PImage getImage(String name) {
        return images.get(nameToStartIndex.get(name)+index);
    }

    /**
     * @Description: 添加名称到image index的映射
     * @param null
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:39
     */
    public static void addNameToStartIndex(TileType tileType){
        nameToStartIndex.put(tileType.getName(),images.size());
    }

    public int getIndex() {
        return index;
    }
}
