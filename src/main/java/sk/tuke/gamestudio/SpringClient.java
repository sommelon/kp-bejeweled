package sk.tuke.gamestudio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.tuke.gamestudio.service.*;
//import main.java.tuke.gamestudio.bejeweled.bot.Bot;
//import main.java.tuke.gamestudio.bejeweled.bot.StupidBot;
//import main.java.tuke.gamestudio.bejeweled.consoleui.BotConsoleUI;

@Configuration
@SpringBootApplication
public class SpringClient {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringClient.class).web(false).run(args);
    }

    @Bean
    public CommandLineRunner runner(sk.tuke.gamestudio.game.bejeweled.kralik.consoleui.ConsoleUI ui) {
        return args -> ui.run();
    }

    @Bean
    public sk.tuke.gamestudio.game.bejeweled.kralik.consoleui.ConsoleUI bejeweledKralikConsoleUI() {
        return new sk.tuke.gamestudio.game.bejeweled.kralik.consoleui.ConsoleUI();
    }

    @Bean
    public sk.tuke.gamestudio.game.bejeweled.kralik.core.Field bejeweledKralikField() {
        return new sk.tuke.gamestudio.game.bejeweled.kralik.core.Field(5, 5, 6);
    }

    @Bean
    public ScoreService scoreService() {
        return new ScoreServiceRestClient();
    }

    @Bean
    public CommentService commentService() {
        return new CommentServiceRestClient();
    }

    @Bean
    public RatingService ratingService() {
        return new RatingServiceRestClient();
    }

}
