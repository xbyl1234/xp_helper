package com.hook.okhttp_redirect;

import com.common.units;

import java.nio.charset.StandardCharsets;

public class ResourceCacheTest implements ResourceCacheInterface {

    //查询是否有缓存
    @Override
    public boolean HasCache(CacheId id) throws Throwable {
        return false;
    }

    //下载缓存
    @Override
    public Cache GetCache(CacheId id) throws Throwable {
        return null;
    }

    //上次缓存
    @Override
    public boolean UploadCache(Cache cache, String path) throws Throwable {
        cache.id.path = path;
        units.save_file(path, cache.ToJson().toJSONString().getBytes(StandardCharsets.UTF_8));
        return true;
    }
}
