package com.nowcoder.wenda.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.wenda.util.JedisAdapter;
import com.nowcoder.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel){
        try {
            //可以用blockingQueue
            //BlockingQueue<EventModel> q = new ArrayBlockingQueue<EventModel>(10);
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key,json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
