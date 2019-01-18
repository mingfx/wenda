package com.nowcoder.wenda.async;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.wenda.controller.CommentController;
import com.nowcoder.wenda.util.JedisAdapter;
import com.nowcoder.wenda.util.RedisKeyUtil;
import org.apache.juli.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<EventType, List<EventHandler>>();
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        //通过spring的上下文可以知道有多少个EventHandler接口的实现类(从容器里找就不需要用配置文件等等，在初始化的时候就会注册好）
        Map<String,EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans!=null){
            for (Map.Entry<String,EventHandler> entry : beans.entrySet()){
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

                for (EventType type:eventTypes){
                    if (!config.containsKey(type)){
                        config.put(type,new ArrayList<EventHandler>());
                    }
//                    List<EventHandler> handlers = config.get(type);
//                    handlers.add(entry.getValue());
//                    config.put(type,handlers);
                    config.get(type).add(entry.getValue());//map可以直接取出来修改吗？
                }
            }
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            //启动后，开启一个线程循环的去查queue里有没有event，如果有就调用List<EventHandler>一个一个处理
            public void run() {
                while (true){
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> events = jedisAdapter.brpop(0,key);
                    for (String event:events){
                        //返回的第一个是key
                        if (event.equals(key)){
                            continue;
                        }
                        //反序列化
                        EventModel eventModel = JSON.parseObject(event,EventModel.class);
                        if (!config.containsKey(eventModel.getType())){
                            logger.error("不能识别的事件:"+eventModel.getType());
                            continue;
                        }

                        for (EventHandler handler:config.get(eventModel.getType())){
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
