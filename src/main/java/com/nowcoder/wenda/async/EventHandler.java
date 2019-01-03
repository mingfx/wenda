package com.nowcoder.wenda.async;

import java.util.List;

public interface EventHandler {
    //处理handle
    void doHandle(EventModel model);
    //注册自己，让别人知道我关注哪些event
    List<EventType> getSupportEventTypes();
}
