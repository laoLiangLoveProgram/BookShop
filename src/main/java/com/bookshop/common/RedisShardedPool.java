package com.bookshop.common;/**
 * Created by Administrator on 2018/3/26.
 */

import com.bookshop.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * 分布式Redis的连接池
 * @program: book_shop
 * @description:
 * @author: LaoLiang
 * @create: 2018-03-26 00:53
 **/
@Slf4j
public class RedisShardedPool {
    private static ShardedJedisPool pool;  //Sharded Jedis连接池
    private static Integer maxTotal = PropertiesUtil.getIntegerProperty("redis.max.total", 20);    //最大连接数
    private static Integer maxIdle = PropertiesUtil.getIntegerProperty("redis.max.idle", 10);     //最大的空闲状态(Idle)的Jedis连接实例的个数
    private static Integer minIdle = PropertiesUtil.getIntegerProperty("redis.min.idle", 2);     //最小的空闲状态的Jedis连接实例的个数

    //向调用者输出“链接”资源时，是否检测有效性, 如果无效则从连接池中移除，并尝试获取继续获取. 默认false即不检测, true则检测, 保证了连接的有效性
    private static Boolean testOnBorrow = PropertiesUtil.getBooleanProperty("redis.test.borrow", true);
    // 向连接池“归还”链接时，是否检测“链接”对象的有效性。默认为false, 即不检测
    private static Boolean testOnReturn = PropertiesUtil.getBooleanProperty("redis.test.return", true);

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = PropertiesUtil.getIntegerProperty("redis1.port");

    //由于新增了分布式的Redis, 所以需要新增一个ip和port
    private static Integer redis2Port = PropertiesUtil.getIntegerProperty("redis2.port");
    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");


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

        JedisShardInfo info1 = new JedisShardInfo(redis1Ip, redis1Port, 1000 * 2);
//        info1.setPassword();
        JedisShardInfo info2 = new JedisShardInfo(redis2Ip, redis2Port, 1000 * 2);

        List<JedisShardInfo> jedisShardInfoList = new ArrayList<>();
        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        //Hashing.MURMUR_HASH 就是consistent hashing 一致性算法
        pool = new ShardedJedisPool(config, jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);

        log.info("RedisShardedPool 分布式连接池已经初始化成功");

    }


    /**
     * 获取连接池
     *
     * @return
     */
    public static ShardedJedis getShardedJedis() {
        return pool.getResource();
    }

    /**
     * 把连接放回连接池
     *
     * @param shardedJedis
     */
    public static void returnResource(ShardedJedis shardedJedis) {
        pool.returnResource(shardedJedis);
    }

    public static void returnBrokenResource(ShardedJedis shardedJedis) {
        pool.returnBrokenResource(shardedJedis);
    }

    public static void main(String[] args) {
        ShardedJedis shardedJedis = RedisShardedPool.getShardedJedis();

        for (int i = 0; i < 100; i++) {
            shardedJedis.set("key"+i, "value"+i);
        }


        RedisShardedPool.returnResource(shardedJedis);

        log.info("main  end ------------------");

    }


}
