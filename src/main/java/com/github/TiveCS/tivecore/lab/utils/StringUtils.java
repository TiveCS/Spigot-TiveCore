package com.github.tivecs.tivecore.lab.utils;

import org.bukkit.ChatColor;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StringUtils {

    public static String decimalFormat(final double input, final int length){
        StringBuilder f;
        if (length >= 1){
            f = new StringBuilder("#.");
            for (int i = 0; i < length; i++){
                f.append("#");
            }
        }else{
            f = new StringBuilder("#");
        }
        DecimalFormat decimalFormat = new DecimalFormat(f.toString());

        return decimalFormat.format(input);
    }

    public static String capitalize(final String text, final String separator){
        String[] s = text.split(separator);
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length; i++){
            String m = s[i];
            b.append(m.substring(0, 1).toUpperCase()).append(m.substring(1));
            if (i + 1 < s.length){
                b.append(separator);
            }
        }
        return b.toString();
    }

    public static List<String> colored(List<String> l){
        for (int i = 0; i < l.size(); i++){
            l.set(i, ChatColor.translateAlternateColorCodes('&', l.get(i)));
        }
        return l;
    }

    public static List<String> insertString(List<String> data, int index, List<String> value){
        List<String> afterIndex = new ArrayList<>();
        List<String> result = new ArrayList<>();

        for (int i = index + 1; i < data.size(); i++){
            afterIndex.add(data.get(i));
        }

        for (int i = 0; i <= index; i++){
            result.add(data.get(i));
        }

        result.addAll(value);
        result.addAll(afterIndex);

        return result;
    }

    public static List<Integer> searchNormalCharLocationByIndexing(final String data, char search){
        List<Integer> l = new ArrayList<>();

        final int length = data.length();
        int pos1 = -1, pos2 = -1, currentIndex = 0;
        boolean stillHave = false, isNew = true;

        do{
            if (isNew) {
                pos1 = data.indexOf(search, currentIndex);

                if (pos1 != -1) {
                    l.add(pos1);
                    currentIndex = pos1 + 1;

                    stillHave = true;
                    isNew = false;
                }else{
                    break;
                }
            }else{
                pos2 = data.indexOf(search, currentIndex);

                if (pos2 != -1) {
                    l.add(pos2);
                    currentIndex = pos2 + 1;

                    stillHave = pos2 != -1;
                    isNew = true;
                }else{
                    break;
                }
            }

        }while(stillHave);

        return l;
    }

    public static List<Integer> searchNormalCharLocation(final String data, char search){
        List<Integer> l = new ArrayList<>();

        for (int i = 0; i < data.length();i++){
            if (data.charAt(i) == search){
                l.add(i);
            }
        }

        return l;
    }

    public static HashMap<Integer, List<Integer>> searchNormalCharLocation(final List<String> data, char search){
        HashMap<Integer, List<Integer>> r = new HashMap<>();

        for (int a = 0; a < data.size(); a++) {
            final String text = data.get(a);
            List<Integer> l = new ArrayList<>();

            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == search) {
                    l.add(i);
                }
            }

            r.put(a, l);
        }

        return r;
    }

    public static List<Integer> searchBigCharLocation(final String data, char search){
        List<Integer> l = new ArrayList<>();

        try {
            final Field field = String.class.getDeclaredField("value");
            field.setAccessible(true);
            try {
                final char[] chars = (char[]) field.get(data);
                final int len = chars.length;
                for (int i = 0; i < len; i++) {
                    if (chars[i] == search) {
                        l.add(i);
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return l;
    }

    public static int countNormalChar(final String data, char search){
        int count = 0;
        for (int i = 0; i < data.length(); i++){
            if (data.charAt(i) == search){
                count++;
            }
        }

        return count;
    }

    public static int countBigChar(final String data, char search){
        int count = 0;

        try {
            final Field field = String.class.getDeclaredField("value");
            field.setAccessible(true);
            try {
                final char[] chars = (char[]) field.get(data);
                final int len = chars.length;
                for (int i = 0; i < len; i++) {
                    if (chars[i] == search) {
                        count++;
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return count;
    }

}
