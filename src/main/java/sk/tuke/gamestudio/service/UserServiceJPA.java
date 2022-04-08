package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
public class UserServiceJPA implements UserService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User login(String username, String passwd) {
        try {
            return (User) entityManager.createNamedQuery("User.login")
                    .setParameter("username", username).setParameter("passwd", passwd).getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    @Override
    public User register(String username, String passwd) {
        try {
            entityManager.createNamedQuery("User.register")
                    .setParameter("username", username).getSingleResult();
            return null;
        }catch (NoResultException e){
            User user = new User(username, passwd);
            entityManager.persist(user);

            return user;
        }

    }
}
