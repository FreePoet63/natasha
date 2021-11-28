package com.example.demo.service;

import com.example.demo.domain.Comment;
import com.example.demo.domain.User;
import com.example.demo.domain.Views;
import com.example.demo.dto.EventType;
import com.example.demo.dto.ObjectType;
import com.example.demo.repository.CommentRepository;
import com.example.demo.util.WsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;

@Service
public class CommentService {
    private final CommentRepository repository;
    private final BiConsumer<EventType, Comment> wsSender;

    @Autowired
    public CommentService(CommentRepository repository, WsSender wsSender) {
        this.repository = repository;
        this.wsSender = wsSender.getSender(ObjectType.COMMENT, Views.FullComment.class);
    }

    public Comment create(Comment comment, User user) {
        comment.setAuthor(user);
        Comment commentFromDb = repository.save(comment);
        wsSender.accept(EventType.CREATE, commentFromDb);
        return commentFromDb;
    }
}
