package com.nowcoder.wenda.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.wenda.controller.CommentController;
import com.nowcoder.wenda.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;

@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool;
    public static void print(int index, Object object) {
        System.out.println(String.format("%d, %s", index, object.toString()));
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("redis://localhost:6379/9");
        jedis.flushDB();

        jedis.set("hello","world");
        print(1,jedis.get("hello"));
        jedis.rename("hello","newhello");
        print(1,jedis.get("newhello"));
        //设置过期时间
        jedis.setex("hello2",15,"world2");

        jedis.set("pv","100");
        jedis.incr("pv");
        jedis.incrBy("pv",5);
        jedis.decrBy("pv",3);
        print(2,jedis.get("pv"));

        print(3,jedis.keys("*"));

        String listName = "list";
        jedis.del(listName);
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listName,"a"+String.valueOf(i));
        }
        print(4,jedis.lrange(listName,0,12));
        print(4,jedis.lrange(listName,0,3));
        print(5,jedis.llen(listName));
        print(6,jedis.lpop(listName));
        print(6,jedis.llen(listName));
        print(7,jedis.lindex(listName,6));
        print(8,jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER,"a4","xx"));
        print(8,jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE,"a4","bb"));
        print(4,jedis.lrange(listName,0,12));

        //hash
        String userKey = "userxx";
        jedis.hset(userKey,"name","jack");
        jedis.hset(userKey,"age","18");
        jedis.hset(userKey,"phone","12345678451");
        print(9,jedis.hget(userKey,"name"));
        print(10,jedis.hgetAll(userKey));
        jedis.hdel(userKey,"phone");
        print(11,jedis.hgetAll(userKey));
        print(12,jedis.hexists(userKey,"email"));
        print(13,jedis.hexists(userKey,"age"));
        print(14,jedis.hkeys(userKey));
        print(15,jedis.hvals(userKey));
        jedis.hsetnx(userKey,"school","zju");
        jedis.hsetnx(userKey,"name","ysy");
        print(16,jedis.hgetAll(userKey));

        jedis.set("pv","12");
        jedis.set("hello","world");
        print(1,jedis.get("hello"));

        //set
        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        for (int i = 0; i < 10; i++) {
            jedis.sadd(likeKey1,String.valueOf(i));
            jedis.sadd(likeKey2,String.valueOf(i*i));
        }
        print(20,jedis.smembers(likeKey1));
        print(20,jedis.smembers(likeKey2));
        print(21,jedis.sunion(likeKey1,likeKey2));
        print(22,jedis.sdiff(likeKey1,likeKey2));
        print(23,jedis.sinter(likeKey1,likeKey2));
        print(24,jedis.sismember(likeKey1,"12"));
        print(25,jedis.sismember(likeKey2,"16"));
        print(26,jedis.srem(likeKey1,"5"));
        print(26,jedis.smembers(likeKey1));
        jedis.smove(likeKey2,likeKey1,"25");
        print(27,jedis.smembers(likeKey1));
        print(28,jedis.scard(likeKey1));
        print(29,jedis.srandmember(likeKey1,2));//随机取值，可以抽奖

        String rankKey = "rankKey";
        jedis.zadd(rankKey,15,"Jim");
        jedis.zadd(rankKey,60,"Ben");
        jedis.zadd(rankKey,90,"Jack");
        jedis.zadd(rankKey,75,"Harry");
        jedis.zadd(rankKey,80,"Lucy");
        print(30,jedis.zcard(rankKey));
        print(31,jedis.zcount(rankKey,60,100));
        print(32,jedis.zscore(rankKey,"Lucy"));
        jedis.zincrby(rankKey,2,"Lucy");
        print(33,jedis.zscore(rankKey,"Lucy"));
        jedis.zincrby(rankKey,2,"Luc");
        print(34,jedis.zscore(rankKey,"Luc"));
        print(25,jedis.zrange(rankKey,0,100));
        print(36,jedis.zrange(rankKey,0,10));
        print(37,jedis.zrange(rankKey,1,3));
        print(37,jedis.zrevrange(rankKey,1,3));
        for (Tuple tuple:jedis.zrangeByScoreWithScores(rankKey,"60","100")){
            print(38,tuple.getElement()+":"+String.valueOf(tuple.getScore()));
        }
        print(28,jedis.zrank(rankKey,"Ben"));
        print(28,jedis.zrevrank(rankKey,"Ben"));

        //根据字母排序
        String setKey = "zset";
        jedis.zadd(setKey,1,"a");
        jedis.zadd(setKey,1,"b");
        jedis.zadd(setKey,1,"c");
        jedis.zadd(setKey,1,"d");
        jedis.zadd(setKey,1,"e");

        print(40,jedis.zlexcount(setKey,"-","+"));
        print(40,jedis.zlexcount(setKey,"(b","[d"));
        print(40,jedis.zlexcount(setKey,"[b","[d"));
        jedis.zrem(setKey,"b");
        print(43,jedis.zrange(setKey,1,10));
        jedis.zremrangeByLex(setKey,"(c","+");
        print(43,jedis.zrange(setKey,1,10));

        print(44,jedis.get("pv"));
//        JedisPool pool = new JedisPool("redis://localhost:6379/9");
//        for (int i = 0; i < 100; i++) {
//            Jedis j = pool.getResource();
//            j.set("ps","15");
//            print(45,j.get("ps"));
//            j.close();
//        }

        //redis做缓存，通过JSON把对象序列化存到Redis，再取出来做反序列化
        User user = new User();
        user.setName("ming");
        user.setPassword("123456");
        user.setHeadUrl("a.png");
        user.setSalt("salt");
        user.setId(1);
        jedis.set("user1", JSONObject.toJSONString(user));
        print(46,jedis.get("user1"));

        String value = jedis.get("user1");
        User user2 = JSON.parseObject(value,User.class);
        print(57,user2);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool  = new JedisPool("redis://localhost:6379/10");
    }

    public long sadd(String key, String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key,value);
        } catch (Exception e) {
            logger.error("发生异常:"+e.getMessage());
        }finally {
            if (jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key, String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key,value);
        } catch (Exception e) {
            logger.error("发生异常:"+e.getMessage());
        }finally {
            if (jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常:"+e.getMessage());
        }finally {
            if (jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public boolean sismember(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key,value);
        } catch (Exception e) {
            logger.error("发生异常:"+e.getMessage());
        }finally {
            if (jedis!=null){
                jedis.close();
            }
        }
        return false;
    }


    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key,value);
        } catch (Exception e) {
            logger.error("发生异常:"+e.getMessage());
        }finally {
            if (jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout,key);
        } catch (Exception e) {
            logger.error("发生异常:"+e.getMessage());
        }finally {
            if (jedis!=null){
                jedis.close();
            }
        }
        return null;
    }
}
