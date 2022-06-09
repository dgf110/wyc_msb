package com.online.taxi.servicepassengeruser.common;


import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class CacheUtil {

    private CacheUtil() {
    }

    private static CacheManager cacheManager = SpringContextHolder.getBean(CacheManager.class);

    private static Cache passengerCache;

    public static Cache passengerCache() {
        if (passengerCache == null) {
            passengerCache = initCache(Constant.CACHE_KEY_PASSENGER);
        }
        return passengerCache;
    }

    private static Cache initCache(String key) {
        return cacheManager.getCache(key);
    }

}
