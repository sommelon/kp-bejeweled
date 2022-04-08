package sk.tuke.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.ScoreException;
import sk.tuke.gamestudio.service.ScoreService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/score")
public class ScoreServiceRest {
    //TODO: pridať autowired inštanciu servisného komponentu JPA
    @Autowired
    private ScoreService scoreService;

    //http://localhost:8080/rest/score
    @POST
    @Consumes("application/json")
    public Response addScore(Score score) throws ScoreException {
        //TODO: pridať score prostredníctvom servisného komponentu JPA
        scoreService.addScore(score);
        return Response.ok().build();
    }

    //http://localhost:8080/rest/score/bejeweled-kralik
    @GET
    @Path("/{game}")
    @Produces("application/json")
    public List<Score> getBestScores(@PathParam("game") String game) throws ScoreException {
        //TODO: vrátiť skóre prostredníctovm servisného komponentu JPA
        return scoreService.getBestScores(game);
    }
}
