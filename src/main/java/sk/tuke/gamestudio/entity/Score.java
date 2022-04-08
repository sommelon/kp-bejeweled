package sk.tuke.gamestudio.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.io.Serializable;
import java.util.Date;

@Entity
@NamedQuery( name = "Score.getBestScores",
        query = "SELECT s FROM Score s WHERE s.game=:game ORDER BY s.points DESC")
public class Score implements Serializable, Comparable<Score> {
    @Id
    @GeneratedValue
    private int ident;


    private String game;
    private String username;
    private int points;
    private Date playedOn;

    public Score(){}

    public Score(String game, String username, int points, Date playedOn) {
        this.game = game;
        this.username = username;
        this.points = points;
        this.playedOn = playedOn;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Date getPlayedOn() {
        return playedOn;
    }

    public void setPlayedOn(Date playedOn) {
        this.playedOn = playedOn;
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    @Override
    public String toString() {
        return "Score{" +
                "game='" + game + '\'' +
                ", username='" + username + '\'' +
                ", points=" + points +
                ", playedOn=" + playedOn +
                '}';
    }

    @Override
    public int compareTo(Score s) {
        return s.getPoints() - this.points;
    }
}
