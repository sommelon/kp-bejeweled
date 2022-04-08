package sk.tuke.gamestudio.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@NamedQueries({
        @NamedQuery(name = "Rating.getAverageRating",
                query = "SELECT avg(r.rating) FROM Rating r WHERE r.game=:game"),
        @NamedQuery( name = "Rating.getRating",
                query = "SELECT r.rating FROM Rating r WHERE r.game=:game AND r.player=:player"),
        @NamedQuery( name = "Rating.deleteRating",
                query = "DELETE FROM Rating r WHERE r.game=:game and r.player=:player")
})
public class Rating {
    @Id
    @GeneratedValue
    private int ident;

    private String player;
    private String game;
    private int rating;
    private Date ratedOn;

    public Rating() {
    }

    public Rating(String game, String player, int rating, Date ratedOn) {
        this.game = game;
        this.player = player;
        this.rating = rating;
        this.ratedOn = ratedOn;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getRatedOn() {
        return ratedOn;
    }

    public void setRatedOn(Date ratedOn) {
        this.ratedOn = ratedOn;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Rating{");
        sb.append("player='").append(player).append('\'');
        sb.append(", game='").append(game).append('\'');
        sb.append(", rating=").append(rating);
        sb.append(", ratedOn=").append(ratedOn);
        sb.append('}');
        return sb.toString();
    }
}
