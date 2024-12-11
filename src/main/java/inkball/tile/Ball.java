package inkball.tile;

import inkball.App;
import inkball.view.Board;
import inkball.view.LineManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: Ball相关操作
 * @author Boning Zhang
 * @date 2024/10/18 15:53
 */
public class Ball extends Tile {
    public static int BALL_SIZE = 24;

    private Pos pos;
    private Pos direction;
    private final int MIN_SIZE = 4;
    private final int MAX_SIZE = BALL_SIZE;
    private boolean isEnterHole = false;
    private int curSize = BALL_SIZE;
    private float score = 1.f;
    private static Map<String,Integer> colorToIndex;
    private static Map<Integer,String> indexToColor = new HashMap();

    public Ball(int index,Pos pos) {
        super(index,TileType.BALL.getName());
        this.pos = pos;
        this.direction = new Pos(App.random.nextFloat()>0.5?2.f:-2.f,App.random.nextFloat()>0.5?2.f:-2.f);
    }

    public void move(){
        if(Board.isPause||Board.isGameOver()||Board.isNoTime()) return;
        pos.x += direction.x;
        pos.y += direction.y;
    }

    /**
     * @Description: 碰撞检测(Wall and Ball),进洞处理
     * @param board 面板管理
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 15:50
     */
    public void collisionAndSuccessCheck(Board board) {
        if (pos.x-BALL_SIZE/2 <= 0 || pos.x+BALL_SIZE/2 >= App.WIDTH) {
            direction.x *= -1;
        }
        if (pos.y-BALL_SIZE/2 <= App.TOPBAR || pos.y+BALL_SIZE/2 >= App.HEIGHT) {
            direction.y *= -1;
        }
        boolean isCollision = false;
        Pos closedColliderDistance = new Pos(999.f,999.f);
        Pos closedColliderPos = new Pos(0.f,0.f);
        int[] closedColliderTile = new int[2];
        int closedColliderColor = 0;
        Pos closedHolePos = new Pos(999.f,999.f);
        for (int i = 0; i < board.getTileHeight(); i++) {
            for (int j = 0; j < board.getTileWidth(); j++) {
                Tile tile = board.getBoard(i, j);
                if(tile.name==TileType.HOLE.getName()){
                    Pos holePos = new Pos(board.getPosX(j) + App.CELLSIZE,board.getPosY(i) + App.CELLSIZE);
                    float distance = pos.getDistance(holePos);
                    if(distance<32+BALL_SIZE/2){
                        direction = Pos.addPos(direction,Pos.mul(Pos.subPos(holePos,pos),0.005f));
                        curSize = Math.min(Math.max(MIN_SIZE,(int)distance),MAX_SIZE);
                        if(distance<10.f){
                            isEnterHole = true;
                            if(tile.getIndex()==0||index==0||tile.getIndex() == index){
                                if(board.getBallQueueManager().getSize()==0&&board.getBalls().size()==1){
                                    board.setSuccess(true);
                                }
                                Map<String,Integer> scoreFromHoleCapture = Board.getConfig().getScoreIncreaseFromHoleCapture();
                                score = scoreFromHoleCapture.get(indexToColor.get(index)) *
                                    Board.getConfig().getLevels().get(Board.getCurLevel()-1).getScoreIncreaseFromHoleCaptureModifier();
                            }else{
                                Map<String,Integer> scoreFromHoleCapture = Board.getConfig().getScoreDecreaseFromWrongHole();
                                score = scoreFromHoleCapture.get(indexToColor.get(index)) * -1 *
                                    Board.getConfig().getLevels().get(Board.getCurLevel()-1).getScoreDecreaseFromWrongHoleModifier();
                                board.getBallQueueManager().addBall(this);
                            }
                        }
                        float _distance = pos.getDistance(closedHolePos);
                        if(distance<_distance){
                            closedHolePos = holePos;
                        }
                    } else{
                        direction.normal();
                        direction = Pos.mul(direction,2 * (float)Math.sqrt(2));
                    }
                }else if(tile.name!=TileType.WALL.getName()) continue;
                float detX = Math.abs(pos.x-(board.getPosX(j)+App.CELLSIZE/2.f));
                float detY = Math.abs(pos.y-(board.getPosY(i)+App.CELLSIZE/2.f));
                Pos wallPos = new Pos(board.getPosX(j) + App.CELLSIZE/2,board.getPosY(i) + App.CELLSIZE/2);
                float distance = pos.getDistance(wallPos);
                float collisionMinLen = (App.CELLSIZE / 2) / (Pos.dotMul(pos, wallPos) / (Pos.length(pos) * Pos.length(wallPos))) + BALL_SIZE/2;
                if(distance<=collisionMinLen){
                    isCollision = true;
                    closedColliderTile[0] = i;
                    closedColliderTile[1] = j;
                    closedColliderColor = tile.getIndex();
                    closedColliderDistance.x = detX;
                    closedColliderDistance.y = detY;
                    closedColliderPos.x = board.getPosX(j)+App.CELLSIZE/2.f;
                    closedColliderPos.y = board.getPosY(i)+App.CELLSIZE/2.f;
                }
            }
        }
        if(isCollision){
            if(((Wall)board.getBoard(closedColliderTile[0],closedColliderTile[1])).getHit()==1){
                board.setTileForHit(closedColliderTile[0],closedColliderTile[1]);
            }else{
                if(board.getBoard(closedColliderTile[0],closedColliderTile[1]).getIndex()==index||
                    board.getBoard(closedColliderTile[0],closedColliderTile[1]).getIndex()==0)
                ((Wall)board.getBoard(closedColliderTile[0],closedColliderTile[1])).subOne();
            }
            if(closedColliderDistance.x>=closedColliderDistance.y){
                if(direction.x*(closedColliderPos.x-pos.x)>0){
                    direction.x *= -1;
                }
            }else{
                if(direction.y*(closedColliderPos.y-pos.y)>0){
                    direction.y *= -1;
                }
            }
            if(closedColliderColor!=0){
                index = closedColliderColor;
            }
        }
        curSize = Math.min(Math.max(MIN_SIZE,(int)pos.getDistance(closedHolePos)),MAX_SIZE);
        collisionCheck(board.getLineManager());
    }

