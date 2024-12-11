package inkball.view;

import inkball.App;
import inkball.tile.Ball;
import inkball.tile.Pos;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 待生成球的管理操作
 * @author Boning Zhang
 * @date 2024/10/18 15:53
 */
public class BallQueueManager{
    private static long startTime = System.currentTimeMillis();
    private static long pauseTime = System.currentTimeMillis();
    private float interval = 10.f;
    private final Pos startPos = new Pos(10,20);
    private final int offset_x = 5;
    private List<Ball> balls;

    public BallQueueManager() {
        balls = new ArrayList();
    }

    /**
     * @Description: 当有球被孵化时，做左移动画效果
     * @param null
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:14
     */
    public void reSort(){
        if(balls.size()==0) return;
        float offset_x = balls.get(0).getPos().x;
        for (Ball ball : balls) {
            if(offset_x>10){
                ball.setPos(ball.getPos().x-1,ball.getPos().y);
            }
        }
    }

    /**
     * @Description: 设置队列球中的位置信息
     * @param null
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:13
     */
    public void initBalls() {
        float posX = startPos.x;
        for (String ball : Board.getConfig().getLevels().get(Board.getCurLevel() - 1).getBalls()) {
            balls.add(new Ball(Ball.getColorToIndex(ball),new Pos(posX,startPos.y)));
            posX += Ball.BALL_SIZE + offset_x;
        }
    }

    /**
     * @Description: 检查在剩余时间为0后在随机一个Spawner处产生球
     * @param board
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:04
     */
    public void addBallCheck(Board board){
        if(balls.size()==0) {
            startTime = System.currentTimeMillis();
        }else if(System.currentTimeMillis()-startTime>interval*1000){
            Ball firstBall = balls.get(0);
            balls.remove(firstBall);
            int index = App.random.nextInt(board.getSpawners().size());
            Pos pos = board.getSpawners().get(index).getPos();
            firstBall.setPos(Pos.add(pos.copy(), App.CELLSIZE/2));
            board.addBall(firstBall);
            startTime = System.currentTimeMillis();
        }
    }

    /**
     * @Description: 获得下次孵化球的剩余时间
     * @param null
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:03
     */
    public float getRemainingTime(){
        if(Board.isPause||Board.isGameOver()||Board.isNoTime()){
            startTime = System.currentTimeMillis() - pauseTime;
        }
        pauseTime = System.currentTimeMillis() - startTime;
        float time = interval - pauseTime / 1000.f;
        time = (float) Math.floor(time * 10.f)/10.f;
        return Math.max(time,0.f);
    }

    /**
     * @Description: 当球进错洞时,加入到队列中
     * @param ball
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:02
     */
    public void addBall(Ball ball) {
        ball.setPos(new Pos(startPos.x+(Ball.BALL_SIZE+ offset_x)*balls.size(),startPos.y));
        balls.add(ball);
    }

    public void clear(){
        startTime = System.currentTimeMillis();
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }

    public float getBackgroundLength(){
        return 5 * (Ball.BALL_SIZE + offset_x) - 5;
    }

    public int getSize() {
        return balls.size();
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public void setBalls(List<Ball> balls) {
        this.balls = balls;
    }

    public Pos getStartPos() {
        return startPos;
    }
}
