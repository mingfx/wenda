package com.nowcoder.wenda.controller;

import com.nowcoder.wenda.model.EntityType;
import com.nowcoder.wenda.model.Feed;
import com.nowcoder.wenda.model.HostHolder;
import com.nowcoder.wenda.service.FeedService;
import com.nowcoder.wenda.service.FollowService;
import com.nowcoder.wenda.util.JedisAdapter;
import com.nowcoder.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {
    @Autowired
    FeedService feedService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowService followService;
    @Autowired
    JedisAdapter jedisAdapter;

    //拉模式，就是从数据库中取出
    @RequestMapping(path = {"/pullfeeds"},method = {RequestMethod.GET})
    private String getPullFeeds(Model model){
        int localUserId = hostHolder.getUser()==null?0:hostHolder.getUser().getId();
        List<Integer> followees = new ArrayList<>();
        if (localUserId!=0){
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER, Integer.MAX_VALUE);
        }
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, 10);
        model.addAttribute("feeds",feeds);
        return "feeds";
    }

    //推模式，从Redis中存的每个用户的feeds列表中取出
    @RequestMapping(path = {"/pushfeeds"},method = {RequestMethod.GET})
    private String getPushFeeds(Model model){
        //如果没登录，就给0，让他从系统给id（所有新鲜事）里取
        int localUserId = hostHolder.getUser()==null?0:hostHolder.getUser().getId();
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId),0,10);
        List<Feed> feeds = new ArrayList<Feed>();
        for (String feedId:feedIds){
            Feed feed = feedService.getById(Integer.parseInt(feedId));
            if (feed==null){
                continue;
            }
            feeds.add(feed);
        }
        model.addAttribute("feeds",feeds);
        return "feeds";
    }
}
