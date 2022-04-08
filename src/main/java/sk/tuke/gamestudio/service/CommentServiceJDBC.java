package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentServiceJDBC implements CommentService{
    private static final String URL = "jdbc:postgresql://localhost/gamestudio";
    private static final String USER = "postgres";
    private static final String PASS = "admin";

    private static final String INSERT =
            "INSERT INTO comment (game, player, comment, commentedOn) VALUES (?, ?, ?, ?)";

    @Override
    public void addComment(Comment comment) throws CommentException {
        try (Connection c = DriverManager.getConnection(URL, USER, PASS)) {
            try(PreparedStatement ps = c.prepareStatement(INSERT)) {
                ps.setString(1, comment.getGame());
                ps.setString(2, comment.getPlayer());
                ps.setString(3, comment.getComment());
                ps.setTimestamp(4, new Timestamp(comment.getCommentedOn().getTime()));

                ps.execute();
            } catch (SQLException e) {
                throw new CommentException("Error saving comment", e);
            }
//
//            c.commit();
//            c.close();
        } catch (SQLException e) {
            throw new CommentException("Error connecting to database", e);
        }
    }

    private static final String SELECT =
            "SELECT game, player, comment, commentedOn FROM comment " +
                    "WHERE game = ? ORDER BY commentedOn DESC";

    @Override
    public List<Comment> getComments(String gameName) throws CommentException{
        List<Comment> comments = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(URL, USER, PASS)) {
            try(PreparedStatement ps = c.prepareStatement(SELECT)) {
                ps.setString(1, gameName);

                ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    Comment comment = new Comment(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getTimestamp(4)
                    );
                    comments.add(comment);
                }

            } catch (SQLException e) {
                throw new CommentException("Error getting comment", e);
            }

//            c.close();
        } catch (SQLException e) {
            throw new CommentException("Error connecting to database", e);
        }

        return comments;
    }
}
