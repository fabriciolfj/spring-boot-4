package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final CacheManager cacheManager;

    public UserService(@Qualifier("userCacheManager") CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void create(final User user) {
        var cache = cacheManager.getCache("users");
        if (cache != null) {
            cache.put(user.getId(), user);
        }
    }

    public Optional<User> getUser(final Long id) {
        var cache = cacheManager.getCache("users");
        if (cache != null) {
            return getWrapperUser(id, cache);
        }

        return Optional.empty();
    }

    public Optional<User> getWrapperUser(final Long id, Cache cache) {
        final Cache.ValueWrapper wrapper = cache.get(id);
        if (wrapper != null) {
            return Optional.ofNullable((User) wrapper.get());
        }

        return Optional.empty();
    }
}
