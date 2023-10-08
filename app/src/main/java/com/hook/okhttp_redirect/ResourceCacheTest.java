package com.hook.okhttp_redirect;

import com.common.units;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ResourceCacheTest implements ResourceCacheInterface {

    //查询是否有缓存
    @Override
    public boolean HasCache(CacheId id) throws Throwable {
        return false;
    }

    //下载缓存
    @Override
    public Cache GetCache(CacheId id) throws Throwable {
        String data = "{\n" +
                "    \"headers\": [\"Content-Type\", \"text/html\", \"Server\", \"bfe\", \"Date\", \"Sat, 07 Oct 2023 07:56:04 GMT\", \"X-Android-Sent-Millis\", \"1650623858271\", \"X-Android-Received-Millis\", \"1650623858312\"],\n" +
                "    \"body\": \"MTIz\",\n" +
                "    \"url\": \"https://www.baidu.com/\"\n" +
                "}";
        return new Cache(id, data.getBytes());
    }

    //上次缓存
    @Override
    public boolean UploadCache(Cache cache, String path) throws Throwable {
        cache.id.path = path;
        units.save_file(path, cache.ToJson().toJSONString().getBytes(StandardCharsets.UTF_8));
        return true;
    }
}
