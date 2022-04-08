package sk.tuke.gamestudio.game.bejeweled.kralik.webui;

import sk.tuke.gamestudio.game.bejeweled.kralik.core.Coordinates;
import sk.tuke.gamestudio.game.bejeweled.kralik.core.Field;
import sk.tuke.gamestudio.game.bejeweled.kralik.core.GameState;
import sk.tuke.gamestudio.game.bejeweled.kralik.core.Tile;

import java.util.ArrayList;
import java.util.List;

public class WebUIExperimental {
    private Field field;
    private List<Coordinates> verticalCombos;
    private List<Coordinates> horizontalCombos;
    private boolean swapIsPossible = false;
    private boolean clearHelpArrays = false;

    public void processCommand(String command, String srcRowString, String srcColString, String destRowString, String destColString) {
		//TODO: implement what should happen when a command comes (open, mark, newgame, ...)
        if (field == null) {
            createField();
        }
        verticalCombos = new ArrayList<>(field.getVerticalCombos());
        horizontalCombos = new ArrayList<>(field.getHorizontalCombos());

        int srcRow;
        int srcCol;
        int destRow;
        int destCol;

        if (command != null) {
            switch (command) {
                case "new":
                    createField();
                    break;
                case "check":
                    try{
                        srcRow = Integer.parseInt(srcRowString);
                        srcCol = Integer.parseInt(srcColString);
                        destRow = Integer.parseInt(destRowString);
                        destCol = Integer.parseInt(destColString);

                        swapIsPossible = field.isTheSwapPossible(srcRow, srcCol, destRow, destCol, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                case "remove":
                    verticalCombos = new ArrayList<>(field.getVerticalCombos());
                    horizontalCombos = new ArrayList<>(field.getHorizontalCombos());
                case "moveDown":
                    field.removeCombos();
                    field.clearCombosList();
                    field.moveTilesDown();
                case "fill":
                    field.fillTheField();
                    break;
                case "auto":
//                    new Bot(field).openTile();
                    break;
            }
        } else {
            if (field.getState() == GameState.PLAYING) {
                try {
                    srcRow = Integer.parseInt(srcRowString);
                    srcCol = Integer.parseInt(srcColString);
                    destRow = Integer.parseInt(destRowString);
                    destCol = Integer.parseInt(destColString);

                    field.swapTilesIfPossible(srcRow, srcCol, destRow, destCol);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                while (field.hasNullTiles()){
                    field.moveTilesDown();
                    field.fillTheField();
                    if (field.areThereNewCombos()){
                        field.removeCombos();
                        verticalCombos = new ArrayList<>(field.getVerticalCombos());
                        horizontalCombos = new ArrayList<>(field.getHorizontalCombos());
                        field.clearCombosList();
                    }
                }
                field.areTherePossibleMoves();
            }
        }
    }

    public String renderAsHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='field'>\n");

//        //TODO: append body of the table according to the field content
        for (int row = 0; row <= 9; row++) {
            sb.append("<tr>\n");
            for (int col = 0; col <= 9; col++) {
                Tile tile = field.getTile(row, col);

                sb.append("<td class='row"+row+" col"+col+"' onclick='select("+row+","+col+")'>\n");
                sb.append("<img src='images/bejeweled/kralik/"+tile.getClass().getSimpleName()+".png' class='shape'/>\n");
                sb.append("</td>\n");
            }
        }

        sb.append("</table>");

        sb.append("<div class='data' hidden>");
        sb.append("<div class='canSwap' data-swap-is-possible="+swapIsPossible+"></div>");

        sb.append("<div class='lastGeneratedTiles'>");
        for (Coordinates coordinates : field.getLastGeneratedTiles()){
            int row = coordinates.getRow();
            int col = coordinates.getColumn();

            sb.append("<div data-row='"+row+"' data-col='"+col+"'></div>");
        }
        sb.append("</div>");

        sb.append("<div class='combosToRemove'>");
        for (Coordinates coordinates : horizontalCombos){
            int row = coordinates.getRow();
            int col = coordinates.getColumn();

            sb.append("<div data-row='"+row+"' data-col='"+col+"'></div>");
        }

        for (Coordinates coordinates : verticalCombos){
            int row = coordinates.getRow();
            int col = coordinates.getColumn();

            sb.append("<div data-row='"+row+"' data-col='"+col+"'></div>");
        }
        sb.append("</div>");
        sb.append("</div>");

        if (clearHelpArrays)
            field.clearWebUIHelpArrays();
        swapIsPossible = false;

        return sb.toString();
    }

    private void createField() {
        field = new Field(10, 10, 6);
    }

    public Field getField(){
        return field;
    }
}
