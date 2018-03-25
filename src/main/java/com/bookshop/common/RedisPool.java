package com.bookshop.common;/**
 * Created by Administrator on 2018/3/24.
 */

import com.bookshop.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @program: book_shop
 * @description: Redis连接池
 * @author: LaoLiang
 * @create: 2018-03-24 23:02
 **/
public class RedisPool {
    private static JedisPool pool;  //Jedis连接池
    private static Integer maxTotal = PropertiesUtil.getIntegerProperty("redis.max.total", 20);    //最大连接数
    private static Integer maxIdle = PropertiesUtil.getIntegerProperty("redis.max.idle", 10);     //最大的空闲状态(Idle)的Jedis连接实例的个数
    private static Integer minIdle = PropertiesUtil.getIntegerProperty("redis.min.idle", 2);     //最小的空闲状态的Jedis连接实例的个数

    //向调用者输出“链接”资源时，是否检测有效性, 如果无效则从连接池中移除，并尝试获取继续获取. 默认false即不检测, true则检测, 保证了连接的有效性
    private static Boolean testOnBorrow = PropertiesUtil.getBooleanProperty("redis.test.borrow", true);
    // 向连接池“归还”链接时，是否检测“链接”对象的有效性。默认为false, 即不检测
    private static Boolean testOnReturn = PropertiesUtil.getBooleanProperty("redis.test.return", true);

    private static String redisIp = PropertiesUtil.getProperty("redis.ip");
    private static Integer redisPort = PropertiesUtil.getIntegerProperty("redis.port");


    static {
        initPool();
    }

    /**
     * 初始化连接池
     */
    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        //连接耗尽是否阻塞, 默认为true, false代表会抛出异常, true代表阻塞到超时, 超时会抛超时异常
        config.setBlockWhenExhausted(true);

        pool = new JedisPool(config, redisIp, redisPort, 1000 * 2);

    }


    /**
     * 获取连接池
     *
     * @return
     */
    public static Jedis getJedis() {
        return pool.getResource();
    }

    /**
     * 把连接放回连接池
     *
     * @param jedis
     */
    public static void returnResource(Jedis jedis) {
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(Jedis jedis) {
        pool.returnBrokenResource(jedis);
    }


    public static void main(String[] args) {
        Jedis jedis = RedisPool.getJedis();
        String set = jedis.set("laoliangkey", "laoliangValue");

        RedisPool.returnResource(jedis);

        pool.destroy(); //临时调用, 销毁连接池中的所以连接, 不建议使用
        System.out.println("program is end");
    }
}
