package inkball;

import inkball.config.Level;
import inkball.tile.Ball;
import inkball.tile.Hole;
import inkball.tile.Pos;
import inkball.view.BallQueueManager;
import inkball.view.Board;
import inkball.view.LineManager;
import processing.core.PApplet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;


public class SampleTest {
    @Test
    public void config() {
        // 读写Config与Level
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.board.parseConfig(app.loadJSONObject("config.json"));
        for (Level level : app.board.getConfig().getLevels()) {
            System.out.println(level.getScoreDecreaseFromWrongHoleModifier());
            System.out.println(level.getScoreIncreaseFromHoleCaptureModifier());
        }
        for(Map.Entry<String,Integer> entry : Board.getConfig().getScoreIncreaseFromHoleCapture().entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        for(Map.Entry<String,Integer> entry : app.board.getConfig().getScoreDecreaseFromWrongHole().entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        app.setup();
    }

    @Test
    public void ball() {
        // 对Ball的处理
        App app = new App();
        App.FPS = 500;
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);
        app.board.getLineManager().addNewLine();
        for (int i = 400; i < 500; i++) {
            app.board.getLineManager().addNodeForCurLine(i,i);
        }
        app.board.getLineManager().addNewLine();
        for (int i = 100; i < 300; i++) {
            app.board.getLineManager().addNodeForCurLine(i,i);
        }
        for (int i = 0; i < 5; i++) {
            app.board.addBall(new Ball(0,new Pos(100.f,100.f)));
        }
        app.delay(3000);
        for (int i = 0; i < App.BOARD_HEIGHT; i+=5) {
            for (int j = 0; j < App.BOARD_HEIGHT-2; j+=5) {
                app.board.tiles[i][j] = new Hole(4);
            }
        }
        app.delay(2000);
        Pos pos = new Pos(0.f, 0.f);
        System.out.println(pos.copy());
        Pos.add(pos,0.f);
        Pos.sub(pos,0.f);
    }

    @Test
    public void board() {
        // 对面板的处理
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);
        for (int i = 0; i < 100; i++) {
            app.board.showForSuccess();
        }
        app.board.clear();
        Board.addLevel();
        app.board.isMaxLevel();
        app.board.addBall(new Ball(0,new Pos(0.f,0.f)));
        app.board.checkGameOver();
        app.board.setNextLevel(true);
        app.board.getSpawners();
        app.board.setSuccess(true);
        Board.setIsNoTime(true);
    }

    @Test
    public void lineManager() {
        // 对LineManager的处理
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);
        LineManager lineManager = app.board.getLineManager();
        lineManager.addNewLine();
        lineManager.addNodeForCurLine(0.f,0.f);
        lineManager.checkLastLine();
        lineManager.getLastNodeForCurLine();
        lineManager.deleteLineForRight(10.f,10.f);
        lineManager.deleteLineForRight(0.f,0.f);
        lineManager.addNewLine();
        lineManager.removeLine(lineManager.getLines().get(0));
        lineManager.addNewLine();
        LineManager.curDrawLine = null;
        lineManager.addNodeForCurLine(0.f,0.f);
        LineManager.curDrawLine = new ArrayList();
        lineManager.getLastNodeForCurLine();
        LineManager.curDrawLine = new ArrayList();
        lineManager.removeLine(LineManager.curDrawLine);
    }

    @Test
    public void ballQueueManager() {
        // 对App和BallQueueManager的处理
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);
        BallQueueManager ballQueueManager = app.board.getBallQueueManager();
        ballQueueManager.addBall(new Ball(0,new Pos(0.f,0.f)));
        ballQueueManager.setInterval(0.01f);
        app.delay(1000);
        ballQueueManager.addBallCheck(app.board);
        ballQueueManager.getSize();

        Board.isGameOver = false;
        app.mouseButton = App.RIGHT;
        app.mousePressed(null);

        app.delay(1000);
        app.board.getTimeManager().setTime(0);
        Board.isGameOver = false;

        app.mouseButton = App.LEFT;
        app.mouseDragged(null);

        app.mouseButton = App.RIGHT;
        app.mouseDragged(null);

        app.keyCode=App.CONTROL;
        app.keyPressed(null);
        app.mouseButton = App.LEFT;
        app.mouseDragged(null);

        app.key = 'r';
        app.keyPressed(null);

        app.key = ' ';
        app.keyPressed(null);

        app.mousePressed(null);
        app.keyReleased();

        app.board.getTimeManager().getScoreForSuccess();
        app.board.getTimeManager().setTime(0);
        app.board.setSuccess(true);
        Board.isGameOver = true;
        app.draw();
        Board.isGameOver = false;
        app.draw();

        Board.isGameOver = true;
        app.reset();
        app.board.switchLevel(app.loadStrings(app.board.levelToFileName(Board.getCurLevel())));
        app.board.switchLevel(app.loadStrings(app.board.levelToFileName(Board.getCurLevel())));
        app.board.switchLevel(app.loadStrings(app.board.levelToFileName(Board.getCurLevel())));
        app.board.switchLevel(app.loadStrings(app.board.levelToFileName(Board.getCurLevel())));
    }
}

// gradle run						Run the program
// gradle test						Run the testcases

// Please ensure you leave comments in your testcases explaining what the testcase is testing.
// Your mark will be based off the average of branches and instructions code coverage.
// To run the testcases and generate the jacoco code coverage report: 
// gradle test jacocoTestReport
