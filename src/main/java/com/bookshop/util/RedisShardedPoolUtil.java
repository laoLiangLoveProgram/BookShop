package com.bookshop.util;/**
 * Created by Administrator on 2018/3/25.
 */

import com.bookshop.common.RedisPool;
import com.bookshop.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

/**
 * RedisShardedPool的工具类
 * @program: book_shop
 * @description: RedisShardedPoolUtil
 * @author: LaoLiang
 * @create: 2018-03-25 00:24
 **/
@Slf4j
public class RedisShardedPoolUtil {

    public static String set(String key, String value) {
        ShardedJedis shardedJedis = null;
        String result = null;

        try {
            shardedJedis = RedisShardedPool.getShardedJedis();
            result = shardedJedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} , value:{} , error:", key, value, e);
            RedisShardedPool.returnBrokenResource(shardedJedis);
            return result;
        }
        RedisShardedPool.returnResource(shardedJedis);
        return result;
    }

    public static String get(String key) {
        ShardedJedis shardedJedis = null;
        String result = null;

        try {
            shardedJedis = RedisShardedPool.getShardedJedis();
            result = shardedJedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} , error:", key, e);
            RedisShardedPool.returnBrokenResource(shardedJedis);
            return result;
        }
        RedisShardedPool.returnResource(shardedJedis);
        return result;
    }

    /**
     * 设置有剩余时间的key-value, 单位是秒
     *
     * @param key
     * @param value
     * @param exTime
     * @return
     */
    public static String setEx(String key, String value, int exTime) {
        ShardedJedis shardedJedis = null;
        String result = null;

        try {
            shardedJedis = RedisShardedPool.getShardedJedis();
            result = shardedJedis.setex(key, exTime, value);
        } catch (Exception e) {
            log.error("set key:{} , value:{} , error:", key, value, e);
            RedisShardedPool.returnBrokenResource(shardedJedis);
            return result;
        }
        RedisShardedPool.returnResource(shardedJedis);
        return result;
    }

    /**
     * 重新设置key的有效期
     *
     * @param key
     * @param exTime
     * @return
     */
    public static Boolean expire(String key, int exTime) {
        ShardedJedis shardedJedis = null;
        Boolean result = false;

        try {
            shardedJedis = RedisShardedPool.getShardedJedis();
            result = shardedJedis.expire(key, exTime) == 1L;
        } catch (Exception e) {
            log.error("expire key:{} , error:", key, e);
            RedisShardedPool.returnBrokenResource(shardedJedis);
            return result;
        }
        RedisShardedPool.returnResource(shardedJedis);
        return result;
    }

    /**
     * 删除一个key
     *
     * @param key
     * @return
     */
    public static Boolean del(String key) {
        ShardedJedis shardedJedis = null;
        Boolean result = false;

        try {
            shardedJedis = RedisShardedPool.getShardedJedis();
            result = shardedJedis.del(key) == 1L;
        } catch (Exception e) {
            log.error("del key:{} , error:", key, e);
            RedisShardedPool.returnBrokenResource(shardedJedis);
            return result;
        }
        RedisShardedPool.returnResource(shardedJedis);
        return result;
    }


}
