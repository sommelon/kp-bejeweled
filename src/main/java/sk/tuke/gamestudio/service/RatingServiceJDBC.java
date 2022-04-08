package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import java.sql.*;

public class RatingServiceJDBC implements RatingService {
    private static final String URL = "jdbc:postgresql://localhost/gamestudio";
    private static final String USER = "postgres";
    private static final String PASS = "admin";

    private static final String UPDATE =
            "UPDATE rating SET rating = ?, ratedOn = ? WHERE player = ? AND game = ?";
    private static final String INSERT =
            "INSERT INTO rating (game, player, rating, ratedOn) VALUES (?, ?, ?, ?)";

    @Override
    public void setRating(Rating rating) throws RatingException {
        try (Connection c = DriverManager.getConnection(URL, USER, PASS)) {
            if (getRating(rating.getGame(), rating.getPlayer()) != 0) {
                try (PreparedStatement ps = c.prepareStatement(UPDATE)) {
                    ps.setInt(1, rating.getRating());
                    ps.setTimestamp(2, new Timestamp(rating.getRatedOn().getTime()));
                    ps.setString(3, rating.getPlayer());
                    ps.setString(4, rating.getGame());

                    ps.execute();
                } catch (SQLException e) {
                    throw new RatingException("Error updating rating", e);
                }
            } else {
                try (PreparedStatement ps = c.prepareStatement(INSERT)) {
                    ps.setString(1, rating.getGame());
                    ps.setString(2, rating.getPlayer());
                    ps.setInt(3, rating.getRating());
                    ps.setTimestamp(4, new Timestamp(rating.getRatedOn().getTime()));

                    ps.execute();
                } catch (SQLException e) {
                    throw new RatingException("Error saving rating", e);
                }
            }

//            c.commit();
//            c.close();
        } catch (SQLException e) {
            throw new RatingException("Error connecting to database", e);
        }
    }

    private static final String SELECT_AVERAGE =
            "SELECT avg(rating) FROM rating WHERE game = ?";

    @Override
    public int getAverageRating(String game) throws RatingException {
        int average = 0;
        try (Connection c = DriverManager.getConnection(URL, USER, PASS)) {
            try(PreparedStatement ps = c.prepareStatement(SELECT_AVERAGE)) {
                ps.setString(1, game);

                ResultSet rs = ps.executeQuery();
                if (rs.next())
                    average = (int) Math.round(rs.getDouble(1));
            } catch (SQLException e) {
                throw new RatingException("Error getting average rating", e);
            }

//            c.close();
        } catch (SQLException e) {
            throw new RatingException("Error connecting to database", e);
        }

        return average;
    }

    private static final String SELECT_RATING =
            "SELECT rating FROM rating WHERE game = ? AND player = ?";

    @Override
    public int getRating(String gameName, String player) throws RatingException {
        int rating = 0;

        try (Connection c = DriverManager.getConnection(URL, USER, PASS)) {
            try(PreparedStatement ps = c.prepareStatement(SELECT_RATING)) {
                ps.setString(1, gameName);
                ps.setString(2, player);

                ResultSet rs = ps.executeQuery();
                if(rs.next())
                    rating = rs.getInt(1);
            } catch (SQLException e) {
                throw new RatingException("Error getting rating", e);
            }

//            c.close();
        } catch (SQLException e) {
            throw new RatingException("Error connecting to database", e);
        }

        return rating;
    }
}
