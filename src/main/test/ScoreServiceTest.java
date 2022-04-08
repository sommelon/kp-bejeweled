import org.junit.Before;
import org.junit.Test;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.ScoreService;
import sk.tuke.gamestudio.service.ScoreServiceJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.*;
import static sk.tuke.gamestudio.game.bejeweled.kralik.core.Field.GAME_NAME;

public class ScoreServiceTest{
    protected ScoreService scoreService = new ScoreServiceJDBC();

    private static final String DELETE = "DELETE FROM score";

    private static final String URL = "jdbc:postgresql://localhost/gamestudio";
    private static final String USER = "postgres";
    private static final String PASS = "admin";

    @Before
    public void setUp() throws Exception {
        Connection c = DriverManager.getConnection(URL, USER, PASS);
        Statement s = c.createStatement();
        s.execute(DELETE);
    }

    @Test
    public void testDbInit() throws Exception {
        assertEquals(0, scoreService.getBestScores(GAME_NAME).size());
    }

    @Test
    public void testAddScore() throws Exception {
        Score s1 = new Score(GAME_NAME, "jozko", 15, new Date());
        Score s2 = new Score(GAME_NAME, "jozko", 12, new Date());
        scoreService.addScore(s1);
        assertEquals(1, scoreService.getBestScores(GAME_NAME).size());
        scoreService.addScore(s2);
        assertEquals(2, scoreService.getBestScores(GAME_NAME).size());
    }

    @Test
    public void testGestBestScores() throws Exception {
        Score s0 = new Score(GAME_NAME, "janko", 150, new Date());
        Score s1 = new Score(GAME_NAME, "marienka", 300, new Date());

        scoreService.addScore(s0);
        scoreService.addScore(s1);

        List<Score> scores = scoreService.getBestScores(GAME_NAME);
        assertEquals(s1.getPoints(), scores.get(0).getPoints());
        assertEquals(s1.getUsername(), scores.get(0).getUsername());
    }
}
