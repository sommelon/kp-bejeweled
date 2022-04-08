package sk.tuke.gamestudio.game.bejeweled.kralik.entity;

import sk.tuke.gamestudio.game.bejeweled.kralik.core.Tile;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GamePlay {
    @Id
    @GeneratedValue
    private int ident;

    private int rowCount;
    private int columnCount;

    private String username;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderColumn
    @JoinColumn(name = "gameplay_ident")
//    private List<TileInfo> tileInfos;
    private Tile[][] tileInfos;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderColumn
    @JoinColumn(name = "gameplay_ident")
    private List<Command> commands;

    public GamePlay() {
    }

    public GamePlay(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
    }

//    public void setTileInfos(List<TileInfo> tileInfos) {
//        this.tileInfos = tileInfos;
//    }
    public void setTileInfos(Tile[][] tileInfos) {
        this.tileInfos = tileInfos;
    }

    public int getIdent() {
        return ident;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public String getUsername() {
        return username;
    }

//    public List<TileInfo> getTileInfos() {
//        return tileInfos;
//    }
    public Tile[][] getTileInfos() {
        return tileInfos;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void addCommand(Command command) {
        if (commands == null) {
            commands = new ArrayList<>();
        }
        commands.add(command);
    }

    public void setUsername(String username) {
        this.username = username;
    }
}


