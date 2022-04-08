package sk.tuke.gamestudio.service;
import sk.tuke.gamestudio.entity.Comment;

import java.util.List;

public interface CommentService {
    void addComment(Comment comment) throws CommentException;
    List getComments(String game) throws CommentException;
}