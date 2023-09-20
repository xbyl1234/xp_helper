package com.hook;

import com.alibaba.fastjson.JSONObject;

public class ResourceCacheTest extends ResourceCache {

    //查询是否有缓存
    public boolean HasCache(CacheId id) throws Throwable {
    }

    //下载缓存
    public byte[] DownloadCache(CacheId id) throws Throwable {
    }

    //上次缓存
    public boolean UploadCache(CacheId id, byte[] data) throws Throwable {

    }
}
