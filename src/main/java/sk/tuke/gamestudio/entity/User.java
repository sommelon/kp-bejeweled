package sk.tuke.gamestudio.entity;

import javax.persistence.*;

@Entity
@Table(name = "player")
@NamedQueries({
        @NamedQuery(name = "User.login",
                query = "SELECT u FROM User u WHERE u.username=:username AND u.passwd=:passwd"),
        @NamedQuery(name = "User.register",
                query = "SELECT u FROM User u WHERE u.username=:username"),
        @NamedQuery(name = "User.getUser",
                query = "SELECT u FROM User u WHERE u.username=:username"),
        @NamedQuery(name = "User.getUsers",
                query = "SELECT u FROM User u"),
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String username;

    private String passwd;

    @Transient
    private String verifiedPasswd;

    public User() {
    }

    public User(String username, String passwd) {
        this.username = username;
        this.passwd = passwd;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", passwd='" + passwd + '\'' +
                '}';
    }
}
