package com.bookshop.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/5/15.
 */
@Slf4j
public class TokenCache {

    //    private static Logger log = LoggerFactory.getLogger(TokenCache.class);
    public static final String TOKEN_PREFIX = "token_";

    //guava中的本地缓存，可以设置过期策略
    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder()
            //设置缓存的初始化容量
            .initialCapacity(1000)
            //当缓存的容量超过最大容量时，使用LRU算法(最少使用算法)来移除缓存项
            .maximumSize(10000)
            //设置缓存的有效期12小时
            .expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，就调用这个方法进行加载
                @Override
                public String load(String s) throws Exception {
                    return "null";  //为了避免.equals方法的空指针异常，返回"null"字符串
                }
            });

    public static void setKey(String key, String value){
        localCache.put(key, value);
    }

    public static String getKey(String key){
        String value = null;
        try{
            value = localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            log.error("localCache get error", e);
            return null;
        }
    }
}
