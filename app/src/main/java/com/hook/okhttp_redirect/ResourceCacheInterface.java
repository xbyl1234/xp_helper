package com.hook.okhttp_redirect;

public interface ResourceCacheInterface {
    boolean HasCache(CacheId id) throws Throwable;

    //下载缓存
    byte[] DownloadCache(CacheId id) throws Throwable;

    //上次缓存
    boolean UploadCache(CacheId id) throws Throwable;
}