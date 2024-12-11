package inkball.view;

import inkball.tile.Pos;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 画线相关操作
 * @author Boning Zhang
 * @date 2024/10/18 15:53
 */
public class LineManager {
    private List<List<Pos>> lines;
    public static List<Pos> curDrawLine;

    public LineManager() {
        lines = new ArrayList();
    }

    public void addNewLine(){
        curDrawLine = new ArrayList();
        lines.add(curDrawLine);
    }

    /**
     * @Description: 对正在画线添加节点
     * @param mouseX
     * @param mouseY
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:19
     */
    public void addNodeForCurLine(float mouseX, float mouseY){
        if(lines.size()==0) return;
        if(curDrawLine==null) return;
        curDrawLine.add(new Pos(mouseX, mouseY));
    }

    /**
     * @Description: 获取正在画线的上一个节点
     * @param null 
     * @return
     * @author Boning Zhang
     * @date 2024/10/18 16:32
     */
    public Pos getLastNodeForCurLine(){
        if(curDrawLine==null) return null;
        if(curDrawLine.size()==0) return null;
        return curDrawLine.get(curDrawLine.size()-1);
    }

    /**
     * @Description: 针对右键或ctrl+鼠标左键删除线
     * @param x
     * @param y
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:33
     */
    public void deleteLineForRight(float x,float y){
        for (List<Pos> line : lines) {
            for (Pos pos : line) {
                if(pos.getDistance(new Pos(x,y))<10.f){
                    if(curDrawLine==line) curDrawLine=null;
                    lines.remove(line);
                    return;
                }
            }
        }
    }

    /**
     * @Description: 若上一条画的线没有节点则删掉该线
     * @param null
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:34
     */
    public void checkLastLine(){
        if(lines.size()==0) return;
        List<Pos> lastLine = lines.get(lines.size() - 1);
        if(lastLine.size()==0){
            if(curDrawLine==lastLine) curDrawLine=null;
            lines.remove(lastLine);
        }
    }

    /**
     * @Description: Ball and Line碰撞后删掉线
     * @param null
     * @return null
     * @author Boning Zhang
     * @date 2024/10/18 16:35
     */
    public void removeLine(List<Pos> line){
        if(curDrawLine==line) curDrawLine=null;
        lines.remove(line);
    }

    public void clear(){
        lines.clear();
    }

    public List<List<Pos>> getLines() {
        return lines;
    }
}
