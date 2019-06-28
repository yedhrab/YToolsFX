package services;

import java.util.HashMap;

public abstract class Cache {

    private final static HashMap<String, Object> datas = new HashMap<>();

    public static void update(String key, Object obj) {
        datas.put(key, obj);
    }

    public static void create(String key, Object obj) {
        if (!datas.containsKey(key)) {
            datas.put(key, obj);
        }
    }

    public static Object get(String key, Object defValue) {
        Object result = datas.get(key);
        return result != null ? result : defValue;
    }

    public static Object getConditionally(String keyTrue, String keyFalse) {
        return get(keyTrue, get(keyFalse, null));
    }
}
