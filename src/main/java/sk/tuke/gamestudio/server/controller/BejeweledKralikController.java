package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.bejeweled.kralik.core.Field;
import sk.tuke.gamestudio.game.bejeweled.kralik.core.GameState;
import sk.tuke.gamestudio.game.bejeweled.kralik.webui.WebUI;
import sk.tuke.gamestudio.service.*;

import java.util.Date;

//http://localhost:8080/bejeweled-kralik
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class BejeweledKralikController {
    private WebUI webUI = new WebUI();
    private final String GAME_NAME = "bejeweled-kralik";

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private UserController userController;

    @RequestMapping("/comment")
    public String comment(Comment comment){
        comment.setGame(GAME_NAME);
        comment.setCommentedOn(new Date());
        if (userController.getLoggedUser() == null)
            return "login";
        comment.setPlayer(userController.getLoggedUser().getUsername());

        try {
            commentService.addComment(comment);
        } catch (CommentException e) {
            e.printStackTrace();
        }

        return "redirect:/"+GAME_NAME;
    }

    @RequestMapping("/rate")
    public String rate(Rating rating){
        rating.setGame(GAME_NAME);
        rating.setRatedOn(new Date());
        if (userController.getLoggedUser() == null)
            return "login";
        rating.setPlayer(userController.getLoggedUser().getUsername());

        try {
            ratingService.setRating(rating);
        } catch (RatingException e) {
            e.printStackTrace();
        }

        return "redirect:/"+GAME_NAME;
    }

    @RequestMapping("/score")
    public String score(Score score){
        score.setGame(GAME_NAME);
        score.setPlayedOn(new Date());
        if (userController.getLoggedUser() == null)
            return "login";
        score.setUsername(userController.getLoggedUser().getUsername());

        try {
            scoreService.addScore(score);
        } catch (ScoreException e) {
            e.printStackTrace();
        }

        return "redirect:/"+GAME_NAME;
    }

    @RequestMapping("/"+GAME_NAME)
    public String bejeweled(@RequestParam(value = "command", required = false) String command,
                            @RequestParam(value = "srcRow", required = false) String srcRow,
                            @RequestParam(value = "srcCol", required = false) String srcCol,
                            @RequestParam(value = "destRow", required = false) String destRow,
                            @RequestParam(value = "destCol", required = false) String destCol, Model model){
        webUI.processCommand(command, srcRow, srcCol, destRow, destCol);
        Field field = webUI.getField();

        if(userController.getLoggedUser() != null && field.getState() != GameState.PLAYING && webUI.getTimeRemaining() >= -5) {
            try {
                scoreService.addScore(new Score(GAME_NAME,
                        userController.getLoggedUser().getUsername(),
                        field.getScore(),
                        new Date()));
                System.out.println("score added");
            } catch (ScoreException e) {
                e.printStackTrace();
            }
        }

        fillModel(model);

        return GAME_NAME; //same name as the template
    }

    private void fillModel(Model model){
        model.addAttribute("webUI", webUI);
        model.addAttribute("timeRemaining", webUI.getTimeRemaining());
        model.addAttribute("currentScore", webUI.getField().getScore());
        model.addAttribute("shapeCount", webUI.getField().getShapeCount());
        model.addAttribute("state", webUI.getField().getState());

        try {
            model.addAttribute("scores", scoreService.getBestScores(GAME_NAME));
        } catch (ScoreException e) {
            e.printStackTrace();
        }

        try {
            model.addAttribute("comments", commentService.getComments(GAME_NAME));
        } catch (CommentException e) {
            e.printStackTrace();
        }

        try {
            model.addAttribute("averageRating", ratingService.getAverageRating(GAME_NAME));
        } catch (RatingException e) {
            e.printStackTrace();
        }

        if(userController.getLoggedUser() != null) {
            try {
                model.addAttribute("rating", ratingService.getRating(GAME_NAME, userController.getLoggedUser().getUsername()));
            } catch (RatingException e) {
                e.printStackTrace();
            }
        }
    }
}