    /**
     * @Description: 碰撞检测(Ball and Line)
     * @param lineManager
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 15:53
     */
    public void collisionCheck(LineManager lineManager) {
        boolean isCollision = false;
        for (List<Pos> line : lineManager.getLines()) {
            int size = line.size();
            for (int i = 1; i < size; i++) {
                float det = pos.getDistance(line.get(i - 1)) + pos.getDistance(line.get(i)) - line.get(i - 1).getDistance(line.get(i));
                if(Math.abs(det) < BALL_SIZE+5){
                    // u=v-2(v dot n)n
                    float dx = line.get(i).x - line.get(i-1).x;
                    float dy = line.get(i).y - line.get(i-1).y;
                    Pos n = new Pos(dy,-dx);
                    if(n.x*(pos.x-line.get(i).x)>0 && n.y*(pos.y-line.get(i).y)>0){
                        n = new Pos(-dy,dx);
                    }
                    n.normal();
                    float dot = direction.x * n.x + direction.y * n.y;
                    direction.x = direction.x-2*dot*n.x;
                    direction.y = direction.y-2*dot*n.y;
                    isCollision = true;
                    break;
                }
            }
            if(isCollision){
                lineManager.removeLine(line);
                break;
            }
        }
    }

    public void setPos(Pos pos) {
        this.pos = pos;
    }

    public void setPos(float x,float y) {
        this.pos = new Pos(x,y);
    }

    public void setEnterHole(boolean enterHole) {
        isEnterHole = enterHole;
    }

    public static void setColorToIndex(Map<String, Integer> colorToIndex) {
        Ball.colorToIndex = colorToIndex;
        for (Map.Entry<String, Integer> entry : colorToIndex.entrySet()) {
            indexToColor.put(entry.getValue(),entry.getKey());
        }
    }

    public static Integer getColorToIndex(String color) {
        return colorToIndex.get(color);
    }

    public float getScore() {
        if(isEnterHole){
            return score;
        }
        return 0.f;
    }

    public Pos getPos() {
        return pos;
    }

    public boolean isEnterHole() {
        return isEnterHole;
    }

    public int getCurSize() {
        return curSize;
    }
}
