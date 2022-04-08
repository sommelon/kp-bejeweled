package sk.tuke.gamestudio.game.bejeweled.kralik.core;

import sk.tuke.gamestudio.game.bejeweled.kralik.entity.GamePlay;
import sk.tuke.gamestudio.game.bejeweled.kralik.entity.TileInfo;
import sk.tuke.gamestudio.game.bejeweled.kralik.shapes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Field {
    public static final String GAME_NAME = "bejeweled-kralik";

    private final int rowCount;
    private final int columnCount;
    private int shapeCount;
    private int score = 0;
    private long startMillis;

    private List<Coordinates> verticalCombos = new ArrayList<>();
    private List<Coordinates> horizontalCombos = new ArrayList<>();
    private List<Coordinates> superCombos = new ArrayList<>();
    private List<Coordinates> lastGeneratedTiles = new ArrayList<>();
    private List<Coordinates> possibleMovesSrc = new ArrayList<>();
    private List<Coordinates> possibleMovesDest = new ArrayList<>();
    private Tile[][] tiles;
    private GameState state = GameState.PLAYING;
    private GamePlay gamePlay;


    public Field(int rowCount, int columnCount, int shapeCount){
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.shapeCount = shapeCount;

        tiles = new Tile[rowCount][columnCount];
        gamePlay = new GamePlay(rowCount, columnCount);

        generateFieldWithoutCombos();
    }

    //Used at the start of the game to generate a field without combos
    //from the last row's left corner to the first row's right corner
    private void generateFieldWithoutCombos(){
        Tile[][] tileInfos = new Tile[rowCount][columnCount];

        for (int row = rowCount-1; row >= 0; row--){
            for (int column = 0; column < columnCount; column++){
                if (tiles[row][column] == null){
                    tiles[row][column] = generateRandomShape();
                    while(checkCombo(tiles[row][column], row, column)) {
                        tiles[row][column] = generateRandomShape();
                    }
                    lastGeneratedTiles.add(new Coordinates(row, column));
//                        tileInfos.add(new TileInfo(tile.getTileShape(), row, column));
                    tileInfos[row][column] = tiles[row][column];
                }
            }
        }

        gamePlay.setTileInfos(tileInfos);

        clearCombosList();
        startMillis = System.currentTimeMillis();
    }

    //Removes combos from the playing field and clears the combos list
    public void removeCombos() {
        countScore();
        for (Coordinates combo : verticalCombos) {
            tiles[combo.getRow()][combo.getColumn()] = null;
        }
        for (Coordinates combo : horizontalCombos) {
            tiles[combo.getRow()][combo.getColumn()] = null;
        }
    }

    //Swaps source tile with the first not null tile above it
    public void moveTilesDown(){
        int upperRow;
        for (int row = rowCount-1; row >= 0; row--){
            for (int column = 0; column < columnCount; column++){
                upperRow = row;

                while (tiles[row][column] == null && row != 0 && upperRow != 0){
                    upperRow--;
                    swapTiles(row, column, upperRow, column);
                }
            }
        }
    }

    //Fills the field with random shapes. Combos can occur.
    public void fillTheField(){
//        lastGeneratedTiles.clear();
        for (int row = rowCount-1; row >= 0; row--){
            for (int column = 0; column < columnCount; column++){
                if (tiles[row][column] == null){
                    tiles[row][column] = generateRandomShape();
                    lastGeneratedTiles.add(new Coordinates(row, column));
                }
            }
        }
    }

    //Swaps the tiles if it's possible
    public void swapTilesIfPossible(final int sourceRow, final int sourceColumn,
                                    final int destinationRow, final int destinationColumn) {

        if (!isTheSwapPossible(sourceRow, sourceColumn, destinationRow, destinationColumn, true)){
            System.out.println("Can't swap the tiles.");
            return;
        }

        swapTiles(sourceRow, sourceColumn, destinationRow, destinationColumn);
        getPossibleMovesSrcCoordinates().clear();
        getPossibleMovesDestCoordinates().clear();

    }

    //Checks if the tiles can be swapped with each other
    public boolean isTheSwapPossible(final int sourceRow, final int sourceColumn,
                                      final int destinationRow, final int destinationColumn, boolean saveCombos) {

        int verticalDistance = Math.abs(sourceRow - destinationRow);
        int horizontalDistance = Math.abs(sourceColumn - destinationColumn);

        if (horizontalDistance + verticalDistance != 1)
            return false;

        swapTiles(sourceRow, sourceColumn, destinationRow, destinationColumn);

        boolean destinationTileCombo = checkCombo(tiles[destinationRow][destinationColumn], destinationRow, destinationColumn);
        boolean sourceTileCombo = checkCombo(tiles[sourceRow][sourceColumn], sourceRow, sourceColumn);

        if (!saveCombos) {
            clearCombosList();
        }

        if (sourceTileCombo || destinationTileCombo) {
            swapTiles(sourceRow, sourceColumn, destinationRow, destinationColumn);
            if (!saveCombos) {
                possibleMovesSrc.add(new Coordinates(sourceRow, sourceColumn));
                possibleMovesDest.add(new Coordinates(destinationRow, destinationColumn));
                System.out.println("Possible move: " + sourceRow + ":" + sourceColumn + " " + destinationRow + ":" + destinationColumn);
            }
            return true;
        } else {
            swapTiles(sourceRow, sourceColumn, destinationRow, destinationColumn);
            return false;
        }
    }

    //Swaps the tiles no matter where they are
    private void swapTiles(final int sourceRow, final int sourceColumn,
                           final int destinationRow, final int destinationColumn) {

        if (tiles[destinationRow][destinationColumn] != null) {
            if (tiles[sourceRow][sourceColumn] == null){
                tiles[sourceRow][sourceColumn] = tiles[destinationRow][destinationColumn].getTileShape();
                tiles[destinationRow][destinationColumn] = null;
            }else {
                Tile tileToSwap = tiles[sourceRow][sourceColumn].getTileShape();
                tiles[sourceRow][sourceColumn] = tiles[destinationRow][destinationColumn].getTileShape();
                tiles[destinationRow][destinationColumn] = tileToSwap;
            }
        }
    }

    //Checks if the tile is a part of the combo
    private boolean checkCombo(Tile tile, final int rowPosition, final int columnPosition){
        boolean verticalCombo = checkComboVertically(tile, rowPosition, columnPosition);
        boolean horizontalCombo = checkComboHorizontally(tile, rowPosition, columnPosition);

        if (verticalCombo)
            verticalCombos.add(new Coordinates(rowPosition, columnPosition));
        if (horizontalCombo)
            horizontalCombos.add(new Coordinates(rowPosition, columnPosition));
        if(verticalCombo && horizontalCombo) {
            superCombos.add(new Coordinates(rowPosition, columnPosition));
        }

        return verticalCombo || horizontalCombo;
    }

    //Checks if the tile is a part of the combo on the vertical axis
    private boolean checkComboVertically(Tile tile, final int rowPosition, final int columnPosition){
        int sameShapeUpCounter = 0;
        int sameShapeDownCounter = 0;

        int row = rowPosition;
        if (rowPosition != 0)
            row = rowPosition - 1;

        while (rowPosition != 0 && row >= 0 && tiles[row][columnPosition] != null
                && tile != null && tiles[row][columnPosition].getClass().equals(tile.getClass())){

            sameShapeUpCounter++;
            verticalCombos.add(new Coordinates(row, columnPosition));

            row--; //UP
        }
        row = rowPosition;
        if (rowPosition != rowCount)
            row = rowPosition + 1;

        while (rowPosition != rowCount-1 && row <= rowCount-1 && tiles[row][columnPosition] != null
                && tile != null && tiles[row][columnPosition].getClass().equals(tile.getClass())){

            sameShapeDownCounter++;
            verticalCombos.add(new Coordinates(row, columnPosition));

            row++; //DOWN
        }

        //if there is 1 or 0 same shape tiles, remove source tile and neighboor tile from the combos list
        if (sameShapeUpCounter + sameShapeDownCounter == 1) {
            verticalCombos.remove(verticalCombos.size() - 1);
            return false;
        }else if (sameShapeUpCounter + sameShapeDownCounter == 0)
            return false;

        return true;
    }

    //Checks if the tile is a part of the combo on the horizontal axis
    private boolean checkComboHorizontally(Tile tile, int rowPosition, int columnPosition){
        int sameShapeLeftCounter = 0;
        int sameShapeRightCounter = 0;

        int column = columnPosition;
        if (columnPosition != columnCount)
            column = columnPosition + 1;

        while (columnPosition != columnCount-1 && column <= columnCount-1 && tiles[rowPosition][column] != null
                && tile != null && tiles[rowPosition][column].getClass().equals(tile.getClass())){

            sameShapeRightCounter++;
            horizontalCombos.add(new Coordinates(rowPosition, column));

            column++; //RIGHT
        }

        column = columnPosition;
        if (columnPosition != 0)
            column = columnPosition - 1;

        while (columnPosition != 0 && column >= 0 && tiles[rowPosition][column] != null
                && tile != null && tiles[rowPosition][column].getClass().equals(tile.getClass())){

            sameShapeLeftCounter++;
            horizontalCombos.add(new Coordinates(rowPosition, column));

            column--; //LEFT
        }

        //if there is 1 or 0 same shape tiles, remove source tile and neighboor tile from the combos list
        if (sameShapeLeftCounter + sameShapeRightCounter == 1) {
            horizontalCombos.remove(horizontalCombos.size() - 1);
            return false;
        }else if (sameShapeLeftCounter + sameShapeRightCounter == 0)
            return false;

        return true;
    }

    //Tries to swap all the tiles with their neighbooring tiles and counts the possible moves.
    //Used to determine if the game is still playable. Changes game state to NO_POSSIBLE_MOVES if not.
    public boolean areTherePossibleMoves(){
        int possibleMoves = 0;
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++){
                if(row != rowCount - 1 && isTheSwapPossible(row, column, row+1, column, false))
                    possibleMoves++;
                if(row != 0 && isTheSwapPossible(row, column, row-1, column, false))
                    possibleMoves++;
                if(column != columnCount -1 && isTheSwapPossible(row, column, row, column+1, false))
                    possibleMoves++;
                if(column != 0 && isTheSwapPossible(row, column, row, column-1, false))
                    possibleMoves++;
            }
        }
        System.out.println("moves: " + possibleMoves);
        if (possibleMoves == 0) {
            state = GameState.NO_POSSIBLE_MOVES;
            return false;
        }
        return true;
    }

    //Used after the fillTheField() to check for new combos
    public boolean areThereNewCombos(){
        int comboCount = 0;
        for (int row = rowCount-1; row >= 0; row--){
            for (int column = 0; column < columnCount; column++){
                if(checkCombo(tiles[row][column], row, column))
                    comboCount++;
            }
        }
        return comboCount != 0;
    }

    private Tile generateRandomShape() {
        Random randomNumber = new Random();
        switch (randomNumber.nextInt(shapeCount)){
            case 0:
                return new Square();
            case 1:
                return new Triangle();
            case 2:
                return new Star();
            case 3:
                return new Circle();
            case 4:
                return new Hexagon();
            case 5:
                return new Diamond();
        }

        return null;
    }

    //Counts score based on number of tiles in combo and combo shapes
    private void countScore(){
        //Remove duplicate tiles from the combos list
        //Duplicate tiles are added when the field is randomly filled and there happen to be some combos
        removeDuplicatesFromAnArray(verticalCombos);
        removeDuplicatesFromAnArray(horizontalCombos);
        removeDuplicatesFromAnArray(superCombos);

//        score += (horizontalCombos.size() + verticalCombos.size()) * (superCombos.size() + 1);
        score += (horizontalCombos.size() + verticalCombos.size());
    }

    //Checks if the field has null tiles. Used to determine if the field needs to be filled.
    public boolean hasNullTiles() {
        for (int row = rowCount-1; row >= 0; row--){
            for (int column = 0; column < columnCount; column++){
                if (tiles[row][column] == null){
                    return true;
                }
            }
        }
        return false;
    }

    private void removeDuplicatesFromAnArray(List<Coordinates> list){
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (j != i && list.get(i).getRow() == list.get(j).getRow()
                        && list.get(i).getColumn() == list.get(j).getColumn())
                    list.remove(j--);
            }
        }
    }

    public void clearCombosList(){
        verticalCombos.clear();
        horizontalCombos.clear();
        superCombos.clear();
    }

    public void clearWebUIHelpArrays(){
        lastGeneratedTiles.clear();
        clearCombosList();
    }

    public int getScore() {
//        if (score == 0)
//            return 0;
//        return (rowCount * columnCount) * 1000 / (score / shapeCount);
        return score;
    }

    public void setShapeCount(int count){
        this.shapeCount = count;
    }

    public int getShapeCount(){
        return shapeCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public Tile getTile(int row, int column) {
        return tiles[row][column];
    }

    public GameState getState() {
        return state;
    }

    public int getPlayingTime() {
        return ((int) (System.currentTimeMillis() - startMillis)) / 1000;
    }

    public List<Coordinates> getLastGeneratedTiles() {
        removeDuplicatesFromAnArray(lastGeneratedTiles);
        return lastGeneratedTiles;
    }

    public List<Coordinates> getVerticalCombos() {
        return verticalCombos;
    }

    public List<Coordinates> getHorizontalCombos() {
        return horizontalCombos;
    }

    public void timeOut(){
        this.state = GameState.TIME_OUT;
    }

    public List<Coordinates> getPossibleMovesSrcCoordinates() {
        return possibleMovesSrc;
    }

    public List<Coordinates> getPossibleMovesDestCoordinates() {
        return possibleMovesDest;
    }
}
