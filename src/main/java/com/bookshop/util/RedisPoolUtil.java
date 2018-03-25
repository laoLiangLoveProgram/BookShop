package com.bookshop.util;/**
 * Created by Administrator on 2018/3/25.
 */

import com.bookshop.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * @program: book_shop
 * @description: RedisPoolUtil
 * @author: LaoLiang
 * @create: 2018-03-25 00:24
 **/
@Slf4j
public class RedisPoolUtil {

    public static String set(String key, String value) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} , value:{} , error:", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String get(String key) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} , error:", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
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
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            log.error("set key:{} , value:{} , error:", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
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
        Jedis jedis = null;
        Boolean result = false;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.expire(key, exTime) == 1L;
        } catch (Exception e) {
            log.error("expire key:{} , error:", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * 删除一个key
     *
     * @param key
     * @return
     */
    public static Boolean del(String key) {
        Jedis jedis = null;
        Boolean result = false;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key) == 1L;
        } catch (Exception e) {
            log.error("del key:{} , error:", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }


}
