package com.nowcoder.wenda.async.handler;

import com.nowcoder.wenda.async.EventHandler;
import com.nowcoder.wenda.async.EventModel;
import com.nowcoder.wenda.async.EventType;
import com.nowcoder.wenda.model.EntityType;
import com.nowcoder.wenda.model.Feed;
import com.nowcoder.wenda.service.FeedService;
import com.nowcoder.wenda.util.JedisAdapter;
import com.nowcoder.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

//一定要添加component注解，否则在启动时spring不能根据实现类来找到这个类!
@Component
public class UnFollowHandler implements EventHandler {
    @Autowired
    FeedService feedService;
    @Autowired
    JedisAdapter jedisAdapter;
    @Override
    public void doHandle(EventModel model) {
        //Redis feed流中删除相关feed
        if (model.getEntityType()== EntityType.ENTITY_USER){
            List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE,Arrays.asList(model.getEntityId()),Integer.MAX_VALUE);
            if (feeds!=null){
                for (Feed feed:feeds){
                    int feedId = feed.getId();
                    String timelineKey = RedisKeyUtil.getTimelineKey(model.getActorId());
                    jedisAdapter.lrem(timelineKey,0,String.valueOf(feedId));
                }
            }
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.UNFOLLOW);
    }
}
