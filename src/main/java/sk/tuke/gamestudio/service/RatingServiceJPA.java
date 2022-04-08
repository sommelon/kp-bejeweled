package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
public class RatingServiceJPA implements RatingService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        entityManager.createNamedQuery("Rating.deleteRating")
                .setParameter("player",rating.getPlayer()).setParameter("game", rating.getGame())
                .executeUpdate();
        entityManager.persist(rating);
    }

    @Override
    public int getAverageRating(String gameName) throws RatingException {
        try{
            return (int) Math.ceil(((Number) entityManager.createNamedQuery("Rating.getAverageRating")
                .setParameter("game", gameName).getSingleResult()).doubleValue());
        }catch (NullPointerException | NoResultException e){
            return 0;
        }
    }

    @Override
    public int getRating(String gameName, String player) throws RatingException {
        try {
            return ((Number) entityManager.createNamedQuery("Rating.getRating")
                    .setParameter("game", gameName).setParameter("player", player).getSingleResult()).intValue();
        }catch (NullPointerException | NoResultException e){
            return 0;
        }
    }
}
