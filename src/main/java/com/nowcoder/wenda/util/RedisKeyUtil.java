package com.nowcoder.wenda.util;

public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    //粉丝
    private static String BIZ_FOLLOWER= "FOLLOWER";
    //关注对象
    private static String BIZ_FOLLOWEE = "FOLLOWEE";
    private static String BIZ_TIMELINE = "TIMELINE";
    //private static String SPLIT = ":";

    public static String getLikeKey(int entityType, int entityId){
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType, int entityId){
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getEventQueueKey(){
        return BIZ_EVENTQUEUE;
    }

    public static String getFollowerKey(int entityType, int entityId){
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType)+ SPLIT + String.valueOf(entityId);
    }
    //用户关注某个实体的类，比如用户a关注的所有问题
    public static String getFolloweeKey(int userId, int entityType){
        return BIZ_FOLLOWEE + SPLIT + String.valueOf(userId)+ SPLIT + String.valueOf(entityType);
    }
    public static String getTimelineKey(){return BIZ_TIMELINE;}
}
