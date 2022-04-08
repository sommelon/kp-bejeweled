package sk.tuke.gamestudio.game.bejeweled.kralik.entity;

import sk.tuke.gamestudio.game.bejeweled.kralik.core.Field;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Command {
    @Id
    @GeneratedValue
    private int ident;

    private int row;

    private int col;

    @Enumerated
    private CommandType commandType;

    public Command() {
    }

    public Command(int row, int col, CommandType commandType) {
        this.row = row;
        this.col = col;
        this.commandType = commandType;
    }

    public int getIdent() {
        return ident;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void execute(Field field) {
        if (commandType == CommandType.REMOVE)
            field.removeCombos();
        else if(commandType == CommandType.DROP)
            field.moveTilesDown();
        else if(commandType == CommandType.FILL)
            field.fillTheField();
    }
}
