package sk.tuke.gamestudio.game.bejeweled.kralik.consoleui;

import org.springframework.beans.factory.annotation.Autowired;
import sk.tuke.gamestudio.game.bejeweled.kralik.core.Field;
import sk.tuke.gamestudio.game.bejeweled.kralik.core.GameState;
import sk.tuke.gamestudio.game.bejeweled.kralik.shapes.*;
import sk.tuke.gamestudio.service.*;
import sk.tuke.gamestudio.entity.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleUI {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private boolean exitedFromGame = false;
    private int rowCount;
    private int columnCount;
    private int shapeCount;
    private Field field;
    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private final Pattern SET_FIELD_SIZE = Pattern.compile("([3-9]|[1-4][0-9]) ([3-9]|[1-4][0-9]) ([3-6])");
    private final Pattern INPUT_PATTERN = Pattern.compile("([0-9]+) ([0-9]+) ([0-9]+) ([0-9]+)");

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;

    public ConsoleUI() {
        setFieldSize();
    }

    public void run(){
        playGame();

        try {
            scoreService.addScore(new Score(
                    Field.GAME_NAME,
                    System.getProperty("user.name"),
                    field.getScore(),
                    new Date()
            ));
            System.out.println("Your score was added to the database");
            printBestScores();
        } catch (ScoreException e) {
            System.err.println(e.getMessage());
        }

        while (true) {
            displayOptions();

            String input = readLine();
            if (input.equalsIgnoreCase("c"))
                handleComment();
            else if (input.equalsIgnoreCase("r"))
                handleRating();
            else if (input.equalsIgnoreCase("s"))
                printBestScores();
            else if (input.equalsIgnoreCase("n"))
                run();
            else if(input.equalsIgnoreCase("x"))
                System.exit(0);
        }
    }

    private void playGame() {
        exitedFromGame = false;
        this.field = new Field(rowCount, columnCount, shapeCount);

        while (!field.areTherePossibleMoves()) {
            System.out.println("No possible moves. Generating new field.");
            field = new Field(rowCount, columnCount, shapeCount);
        }
        do {
            System.out.println("Score = " + field.getScore());
            showField();
            handleInput();
            if (exitedFromGame)
                return;
            field.removeCombos();
            field.clearCombosList();
            while (field.hasNullTiles()){
                field.moveTilesDown();
                field.fillTheField();
                if (field.areThereNewCombos()){
                    showField();
                    field.removeCombos();
                    field.clearCombosList();
                }
            }
            field.areTherePossibleMoves();
        } while (field.getState() == GameState.PLAYING);

        showField();

        System.out.println("No more possible moves");
        System.out.printf("Your score was: %d%n%n", field.getScore());
    }

    private void displayOptions(){
        System.out.println("To display scoreboard, type [s]");
        System.out.println("To comment, type [c]");
        System.out.println("To rate, type [r]");
        System.out.println("To quit, type [x]");
        System.out.println("To start a new game, type [n]");
    }

    private void handleComment() {
        printComments();
        System.out.println("Type a comment. [x] to return: ");
        String input = readLine();

        if (input.equalsIgnoreCase("x"))
            return;

        try {
            commentService.addComment(new Comment(
                    Field.GAME_NAME,
                    System.getProperty("user.name"),
                    input,
                    new Date()
            ));
            System.out.println("Your comment was added to the database");
            printComments();
        } catch (CommentException e) {
            System.err.println(e.getMessage());
        }
    }

    private void handleRating(){
        int input = -1;
        try {
            System.out.println("Your old rating is: " + ratingService.getRating(Field.GAME_NAME, System.getProperty("user.name")) + "\n");
        } catch (RatingException e) {
            System.err.println(e);
        }
        do {
            try {
                System.out.println("Rate from 1 to 5. [0] to return.");
                input = Integer.parseInt(readLine());
                if (input == 0) {
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("Only numbers accepted.");
            }
        }while (input > 5 || input < 1);

        try {
            ratingService.setRating(new Rating(
                    Field.GAME_NAME,
                    System.getProperty("user.name"),
                    input,
                    new Date()
            ));
            System.out.println("Your rating was added to the database");
            System.out.println("Average rating is: " + ratingService.getAverageRating(Field.GAME_NAME) + "\n");
        } catch (RatingException e) {
            System.err.println(e.getMessage());
        }
    }

    private void printComments() {
        try {
            List<Comment> comments = commentService.getComments(Field.GAME_NAME);

            for(Comment c : comments) {
                System.out.println(c);
            }
            System.out.println();
        } catch (CommentException e) {
            System.err.println(e.getMessage());
        }
    }

    private void handleInput() {
        System.out.println("[srcRow] [srcCol] [destRow] [destCol]");
        String input = readLine();

        if(input.equalsIgnoreCase("x")) {
            exitedFromGame = true;
            return;
        }

        if (input.equalsIgnoreCase("r"))
            run();

        Matcher matcher = INPUT_PATTERN.matcher(input);
        if (matcher.matches()){
            int srcRow = Integer.parseInt(matcher.group(1));
            int srcCol = Integer.parseInt(matcher.group(2));
            int destRow = Integer.parseInt(matcher.group(3));
            int destCol = Integer.parseInt(matcher.group(4));

            if (srcRow >= rowCount || destRow >= rowCount || srcRow < 0 || destRow < 0) {
                System.out.println("There is no such row.");
                return;
            }
            if (srcCol >= columnCount || destCol >= columnCount || srcCol < 0 || destCol < 0) {
                System.out.println("There is no such col.");
                return;
            }

            field.swapTilesIfPossible(srcRow, srcCol, destRow, destCol);
        } else {
            System.out.println("Wrong input. Try again.");
        }
    }

    private void printBestScores() {
        try {
            List<Score> scores = scoreService.getBestScores(Field.GAME_NAME);

            int i = 1;
            for(Score s : scores) {
                System.out.println(i++ + ". " + s);
            }
            System.out.println();
        } catch (ScoreException e) {
            System.err.println(e.getMessage());
        }
    }

    private String readLine() {
        try {
            return br.readLine();
        } catch (IOException e) {
            System.err.println("Nepodarilo sa nacitat vstup, skus znova");
            return "";
        }
    }

    private void showField(){
        System.out.print("  ");
        for (int column = 0; column < field.getColumnCount(); column++){
            System.out.print(column+" ");
        }
        System.out.println();
        for (int row = 0; row < field.getRowCount(); row++) {
            System.out.print(row);
            for (int column = 0; column < field.getColumnCount(); column++) {
                System.out.print(ANSI_RESET + "|");
                if (field.getTile(row, column) instanceof Circle) {
                    System.out.print(ANSI_RED + "C");
                }else if (field.getTile(row, column) instanceof Diamond){
                    System.out.print(ANSI_BLUE + "D");
                }else if (field.getTile(row, column) instanceof Hexagon){
                    System.out.print(ANSI_GREEN + "H");
                }else if (field.getTile(row, column) instanceof Square){
                    System.out.print(ANSI_PURPLE + "Q");
                }else if (field.getTile(row, column) instanceof Star){
                    System.out.print(ANSI_CYAN + "S");
                }else if (field.getTile(row, column) instanceof Triangle){
                    System.out.print(ANSI_YELLOW + "T");
                }else{
                    System.out.print(ANSI_RESET + " ");
                }
            }
            System.out.println(ANSI_RESET + "|" + row);
        }

        System.out.print("  ");
        for (int column = 0; column < field.getColumnCount(); column++){
            System.out.print(column+" ");
        }
        System.out.println();
    }

    private void setFieldSize() {
        System.out.println("[rowCount] [columnCount] [shapeCount]");
        Matcher fieldSizeMatcher = SET_FIELD_SIZE.matcher(readLine());
        while (!fieldSizeMatcher.matches()){
            System.out.println("Wrong input. Row count and column count must be a number in range 3-50. " +
                    "Shape count must be in range 3-6");
            fieldSizeMatcher = SET_FIELD_SIZE.matcher(readLine());
        }
        rowCount = Integer.parseInt(fieldSizeMatcher.group(1));
        columnCount = Integer.parseInt(fieldSizeMatcher.group(2));
        shapeCount = Integer.parseInt(fieldSizeMatcher.group(3));
    }
}
