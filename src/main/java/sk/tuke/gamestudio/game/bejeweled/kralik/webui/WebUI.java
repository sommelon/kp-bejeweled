package sk.tuke.gamestudio.game.bejeweled.kralik.webui;

import sk.tuke.gamestudio.game.bejeweled.kralik.core.Coordinates;
import sk.tuke.gamestudio.game.bejeweled.kralik.core.Field;
import sk.tuke.gamestudio.game.bejeweled.kralik.core.GameState;
import sk.tuke.gamestudio.game.bejeweled.kralik.core.Tile;

public class WebUI {
    private Field field;
    private final int TIME_LIMIT = 150;
    private int timeRemaining = 0;
    private boolean hint = false;

    public void processCommand(String command, String srcRowString, String srcColString, String destRowString, String destColString) {
        if (field == null) {
            createField();
        }

        hint = false;

        if (command != null) {
            switch (command) {
                case "new":
                    createField();
                    break;
                case "hint":
                    hint = true;
                    timeRemaining -= 5;
                    break;
            }
        } else {
            if (field.getState() == GameState.PLAYING) {
                try {
                    int srcRow = Integer.parseInt(srcRowString);
                    int srcCol = Integer.parseInt(srcColString);
                    int destRow = Integer.parseInt(destRowString);
                    int destCol = Integer.parseInt(destColString);

                    field.swapTilesIfPossible(srcRow, srcCol, destRow, destCol);
                } catch (Exception e) {
                    e.printStackTrace();
//                    return;
                }

                field.removeCombos();
                field.clearCombosList();

                while (field.hasNullTiles()) {
                    field.moveTilesDown();
                    field.fillTheField();
                    if (field.areThereNewCombos()) {
                        field.removeCombos();
                        field.clearCombosList();
                    }
                }
                field.areTherePossibleMoves();
            }
        }

        timeRemaining = TIME_LIMIT - field.getPlayingTime();
        if (timeRemaining <= 0) {
//            timeRemaining = 0;
            field.timeOut();
        }

        if (field.getScore() >= 1000 && field.getShapeCount() == 3){
            field.setShapeCount(4);
        }else if(field.getScore() >= 1250 && field.getShapeCount() == 4){
            field.setShapeCount(5);
        }else if(field.getScore() >= 1500 && field.getShapeCount() == 5){
            field.setShapeCount(6);
        }
    }

    public String renderAsHtml() {
        StringBuilder sb = new StringBuilder();


        sb.append("<div class='field'>\n");
        sb.append("<table>\n");

        for (int row = 0; row <= 9; row++) {
            sb.append("<tr>\n");
            for (int col = 0; col <= 9; col++) {
                Tile tile = field.getTile(row, col);

                if (field.getPossibleMovesSrcCoordinates().size() != 0 && field.getState() == GameState.PLAYING && field.getScore() >= 1500){
                    int srcRow = field.getPossibleMovesSrcCoordinates().get(0).getRow();
                    int srcCol = field.getPossibleMovesSrcCoordinates().get(0).getColumn();
                    int destRow = field.getPossibleMovesDestCoordinates().get(0).getRow();
                    int destCol = field.getPossibleMovesDestCoordinates().get(0).getColumn();

                    if (hint && (srcRow == row && srcCol == col) || (destRow == row && destCol == col)) {
                        sb.append("<td class='hint ");
                    }else {
                        sb.append("<td class='");
                    }
                }else{
                    sb.append("<td class='");
                }
                sb.append("row" + row + " col" + col + "' onclick='select(" + row + "," + col + ")'>\n");
                sb.append("<img src='images/bejeweled/kralik/" + tile.getClass().getSimpleName() + ".png' class='shape hoverAnimation'/>\n");
                sb.append("</td>\n");
            }
        }

        sb.append("</table>");
        sb.append("</div>");

        sb.append("<div class='data' hidden>");
        sb.append("<div class='lastGeneratedTiles'>");

        for (Coordinates coordinates : field.getLastGeneratedTiles()){
            int row = coordinates.getRow();
            int col = coordinates.getColumn();

            sb.append("<div data-row='"+row+"' data-col='"+col+"'></div>");
        }

        sb.append("</div>");
        sb.append("</div>");

        field.clearWebUIHelpArrays();

        return sb.toString();
    }

    private void createField() {
        field = new Field(10, 10, 3);
        while (!field.areTherePossibleMoves()) {
            System.out.println("No possible moves. Generating new field.");
            field = new Field(10, 10, 3);
        }
    }

    public Field getField(){
        return field;
    }

    public int getTimeRemaining(){
        return timeRemaining;
    }
}
