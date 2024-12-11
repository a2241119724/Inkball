package inkball.tile;

import inkball.App;

/**
 * @Description: Pos相关操作
 * @author Boning Zhang
 * @date 2024/10/18 15:53
 */
public class Pos{
    public float x;
    public float y;

    public Pos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @Description: 获取与pos之间的距离
     * @param pos
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:38
     */
    public float getDistance(Pos pos){
        return (float) Math.sqrt(Math.pow(pos.x-x,2)+Math.pow(pos.y-y,2));
    }

    /**
     * @Description: 对Pos进行归一化操作
     * @param null
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:38
     */
    public void normal(){
        float mu = (float) Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
        x = x / mu;
        y = y / mu;
    }

    public Pos copy(){
        return new Pos(x,y);
    }

    public static Pos add(Pos p1,float det){
        return new Pos(p1.x+det,p1.y+det);
    }

    public static Pos addPos(Pos p1,Pos p2){
        return new Pos(p1.x+p2.x,p1.y+p2.y);
    }

    public static float length(Pos p1){
        return App.sqrt(App.pow(p1.x,2)+App.pow(p1.y,2));
    }

    public static Pos mul(Pos p1,float det){
        return new Pos(p1.x*det,p1.y*det);
    }

    public static float dotMul(Pos p1,Pos p2){
        return p1.x*p2.x + p1.y*p2.y;
    }

    public static Pos sub(Pos p1,float det){
        return new Pos(p1.x-det,p1.y-det);
    }

    public static Pos subPos(Pos p1,Pos p2){
        return new Pos(p1.x-p2.x,p1.y-p2.y);
    }
}
