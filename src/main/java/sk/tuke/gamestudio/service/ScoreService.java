package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Score;

import java.util.List;

public interface ScoreService {
    public void addScore(Score score) throws ScoreException;

    public List<Score> getBestScores(String gameName) throws ScoreException;
}
