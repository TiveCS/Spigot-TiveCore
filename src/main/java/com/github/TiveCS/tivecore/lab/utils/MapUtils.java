package com.github.tivecs.tivecore.lab.utils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapUtils {

    public static List<String> getKeysStartWith(@Nonnull Map<? extends String, ?> map, @Nonnull String str){
        List<String> keys = new ArrayList<>();

        for (String s : map.keySet()){
            if (s.startsWith(str)){
                keys.add(s);
            }
        }

        return keys;
    }

    public static List<String> getKeysEndWith(@Nonnull Map<? extends String, ?> map, @Nonnull String str){
        List<String> keys = new ArrayList<>();

        for (String s : map.keySet()){
            if (s.endsWith(str)){
                keys.add(s);
            }
        }

        return keys;
    }

    public static List<Object> getValuesStartWith(@Nonnull Map<? extends String, ?> map, @Nonnull String str){
        List<Object> values = new ArrayList<>();

        for (String s : map.keySet()){
            if (s.startsWith(str)){
                values.add(map.get(s));
            }
        }

        return values;
    }

    public static List<Object> getValuesEndWith(@Nonnull Map<? extends String, ?> map, @Nonnull String str){
        List<Object> values = new ArrayList<>();

        for (String s : map.keySet()){
            if (s.endsWith(str)){
                values.add(map.get(s));
            }
        }

        return values;
    }

}
