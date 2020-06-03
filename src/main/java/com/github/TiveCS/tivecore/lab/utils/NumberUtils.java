package com.github.tivecs.tivecore.lab.utils;

import java.util.regex.Pattern;

public class NumberUtils {

    private final static Pattern isAbleToDecimalPattern = Pattern.compile("^-?[0-9]+\\.?[0-9]*"),
            isAbleToIntegerPattern = Pattern.compile("^-?[0-9]+\\.{0}");

    public static long convertToLong(String str){
        long v = 0;
        if (isAbleToInteger(str)){
            Number f = Float.parseFloat(str);
            v = f.longValue();
        }
        return v;
    }
    public static int convertToInt(String str){
        int v = 0;
        if (isAbleToInteger(str)){
            Number f = Float.parseFloat(str);
            v = f.intValue();
        }
        return v;
    }
    public static short convertToShort(String str){
        short v = 0;
        if (isAbleToInteger(str)){
            Number f = Float.parseFloat(str);
            v = f.shortValue();
        }
        return v;
    }
    public static byte convertToByte(String str){
        byte v = 0;
        if (isAbleToInteger(str)){
            Number f = Float.parseFloat(str);
            v = f.byteValue();
        }
        return v;
    }

    public static boolean isAbleToInteger(String str){
        return isAbleToIntegerPattern.matcher(str).matches();
    }
    public static boolean isAbleToDecimal(String str){
        return isAbleToDecimalPattern.matcher(str).matches();
    }

    public static boolean isNonDecimal(Object obj){
        return isLong(obj) || isInteger(obj) || isShort(obj) || isByte(obj);
    }
    public static boolean isDecimal(Object obj){
        return isDouble(obj) || isFloat(obj);
    }

    public static boolean isNumber(Object obj){
        return obj instanceof Number;
    }
    public static boolean isNumber(Class<?> type){
        return type.equals(Number.class) || Number.class.isAssignableFrom(type) || isLong(type) || isInteger(type) || isShort(type) || isByte(type) || isDouble(type) || isFloat(type);
    }

    public static boolean isDouble(Class<?> type){
        return type.equals(Double.class) || type.equals(double.class);
    }
    public static boolean isDouble(Object obj){
        return obj instanceof Double;
    }

    public static boolean isFloat(Class<?> type){
        return type.equals(Float.class) || type.equals(float.class);
    }
    public static boolean isFloat(Object obj){
        return obj instanceof Float;
    }

    public static boolean isLong(Class<?> type){
        return type.equals(Long.class) || type.equals(long.class);
    }
    public static boolean isLong(Object obj){
        return obj instanceof Long;
    }

    public static boolean isInteger(Class<?> type){
        return type.equals(Integer.class) || type.equals(int.class);
    }
    public static boolean isInteger(Object obj){
        return obj instanceof Integer;
    }

    public static boolean isShort(Class<?> type){
        return type.equals(Short.class) || type.equals(short.class);
    }
    public static boolean isShort(Object obj){
        return obj instanceof Short;
    }

    public static boolean isByte(Class<?> type){
        return type.equals(Byte.class) || type.equals(byte.class);
    }
    public static boolean isByte(Object obj){
        return obj instanceof Byte;
    }
}
