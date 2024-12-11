package inkball;

import inkball.tile.*;
import inkball.view.Board;
import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.*;

public class App extends PApplet {
    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;
    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 576; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;
    public static int FPS = 30;
    public String configPath;
    public static Random random = new Random();

    public static final int LINE_WIDTH = 10;

    // image count
    private static final int ballImageCount = 5;
    private static final int spawnerImageCount = 1;
    private static final int holeImageCount = 5;
    private static final int spaceImageCount = 1;
    private static final int wallImageCount = 5;
    private static int totalScore = 0;
    private static int lastScore = 0;
    private boolean ctrl = false;
    public Board board = new Board();

	// Feel free to add any additional methods or attributes you want. Please put classes in different files.
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);
        Tile.addNameToStartIndex(TileType.BALL);
        for (int i = 0; i < ballImageCount; i++) {
            Tile.addImage(loadImage(this.getClass().getResource("/inkball/ball"+i+".png").getPath().toLowerCase(Locale.ROOT).replace("%20", " ")));
        }
        Tile.addNameToStartIndex(TileType.SPAWNER);
        for (int i = 0; i < spawnerImageCount; i++) {
            Tile.addImage(loadImage(this.getClass().getResource("/inkball/entrypoint"+i+".png").getPath().toLowerCase(Locale.ROOT).replace("%20", " ")));
        }
        Tile.addNameToStartIndex(TileType.HOLE);
        for (int i = 0; i < holeImageCount; i++) {
            Tile.addImage(loadImage(this.getClass().getResource("/inkball/hole"+i+".png").getPath().toLowerCase(Locale.ROOT).replace("%20", " ")));
        }
        Tile.addNameToStartIndex(TileType.SPACE);
        for (int i = 0; i < spaceImageCount; i++) {
            Tile.addImage(loadImage(this.getClass().getResource("/inkball/tile" +i+".png").getPath().toLowerCase(Locale.ROOT).replace("%20", " ")));
        }
        Tile.addNameToStartIndex(TileType.WALL);
        for (int i = 0; i < wallImageCount; i++) {
            Tile.addImage(loadImage(this.getClass().getResource("/inkball/wall"+i+".png").getPath().toLowerCase(Locale.ROOT).replace("%20", " ")));
        }
        PImage pImage = loadImage(this.getClass().getResource("/inkball/inkball_spritesheet.png").getPath()
                .toLowerCase(Locale.ROOT).replace("%20", " "));
        PImage[] pImages = new PImage[5];
        pImages[0] = pImage.get(99,166,32,32);
        pImages[1] = pImage.get(99,199,32,32);
        pImages[2] = pImage.get(99,232,32,32);
        pImages[3] = pImage.get(99,265,32,32);
        pImages[4] = pImage.get(99,298,32,32);
        Wall.setHitImages(pImages);
        board.parseConfig(loadJSONObject(configPath));
        board.switchLevel(loadStrings(board.levelToFileName(Board.getCurLevel())));
        Ball.setColorToIndex(new HashMap(){{
            put("grey",0);
            put("orange",1);
            put("blue",2);
            put("green",3);
            put("yellow",4);
        }});
        board.getBallQueueManager().initBalls();
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        if (key == 'r' || key == 'R') {
            reset();
        } else if (key == ' ') {
            Board.isPause = !Board.isPause;
        }
        if(keyCode==CONTROL){
            ctrl = true;
            keyCode = -1;
        }
    }

    /**
     * @Description: reset game
     * @param null
     * @return null
     * @exception
     * @author Boning Zhang
     * @date 2024/10/18 15:58
     */
    public void reset(){
        totalScore = lastScore;
        if(Board.isGameOver()){
            board.setNextLevel(true);
            totalScore = 0;
        }
        Board.setIsNoTime(false);
        board.clear();
        setup();
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        ctrl = false;
        board.getLineManager().checkLastLine();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // create a new player-drawn line object
        if(board.isGameOver()) return;
        board.getLineManager().addNewLine();
        if (mouseButton==RIGHT||(mouseButton==LEFT&&ctrl)) {
            board.getLineManager().deleteLineForRight(mouseX,mouseY);
        }
    }
	
	@Override
    public void mouseDragged(MouseEvent e) {
        if(board.isGameOver()) return;
        // add line segments to player-drawn line object if left mouse button is held
        if (mouseButton == LEFT) {
            if(ctrl){
                board.getLineManager().deleteLineForRight(mouseX,mouseY);
            }else{
                stroke(0);
                strokeWeight(LINE_WIDTH);
                Pos lastNodePos = board.getLineManager().getLastNodeForCurLine();
                if(lastNodePos!=null){
                    line(lastNodePos.x,lastNodePos.y,mouseX,mouseY);
                }
                board.getLineManager().addNodeForCurLine(mouseX,mouseY);
            }
        }
        // remove player-drawn line object if right mouse button is held
        // and mouse position collides with the line
        if (mouseButton == RIGHT) {
            board.getLineManager().deleteLineForRight(mouseX,mouseY);
        }
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        background(255);
        board.getTimeManager().update();
        // render balls queue
        rect(board.getBallQueueManager().getStartPos().x,board.getBallQueueManager().getStartPos().y,
                board.getBallQueueManager().getBackgroundLength(),board.getBallQueueManager().getStartPos().y+3.f);
        int index = 0;
        for (Ball ball : board.getBallQueueManager().getBalls()) {
            if(index++==5) break;
            image(ball.getImage(ball.name),ball.getPos().x,ball.getPos().y,ball.getCurSize(),ball.getCurSize());
        }
        stroke(255);
        rect(board.getBallQueueManager().getBackgroundLength()+20,board.getBallQueueManager().getStartPos().y,
                10,board.getBallQueueManager().getStartPos().y+3.f);
        if(board.isNextLevel()){
            board.clear();
            board.addLevel();
            board.setNextLevel(false);
            setup();
        }
        if(board.getTimeManager().getTime() <= 0&&!Board.isGameOver()){
            Board.setIsNoTime(true);
            textSize(20);
            text("=== TIME'S UP ===", 250,TOPBAR-20);
            board.checkGameOver();
        }
        if(Board.isPause){
            textSize(20);
            text("*** PAUSED ***", 250,TOPBAR-20);
        }
        //----------------------------------
        //display Board for current level:
        //----------------------------------
        //----------------------------------
        //display score
        //----------------------------------
        fill(0);
        textSize(20);
        text("Timer: "+ board.getTimeManager().getTime(), 450, TOPBAR-10);
        text("Score: "+ totalScore, 450, TOPBAR/2-10);
        stroke(0);
        strokeWeight(LINE_WIDTH);
        text(""+board.getBallQueueManager().getRemainingTime(), 165,TOPBAR/2+8);
		//----------------------------------
        //----------------------------------
		//display game end message
        int width = BOARD_WIDTH;
        int height = BOARD_HEIGHT-TOPBAR/CELLSIZE;
        for (int i = height-1; i >= 0; i--) {
            for (int j = width-1; j >= 0; j--) {
                Tile tile = board.getBoard(i, j);
                image(tile.getImage(tile.name),board.getPosX(j),board.getPosY(i));
            }
        }
        // render balls
        for (Ball ball : board.getBalls()) {
            ball.move();
            ball.collisionAndSuccessCheck(board);
            totalScore += ball.getScore();
            Pos pos = ball.getPos();
            image(ball.getImage(ball.name),pos.x-Ball.BALL_SIZE/2,pos.y-Ball.BALL_SIZE/2,ball.getCurSize(),ball.getCurSize());
        }
        // remove if enter hole
        board.getBalls().removeIf(ball -> {
            boolean enterHole = ball.isEnterHole();
            ball.setEnterHole(false);
            return enterHole;
        });
        board.getBallQueueManager().addBallCheck(board);
        // render lines
        for (List<Pos> line : board.getLineManager().getLines()) {
            int size = line.size();
            for (int i = 1; i < size; i++) {
                line(line.get(i-1).x,line.get(i-1).y,line.get(i).x,line.get(i).y);
            }
        }
        board.getBallQueueManager().reSort();
        if(board.isSuccess()){
            lastScore = totalScore;
            if(board.getTimeManager().getTime()<=0){
                if(Board.isGameOver()){
                    textSize(20);
                    text("*** ENDED ***", 250,TOPBAR-20);
                    return;
                }else{
                    Board.setIsNoTime(false);
                    board.setNextLevel(true);
                }
            }
            totalScore += board.getTimeManager().getScoreForSuccess();
            board.showForSuccess();
        }
    }

    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }
}
