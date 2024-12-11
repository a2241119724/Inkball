package inkball.config;

import java.util.List;

/**
 * @Description: 读入config.json
 * @author Boning Zhang
 * @date 2024/10/18 15:53
 */
public class Level {
    private String layout;
    private Integer time;
    private Integer spawnInterval;
    private Float scoreIncreaseFromHoleCaptureModifier;
    private Float scoreDecreaseFromWrongHoleModifier;
    private List<String> balls;

    public String getLayout() {
        return layout;
    }

    public Integer getSpawnInterval() {
        return spawnInterval;
    }

    public Float getScoreIncreaseFromHoleCaptureModifier() {
        return scoreIncreaseFromHoleCaptureModifier;
    }

    public Float getScoreDecreaseFromWrongHoleModifier() {
        return scoreDecreaseFromWrongHoleModifier;
    }

    public List<String> getBalls() {
        return balls;
    }

    public Integer getTime() {
        return time;
    }
}
