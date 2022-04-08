import org.junit.Before;
import org.junit.Test;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.CommentServiceJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.*;
import static sk.tuke.gamestudio.game.bejeweled.kralik.core.Field.GAME_NAME;

public class CommentServiceTest {
    protected CommentService commentService = new CommentServiceJDBC();

    private static final String DELETE = "DELETE FROM comment";

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
        assertEquals(0, commentService.getComments(GAME_NAME).size());
    }

    @Test
    public void testAddComment() throws Exception {
        Comment c1 = new Comment(GAME_NAME, "jozko", "lorem ipsum dolor sit ames", new Date());
        Comment c2 = new Comment(GAME_NAME, "jozko", "lorem ipsum dolor sit", new Date());
        commentService.addComment(c1);
        assertEquals(1, commentService.getComments(GAME_NAME).size());
        commentService.addComment(c2);
        assertEquals(2, commentService.getComments(GAME_NAME).size());
    }

    @Test
    public void testGetComments() throws Exception {
        Comment c0 = new Comment(GAME_NAME, "janko", "ahoj ako sa mas?", new Date());
        Comment c1 = new Comment(GAME_NAME, "marienka", "ja sa mam dobre a ty?", new Date());

        commentService.addComment(c0);
        commentService.addComment(c1);

        List<Comment> comments = commentService.getComments(GAME_NAME);
        assertEquals(c1.getComment(), comments.get(1).getComment());
        assertEquals(c1.getPlayer(), comments.get(1).getPlayer());
    }

}