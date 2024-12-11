package inkball.config;

import java.util.List;
import java.util.Map;

/**
 * @Description: 读入config.json
 * @author Boning Zhang
 * @date 2024/10/18 15:53
 */
public class Config {
    private List<Level> levels;
    private Map<String,Integer> scoreIncreaseFromHoleCapture;
    private Map<String,Integer> scoreDecreaseFromWrongHole;

    public List<Level> getLevels() {
        return levels;
    }

    public Map<String, Integer> getScoreIncreaseFromHoleCapture() {
        return scoreIncreaseFromHoleCapture;
    }

    public Map<String, Integer> getScoreDecreaseFromWrongHole() {
        return scoreDecreaseFromWrongHole;
    }
}
