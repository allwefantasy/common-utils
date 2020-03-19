package tech.mlsql.test;

import tech.mlsql.common.utils.cache.CacheBuilder;
import tech.mlsql.common.utils.cache.CacheLoader;
import tech.mlsql.common.utils.cache.LoadingCache;

/**
 * 18/3/2020 WilliamZhu(allwefantasy@gmail.com)
 */
public class Main {
    public static void main(String[] args) {
        CacheLoader<String, String> loader = new CacheLoader<String, String>() {
            public String load(String key) {
                return "---";
            }
        };
        LoadingCache<String, String> cache = CacheBuilder.newBuilder().build(loader);
        System.out.println(cache.getUnchecked("jack"));
    }
}

