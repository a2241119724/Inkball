package inkball.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import inkball.App;
import inkball.config.Config;
import inkball.tile.*;
import processing.data.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 面板控制相关操作
 * @author Boning Zhang
 * @date 2024/10/18 15:53
 */
public class Board{
    public static boolean isPause = false;
    private static Config config;
    private static int curLevel = 1;

    public Tile[][] tiles;
    private final int tileWidth = App.BOARD_WIDTH;
    private final int tileHeight = App.BOARD_HEIGHT-App.TOPBAR/ App.CELLSIZE;
    private List<Ball> balls = new ArrayList();
    private List<Spawner> spawners = new ArrayList();

    private LineManager lineManager = new LineManager();
    private TimeManager timeManager = new TimeManager();
    private BallQueueManager ballQueueManager = new BallQueueManager();
    private boolean isSuccess = false;
    public static boolean isGameOver = false;
    private static boolean isNoTime = false;
    private int[] startPosForSuccess = new int[]{0,0};
    private int[] lastColor = new int[2];
    private Tile[] lastTile = new Tile[2];
    private boolean isNextLevel = false;

    public Board() {
        tiles = new Tile[App.BOARD_HEIGHT][App.BOARD_WIDTH];
    }

    /**
     * @Description: parse config.json
     * @param json
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:15
     */
    public void parseConfig(JSONObject json){
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS,false);
        try {
            config = mapper.readValue(json.toString(), Config.class);
        } catch (JsonProcessingException e) {
            System.out.println("parse json error!!!");
        }
    }

    /**
     * @Description: 获取level对应的文件名
     * @param level
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:16
     */
    public String levelToFileName(int level){
        this.curLevel = level;
        ballQueueManager.setInterval(config.getLevels().get(level-1).getSpawnInterval());
        timeManager.setTime(config.getLevels().get(level-1).getTime());
        return config.getLevels().get(level-1).getLayout();
    }

    /**
     * @Description: 切换关卡时,对地图中的tile进行创建
     * @param layout 从level*.txt文件中读取的内容
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:17
     */
    public void switchLevel(String[] layout){
        for (int i = 0; i < tileHeight; i++) {
            for (int j = 0; j < tileWidth; j++) {
                char c = layout[i].charAt(j);
                if(c=='X'){
                    tiles[i][j] = new Wall(0);
                }else if(c>='0'&&c<='9'){
                    int index = c-'0';
                    if(j-1<0){
                        tiles[i][j] = new Wall(index);
                        continue;
                    }
                    c = layout[i].charAt(j-1);
                    if(c=='B'||c=='H'){
                        tiles[i][j] = new Space(0);
                    }else{
                        tiles[i][j] = new Wall(index);
                    }
                }else if(c=='S'){
                    tiles[i][j] = new Spawner(0,new Pos(getPosX(j),getPosY(i)));
                    spawners.add((Spawner) tiles[i][j]);
                }else if(c=='H'){
                    int index = 0;
                    c = layout[i].charAt(j+1);
                    if(c>='0'&&c<='9'){
                        index = c-'0';
                    }
                    tiles[i][j] = new Hole(index);
                }else{
                    tiles[i][j] = new Space(0);
                }
                c = layout[i].charAt(j);
                if(c=='B'){
                    int index = 0;
                    c = layout[i].charAt(j+1);
                    if(c>='0'&&c<='9'){
                        index = c-'0';
                    }
                    balls.add(new Ball(index,new Pos(getPosX(j)+App.CELLSIZE/2,getPosY(i)+App.CELLSIZE/2)));
                }
            }
        }
    }

    /**
     * @Description: 初始化所有相关信息
     * @param
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:18
     */
    public void clear(){
        lineManager.clear();
        balls.clear();
        ballQueueManager.setBalls(new ArrayList());
        ballQueueManager.clear();
        isSuccess = false;
        isGameOver = false;
    }

    public static boolean isGameOver() {
        return isGameOver;
    }

    public void checkGameOver() {
        if(isMaxLevel()){
            isGameOver=true;
        }
    }

    public static void addLevel() {
        Board.curLevel %= config.getLevels().size();
        Board.curLevel += 1;
    }

    /**
     * @Description: 本轮胜利之后,进行动画处理
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:19
     */
    public void showForSuccess(){
        Tile tile1 = tiles[startPosForSuccess[0]][startPosForSuccess[1]];
        Tile tile2 = tiles[tileHeight - 1 - startPosForSuccess[0]][tileWidth - 1 - startPosForSuccess[1]];
        if(lastTile[0] instanceof Wall){
            ((Wall)lastTile[0]).setIndex(lastColor[0]);
        }
        if(lastTile[1] instanceof Wall){
            ((Wall)lastTile[1]).setIndex(lastColor[1]);
        }
        lastColor[0] = tile1.getIndex();
        lastColor[1] = tile2.getIndex();
        lastTile[0] = tile1;
        lastTile[1] = tile2;
        if(tile1 instanceof Wall){
            ((Wall)tiles[startPosForSuccess[0]][startPosForSuccess[1]]).setIndex(4);
        }
        if(tile2 instanceof Wall){
            ((Wall)tiles[tileHeight-1-startPosForSuccess[0]][tileWidth-1-startPosForSuccess[1]]).setIndex(4);
        }
        if(startPosForSuccess[0]==0&&startPosForSuccess[1]!=tileWidth-1){
            startPosForSuccess[1]++;
        } else if (startPosForSuccess[1]==tileWidth-1&&startPosForSuccess[0]!=tileHeight-1) {
            startPosForSuccess[0]++;
        }else if (startPosForSuccess[0]==tileHeight-1&&startPosForSuccess[1]!=0) {
            startPosForSuccess[1]--;
        }else if (startPosForSuccess[1]==0) {
            startPosForSuccess[0]--;
        }
    }

    public static boolean isNoTime() {
        return isNoTime;
    }

    public static void setIsNoTime(boolean isNoTime) {
        Board.isNoTime = isNoTime;
    }

    public void setTileForHit(int x, int y) {
        tiles[x][y] = new Space(0);
    }

    public boolean isMaxLevel(){
        return config.getLevels().size()== curLevel;
    }

    public boolean isNextLevel() {
        return isNextLevel;
    }

    public void setNextLevel(boolean nextLevel) {
        isNextLevel = nextLevel;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public BallQueueManager getBallQueueManager() {
        return ballQueueManager;
    }

    public static Config getConfig() {
        return config;
    }

    public static int getCurLevel() {
        return curLevel;
    }

    public List<Spawner> getSpawners() {
        return spawners;
    }

    public void addBall(Ball ball){
        balls.add(ball);
    }

    public LineManager getLineManager() {
        return lineManager;
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public Tile getBoard(int x, int y) {
        return tiles[x][y];
    }

    public float getPosX(int x) {
        return x * App.CELLSIZE;
    }
    public float getPosY(int y) {
        return y*App.CELLSIZE+App.TOPBAR;
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }
}
