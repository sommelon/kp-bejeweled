package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Score;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreServiceJDBC implements ScoreService {
    private static final String URL = "jdbc:postgresql://localhost/gamestudio";
    private static final String USER = "postgres";
    private static final String PASS = "admin";

    private static final String INSERT =
            "INSERT INTO score (game, player, points, playedOn) VALUES (?, ?, ?, ?)";

    @Override
    public void addScore(Score score) throws ScoreException {
        try (Connection c = DriverManager.getConnection(URL, USER, PASS)) {
            try(PreparedStatement ps = c.prepareStatement(INSERT)) {
                ps.setString(1, score.getGame());
                ps.setString(2, score.getUsername());
                ps.setInt(3, score.getPoints());
                ps.setTimestamp(4, new Timestamp(score.getPlayedOn().getTime()));

                ps.execute();
            } catch (SQLException e) {
                throw new ScoreException("Error saving score", e);
            }

//            c.commit();
//            c.close();
        } catch (SQLException e) {
            throw new ScoreException("Error connecting to database", e);
        }
    }

    private static final String SELECT =
            "SELECT game, player, points, playedOn FROM score " +
                    "WHERE game = ? ORDER BY points DESC";

    @Override
    public List<Score> getBestScores(String gameName) throws ScoreException {
        List<Score> scores = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(URL, USER, PASS)) {
            try(PreparedStatement ps = c.prepareStatement(SELECT)) {
                ps.setString(1, gameName);

                ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    Score s = new Score(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getInt(3),
                            rs.getTimestamp(4)
                    );
                    scores.add(s);
                }
            } catch (SQLException e) {
                throw new ScoreException("Error getting score", e);
            }

//            c.close();
        } catch (SQLException e) {
            throw new ScoreException("Error connecting to database", e);
        }

        return scores;
    }
}
