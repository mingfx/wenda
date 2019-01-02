package com.nowcoder.wenda.util;

import com.nowcoder.wenda.controller.CommentController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool;
    public static void print(int index, Object object){
        System.out.println(String.format("%d, %s",index,object.toString()));
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


}
