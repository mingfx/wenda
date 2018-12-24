package com.nowcoder.wenda.service;

import com.nowcoder.wenda.dao.CommentDAO;
import com.nowcoder.wenda.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;

    public List<Comment> getCommentsByEntity(int entityId,int entityType){
        return commentDAO.selectCommentByEntity(entityId,entityType);
    }

    public int addComment(Comment comment){
        return commentDAO.addComment(comment) > 0 ? comment.getId() : 0;
    }

    public int getCommentCount(int entityId,int entityType){
        return commentDAO.getCommentCount(entityId,entityType);
    }

    public boolean deleteComment(int commentId){
        return commentDAO.updateStatus(commentId,1) > 0;
    }
}
