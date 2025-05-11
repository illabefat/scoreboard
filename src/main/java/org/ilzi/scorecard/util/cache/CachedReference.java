package org.ilzi.scorecard.util.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.function.Supplier;

public final class CachedReference<R> {

    private static final String KEY = "KEY";

    private final LoadingCache<String, R> cache;

    public CachedReference(Supplier<R> supplier) {
        this.cache = Caffeine.newBuilder()
            .build(key -> supplier.get());
    }

    public R get() {
        return cache.get(KEY);
    }

    public void invalidate() {
        cache.invalidate(KEY);
    }
}