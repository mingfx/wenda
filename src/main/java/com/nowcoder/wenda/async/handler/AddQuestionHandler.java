package com.nowcoder.wenda.async.handler;

import com.nowcoder.wenda.async.EventHandler;
import com.nowcoder.wenda.async.EventModel;
import com.nowcoder.wenda.async.EventType;
import com.nowcoder.wenda.controller.CommentController;
import com.nowcoder.wenda.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AddQuestionHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(AddQuestionHandler.class);

    @Autowired
    SearchService searchService;
    @Override
    public void doHandle(EventModel model) {
        try {
            searchService.indexQuestion(model.getEntityId(),model.getExt("title"),
                    model.getExt("content"));
        } catch (Exception e) {
            logger.error("添加题目索引错误:"+e.getMessage());
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.ADD_QUESTION});
    }
}
