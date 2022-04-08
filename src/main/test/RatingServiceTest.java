import org.junit.Before;
import org.junit.Test;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.service.RatingService;
import sk.tuke.gamestudio.service.RatingServiceJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static sk.tuke.gamestudio.game.bejeweled.kralik.core.Field.GAME_NAME;

public class RatingServiceTest {
    protected RatingService ratingService = new RatingServiceJDBC();

    private static final String DELETE = "DELETE FROM rating";

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
        assertEquals(0, ratingService.getAverageRating(GAME_NAME));
    }

    @Test
    public void testSetRating() throws Exception {
        Rating r0 = new Rating(GAME_NAME, "jozko", 5, new Date());
        Rating r1 = new Rating(GAME_NAME, "jozko", 1, new Date());
        ratingService.setRating(r0);
        assertEquals(5, ratingService.getRating(GAME_NAME, "jozko"));
        ratingService.setRating(r1);
        assertEquals(1, ratingService.getRating(GAME_NAME, "jozko"));
    }

    @Test
    public void testGetAverageRating() throws Exception {
        Rating r0 = new Rating(GAME_NAME, "janko", 5, new Date());
        Rating r1 = new Rating(GAME_NAME, "marienka", 3, new Date());

        ratingService.setRating(r0);
        ratingService.setRating(r1);

        int averageRating = ratingService.getAverageRating(GAME_NAME);
        assertEquals((r0.getRating()+r1.getRating())/2, averageRating);
    }

    @Test
    public void testGetRating() throws Exception {
        Rating r0 = new Rating(GAME_NAME, "janko", 5, new Date());
        Rating r1 = new Rating(GAME_NAME, "marienka", 3, new Date());

        ratingService.setRating(r0);
        ratingService.setRating(r1);

        int rating = ratingService.getRating(GAME_NAME, "marienka");
        assertEquals(rating, r1.getRating());
        assertEquals("marienka", r1.getPlayer());
    }

}