package inkball.view;

/**
 * @Description: time 相关操作
 * @author Boning Zhang
 * @date 2024/10/18 15:53
 */
public class TimeManager {
    private long startTime = System.currentTimeMillis();
    private long time;

    /**
     * @Description: 更新时间信息
     * @param null 
     * @return null 
     * @author Boning Zhang
     * @date 2024/10/18 16:36
     */
    public void update(){
        if(Board.isPause){
            return;
        }
        time-=(System.currentTimeMillis()-startTime);
        startTime = System.currentTimeMillis();
    }

    /**
     * @Description: 获取时间信息
     * @param null
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:37
     */
    public int getTime() {
        if(time<=0){
            return 0;
        }
        return (int)time/1000;
    }

    /**
     * @Description: 获胜后对时间与分数的处理
     * @param null
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:37
     */
    public int getScoreForSuccess(){
        if(time!=0){
            time-=670;
            return 10;
        }
        return 0;
    }

    public void setTime(int time) {
        this.time = time*1000;
    }
}
