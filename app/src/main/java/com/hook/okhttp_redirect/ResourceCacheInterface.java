package com.hook.okhttp_redirect;

public interface ResourceCacheInterface {
    boolean HasCache(CacheId id) throws Throwable;

    //下载缓存
    Cache GetCache(CacheId id) throws Throwable;

    //上次缓存
    boolean UploadCache(Cache id) throws Throwable;
}