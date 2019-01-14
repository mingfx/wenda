package com.nowcoder.wenda.controller;

import com.nowcoder.wenda.model.*;
import com.nowcoder.wenda.service.*;
import com.nowcoder.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowService followService;

    @Autowired
    LikeService likeService;

    @RequestMapping(value = "/question/add",method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content){
        try {
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            //没登录设置匿名用户
            if (hostHolder.getUser()==null){
                //question.setUserId(WendaUtil.ANONYMOUS_USERID);
                //不用匿名用户，未登录直接返回999，登录页面
                return WendaUtil.getJSONString(999);
            }else {
                question.setUserId(hostHolder.getUser().getId());
            }
            if(questionService.addQuestion(question)>0){
                return WendaUtil.getJSONString(0);
            }

        }catch (Exception e){
            logger.error("增加题目失败"+e.getMessage());
        }

        return WendaUtil.getJSONString(1,"失败");
    }

    @RequestMapping(value = "/question/{qid}")
    public String questionDetail(Model model,
            @PathVariable("qid") int qid){
        Question question = questionService.selectById(qid);
        model.addAttribute("question",question);
        model.addAttribute("user",userService.getUser(question.getUserId()));
        List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
        //评论显示时需要和user等信息关联起来，因此要使用ViewObject
        List<ViewObject> comments = new ArrayList<ViewObject>();
        for (Comment comment:commentList){
            ViewObject vo = new ViewObject();
            vo.set("comment",comment);
            if (hostHolder.getUser()==null){
                vo.set("liked",0);
            }else {
                vo.set("liked",likeService.getLikeStatus(hostHolder.getUser().getId(),EntityType.ENTITY_COMMENT,comment.getId()));
            }

            vo.set("likeCount",likeService.getLikeCount(EntityType.ENTITY_COMMENT,comment.getId()));
            vo.set("user",userService.getUser(comment.getUserId()));
            comments.add(vo);
        }
        model.addAttribute("comments",comments);

        List<ViewObject> followUsers = new ArrayList<ViewObject>();
        // 获取关注的用户信息
        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 20);
        for (Integer userId : users) {
            ViewObject vo = new ViewObject();
            User u = userService.getUser(userId);
            if (u == null) {
                continue;
            }
            vo.set("name", u.getName());
            vo.set("headUrl", u.getHeadUrl());
            vo.set("id", u.getId());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers", followUsers);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
        } else {
            model.addAttribute("followed", false);
        }
        return "detail";
    }
}
