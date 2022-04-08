package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.game.bejeweled.kralik.core.Field;
import sk.tuke.gamestudio.entity.Score;

import java.io.*;
import java.util.*;

public class ScoreServiceFile implements ScoreService {

    private String getFileName(String game) {
        return System.getProperty("user.home") + "/" + game + ".score";
    }

    @Override
    public void addScore(Score score) throws ScoreException {
        List<Score> scores = getBestScores(score.getGame());
        scores.add(score);
        Collections.sort(scores);
//        Collections.sort(scores, (o1, o2) -> o2.getPoints() - o1.getPoints());

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getFileName(score.getGame())))) {
            oos.writeObject(scores);
//            oos.writeObject(new Date());
        } catch (Exception e) {
            throw new ScoreException("Error saving score", e);
        }
    }

    @Override
    public List<Score> getBestScores(String game) throws ScoreException {
        List<Score> scores = new ArrayList<>();
        File file = new File(getFileName(game));
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))
            ) {
                scores = (List<Score>) ois.readObject();
//                Date dateRecorded = (Date) ois.readObject();
            } catch (Exception e) {
                throw new ScoreException("Error loading score", e);
            }
        }
        return scores;
    }

    public static void main(String[] args) throws Exception {
        Score score = new Score(Field.GAME_NAME, "name", 100, new java.util.Date());
        ScoreService scoreService = new ScoreServiceFile();
        scoreService.addScore(score);
        System.out.println(scoreService.getBestScores("bejeweled"));
    }
}
