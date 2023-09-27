package com.hook.okhttp_redirect;

public class ResourceCacheTest implements ResourceCacheInterface {

    //查询是否有缓存
    @Override
    public boolean HasCache(CacheId id) throws Throwable {
        return true;
    }

    //下载缓存
    @Override
    public byte[] DownloadCache(CacheId id) throws Throwable {
        return null;
    }

    //上次缓存
    @Override
    public boolean UploadCache(CacheId id) throws Throwable {
        return true;
    }
}
