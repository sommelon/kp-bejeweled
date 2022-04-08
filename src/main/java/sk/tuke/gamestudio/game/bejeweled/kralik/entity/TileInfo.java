package sk.tuke.gamestudio.game.bejeweled.kralik.entity;

import sk.tuke.gamestudio.game.bejeweled.kralik.core.Tile;
import sk.tuke.gamestudio.game.bejeweled.kralik.shapes.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TileInfo {
    @Id
    @GeneratedValue
    private int ident;

    private int tileShape;
    private int row;
    private int column;

    public TileInfo() {
    }

    public TileInfo(Tile tileShape, int row, int column) {
        if (tileShape instanceof Square){
            this.tileShape = 0;
        }else if(tileShape instanceof Triangle){
            this.tileShape = 1;
        }else if(tileShape instanceof Star){
            this.tileShape = 2;
        }else if(tileShape instanceof Circle){
            this.tileShape = 3;
        }else if(tileShape instanceof Hexagon){
            this.tileShape = 4;
        }else if(tileShape instanceof Diamond){
            this.tileShape = 5;
        }

        this.row = row;
        this.column = column;
    }

    public int getIdent() {
        return ident;
    }

    public int getTileShape() {
        return tileShape;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
